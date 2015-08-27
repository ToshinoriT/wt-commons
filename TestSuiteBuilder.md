# Introduction to TestSuiteBuilder #

TestSuiteBuilder is a way to use Eclipse's plugin extensions to build JUnit test suites declaratively in an XML file. It is a class defined in the org.wtc.eclipse.core Helper Library plugin. See [TestSuiteBuilder](http://wt-commons.googlecode.com/svn/docs/html/reference/javadoc/helper-libs/org/wtc/eclipse/core/tsb/TestSuiteBuilder.html), though this class should only be referred to in JUnit test runner arguments and not programatically (this class is not meant to be subclassed).

Why build test suites programatically? For large Eclipse projects, tests are often split out by plugin but should be part of the same JUnit test suite. As an example, let's assume I have two teams working on components for the same Eclipse feature. In this scenario, the teams each work within their own modules in source, each with their own production plugin and accompanying test plugin:
```
project/
   moduleA/
      src/
          plugins/
              project.modulea/
      test/
          plugins/
              project.modulea.test/
   moduleB/
      src/
          plugins/
              project.moduleb/
      test/
          plugins/
              project.moduleb.test/
```

The teams chose to split the production code and test code into separate plugins to make sure that the resources delivered to customers contained only the required dependencies for the project (for example, the production plugins should not depend on Window Tester test harness plugins).

The team decides that they will run automated checkin tests before each checkin. More specifically, the automated tests to be run will reflect the tets for the modules. For example, if a change is made to module A, then the module A checkin tests will be run.

But if module B depends on module A, then module B is a downstream dependency on module A. The teams decide that, in addition to the API tests module A might run on checkin, a subset of the module B checkin tests will be run as integration tests for each checkin. Since A cannot depend on B (which would introduce a dependency cycle), how will the module B tests be added to the module A checkin test suite? There are a few options:
  1. Build a test suite in B that includes tests from A. This is undesirable because a) now the module A checkin test suite must be maintained outside of module B and b) this solution doesn't scale as the number of modules grows larger.
  1. Build a common test plugin that contains one, giant, test suite. This solution is undesirable because this all-containing test plugin must depend on every plugin in order to run the tests. In that case, module A's developers would then have to import module B to get the test plugin to compile and modularity is lost.
  1. Have everyone run all of the tests, all the time. But what about extended performance tests? Will a developer really want to run a 5-day uptime and memory test for each check in? No...
  1. Use Eclipse's plugin loaders and extension points to your advantage. Create a way to flag certain tests with keys and run a test that builds a test suite from tests marked with those keywords. This is the TestSuiteBuilder.

# TestSuiteBuilder Components #

There are three parts to TestSuiteBuidler:
  1. **The org.wtc.eclipse.core.tsb.TestSuiteBuilder Class**: The TestSuitebuilder class extends junit.framework.TestCase and so is run directly when running tests. This class is not meant to be subclassed. The tests added to its suite do not have any requirements over the requirements for building JUnit tests (TestCases, TestSuites, etc), though when run with the Helper Libraries, it is best to extend [org.wtc.eclipse.platform.tests.EclipseUITest](http://wt-commons.googlecode.com/svn/docs/html/reference/javadoc/helper-libs/org/wtc/eclipse/platform/tests/EclipseUITest.html) and [org.wtc.eclipse.core.tests.ManagedTestSuite](http://wt-commons.googlecode.com/svn/docs/html/reference/javadoc/helper-libs/org/wtc/eclipse/core/tests/ManagedTestSuite.html), respectively.
  1. **The org.wtc.eclipse.core.testSuiteBuilder Extension Point**: This extension point is declares test registry files for a plugin and is loaded by the TestSuiteBuidler class at runtime. A DTD for this extension is shown below.  NOTE: testSuiteBuilder/registry/@path is a plugin-relative path to a test registry XML file. See the testSuiteBuidler extension point [source](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.core/schema/testSuiteBuilder.exsd) for more details.
  1. **The Test Registry XML File**: This file declares the test classes that a plugin can contribute and the modules, or keywords, that those tests are associated with. A DTD is shown below. NOTE: test-project/test/@class is the fully-qualified class name of a JUnit test to run. This class may be a TestCase or TestSuite (TestDecorators are not supported at this time). The test-project/test/module value is any alpha-numeric string. See [testProject.xsd](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.core/schema/testproject.xsd) for more information.

### DTD For org.wtc.eclipse.core.testSuiteBuilder Extension Point ###
```
<!ELEMENT testSuiteBuilder (registry)+ >
<!ELEMENT registry >
<!ATTLIST registry
    path CDATA #REQURIED 
>
```

### DTD Test Registry XML File ###
```
<!ELEMENT test-project (test)* >
<!ELEMENT test (module)+ >
<!ATTLIST test
   class CDATA #REQUIRED
   owner CDATA #IMPLIED
>
<!ELEMENT module #PCDATA>
```

## Example of Test Suite Builder Set Up ##
The [org.wtc.eclipse.platform project](http://code.google.com/p/wt-commons/source/browse/#svn/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform) uses TestSuiteBuilder to build its test suites. Let's look at an example:
  1. First, the plugin manifest adds a dependency on the org.wtc.eclipse.core plugin: [SOURCE](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform/META-INF/MANIFEST.MF#22)
  1. Next, the plugin creates a series of test case classes extending EclipseUITestCase: [SOURCE](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform/src/org/wtc/eclipse/platform/tests/helpers)
  1. Third, the plugin registers these tests in a test registry XML file: [SOURCE](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform/testregistry.xml)
  1. Fourth, the plugin implements the testSuiteBuidler extension point in its plugin.xml to point to the test registry XML file in its root directory: [SOURCE](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform/plugin.xml#10)
  1. Finally, don't forget to export the test registry file with builds and exports of the test plugin: [SOURCE](http://code.google.com/p/wt-commons/source/browse/trunk/helper-libs/eclipse33/plugins/org.wtc.eclipse.platform/build.properties#8)

=Special Test Registry Modules+
There are certain reserved modules names that will keep a test from running when marked with that module. The purpose of these modules is to disable a test from all TestSuiteBuilder suites in the test registry file. They are:
| **MODULE** | **DESCRIPTION** |
|:-----------|:----------------|
| failure    | Disable a test. Do not run that test under any conditions |
| nolinux    | Disable a test on Linux operating systems only |

# TestSuiteBuilder Runtime Options #
TestSuiteBuilder offers different options for running tests, options that are passed as system properties at test runtime.
| **OPTION**    | **TYPE** | **DESCRIPTION** |
|:--------------|:---------|:----------------|
| tsb.modules   | Comma-separated list of module names | Run the tests that have at least one of these modules declared for that test |
| tsb.tests     | Comma-separated list of fully-qualified or short names of tests | Tests to run    |
| tsb.plugins   | Comma-separated list of plugin IDs | Only run tests in these plugins and that match tsb.modules and/or tsb.tests criteria |
| tsb.loops     | Integer  | Loop the build test suite N number of times |

## Examples ##
For the following test registry file:
```
<test-project>
   <test class="project.modulea.Test1">
      <module>checkinA</module>
      <module>checkinB</module>
   </test>
   <test class="project.modulea.Test2">
      <module>checkinA</module>
      <module>checkinC</module>
   </test>
   <test class="project.modulea.subpack.Test1">
      <module>checkinD</module>
   </test>
   <test class="project.modulea.Test3">
      <module>checkinA</module>
      <module>checkinE</module>
      <module>failure</module>
   </test>
</test-project>
```

| **SYSTEM PROPERTIES** | **TESTS RUN** |
|:----------------------|:--------------|
| -Dtsb.modules=checkinA | project.modulea.Test1, project.modulea.Test2 |
| -Dtsb.modules=checkinA,checkinD | project.modulea.Test1, project.modulea.Test2, project.modulea.subpack.Test1 |
| -Dtsb.tests=project.modulea.Test1 | project.modulea.Test1 |
| -Dtsb.tests=Test1     | project.modulea.Test1, project.modulea.subpack.Test1 |
| -Dtsb.modules=checkinA -Dtsb.plugins=bogus.plugin.id | {nothing run} |
| -Dtsb.modules=checkinE | {nothing run} |

# Running TestSuiteBuilder Tests In Eclipse #
To run TestSuiteBuilder in an Eclipse environment (instruction written using Eclipse 3.3):
  1. Import the org.wtc.eclipse.core plugin into your workspace as a binary plugin using **File** -> **Import...** -> **Plug-in Development** / **Plug-ins and Features**
  1. Open the run configurations dialog with **Run** -> **Open Run Dialog...**
  1. Right-click on the **JUnit Plug-in Test** item and select **New...**
  1. Check the **Run a single test** radio button
  1. On the **Test** tab, set the **Project:** text field to _org.wtc.eclipse.core_
  1. On the **Test** tab, set the **Test class:** text field to _org.wtc.eclipse.core.tsb.TestSuiteBuilder_
  1. On the **Arguments** tab, add the TestSuiteBuilder arguments into the **VM Arguments** text are as system properties (See examples above)
  1. Click **Apply** to save the run configuration and click the **Run** button.
  1. If your test is not running as expected, enable tracing on the run configuration and enable the **logging/testsuitemodel** debug option from the **Tracing** tab. At runtime, the TestSuiteBuilder will show a listing of exactly why each test was or was not loaded into the suite to execute.