# WT Samples #

Like the [snippets](WTSnippets.md), the samples provide jumping off points for exploring UI testing using the WindowTester runtime API.  While the snippets strive to be small and stand-alone, samples tend to be bigger and come with more dependencies.

Like the snippets, samples are a great way to isolate problems and limitations in the current window tester API.  In cases where a snippet is too restrictive, samples are a handy way to communicate.  If you would like to report a bug or feature request this way, attach your sample to a bug report sent to [WT Support](mailto:wintest-support@instantiations.com).

In some cases, you will want to increase the heap size of your runtime workspace in the snippet's launch configuration.  If you run into heap issues, you might try specifying VM arguments like these:

```
-Xms256m
-Xmx512m
```



---


## GMF ##

  * [Driving the GMF Taipan example](http://code.google.com/p/wt-commons/source/browse/#svn/trunk/org.wtc.samples.gmf.taipan)