# WT Snippets #

In the spirit of the [SWT Snippets](http://www.eclipse.org/swt/snippets/), the WT Snippets are minimal (and mostly) stand-alone tests (or drivers) that demonstrate specific techniques or functionality. Often a small example is the easiest way to understand how to use a particular feature.  For the most part, these are not _tests_ in the strictest sense since they often will not seek to verify anything.  Instead, these are building blocks for helper functions and tests.  If you are looking for longer examples, there is a separate [WT Samples](WTSamples.md) area.

In addition to demonstrating functionality, snippets also help isolate problems and limitations in the current window tester API. The best way to report a bug is to write your own snippet showing the problem and paste it into a bug report sent to [WT Support](mailto:wintest-support@instantiations.com).  Snippets should strive to be as stand-alone as possible.  If the need arises we will factor out a small set of snippet-building helpers.

To run a snippet, create a new plug-in project with the the WT libraries in its dependencies, copy the desired snippet to the clipboard, and paste it into a new snippet class.   (Alternatively, just check out the entire snippets project from [SVN](http://code.google.com/p/wt-commons/source/checkout)).  Run by selecting the class and then selecting "Run > Run As > JUnit Plug-in Test".

In some cases, you will want to increase the heap size of your runtime workspace in the snippet's launch configuration.  If you run into heap issues, you might try specifying VM arguments like these:

```
-Xms256m
-Xmx512m
```

To contribute a new snippet, create a snippet contribution report in our [issue tracker](http://code.google.com/p/wt-commons/issues/entry).  Thanks in advance for your input and contributions!


---


## Eclipse ##

  * [Collecting and accessing problems in the Eclipse Problems View.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet001CollectProblems.java)

  * [Interacting (dumbly) with an Excel spreadsheet embedded in an OLEFrame (win32).](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet002OLE.java)

  * [Verifying workbench settings in the Eclipse preference page.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet003PreferencePageAsserts.java)

  * [Verifying the presence (and absence) of items in the Eclipse Navigator view.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet004VerifyTreeContents.java)

  * [Verifying context help content for the "General/Content Types" Eclipse preference page.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet005VerifyDynamicHelpContents.java)

  * [A sample custom Browser Widget Locator.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet006CustomBrowserLocator.java)

  * [Interacting with an embedded Swing/AWT component.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet007EmbeddedSwing.java)


  * [Accessing Console view text contents.](http://code.google.com/p/wt-commons/source/browse/trunk/org.wtc.snippets/src/org/wtc/snippets/eclipse/Snippet008ConsoleViewHasTextAssertion.java)