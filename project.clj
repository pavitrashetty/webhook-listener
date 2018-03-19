(defproject webhook-listener "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0" :exclusions [org.clojure/clojure
                                                      clj-time
                                                      commons-codec
                                                      commons-io]]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-http "2.3.0"]
                 [cheshire "5.6.3"]
                 [compojure "1.3.4" :exclusions [org.clojure/clojure
                                                 ring/ring-core]]]
  :main ^:skip-aot webhook-listener.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
