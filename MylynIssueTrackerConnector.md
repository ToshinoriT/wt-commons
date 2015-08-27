### API ###

Google-code does not as of yet (05/2009) have an [API for the issue tracker](http://code.google.com/p/support/issues/detail?id=148).


### Web Connector ###

Until it is implemented, the best bet is to use the generic web connector as described in [this blog entry](http://www.jroller.com/alexRuiz/entry/using_mylyn_with_google_code1).

Example repository properties are as follows:

  * **Server:** http://code.google.com/p/wt-commons/issues
  * **Label:**  wt-commons
  * **Query Request URL:** `${serverUrl}/csv?can=1&colspec=ID+Status+Type+Owner+Summary`
  * **Query Pattern:**     `"({Id}[0-9]+?)","({Status}.+?)","({Type}.+?)","({Owner}.+?)","({Description}.+?)"\s`

#### Refined Searches ####

Tweaking search parameters is handy if you want to refine/filter your search.  Look at to this [blog entry](http://alblue.blogspot.com/2009/04/google-code-and-mylyn-redux.html) to see some of the the secrets of google [search operators](http://code.google.com/p/objectiveclipse/issues/searchtips) revealed.