goog.addDependency("base.js", ['goog'], []);
goog.addDependency("../cljs/core.js", ['cljs.core'], ['goog.string', 'goog.array', 'goog.object', 'goog.string.StringBuffer']);
goog.addDependency("../pub_app/data.js", ['pub_app.data'], ['cljs.core', 'goog.ui.IdGenerator']);
goog.addDependency("../om/dom.js", ['om.dom'], ['cljs.core']);
goog.addDependency("../om/core.js", ['om.core'], ['cljs.core', 'om.dom']);
goog.addDependency("../pub_app/utils.js", ['pub_app.utils'], ['cljs.core', 'om.core']);
goog.addDependency("../clojure/string.js", ['clojure.string'], ['cljs.core', 'goog.string', 'goog.string.StringBuffer']);
goog.addDependency("../pub_app/core.js", ['pub_app.core'], ['cljs.core', 'pub_app.data', 'om.core', 'clojure.string', 'om.dom', 'pub_app.utils']);