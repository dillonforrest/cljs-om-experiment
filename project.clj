(defproject pub-app "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [secretary "0.4.0"]
                 [om "0.1.7"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "pub-app"
              :source-paths ["src"]
              :compiler {
                :output-to "pub_app.js"
                :output-dir "out"
                :optimizations :none
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]
                :source-map true}}]})
