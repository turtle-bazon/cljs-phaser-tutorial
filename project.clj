(defproject memory-game "0.1.0-SNAPSHOT"
  :description "Phaser tutorial in ClojureScript"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [phzr "0.1.0-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.1.2"]]
  :cljsbuild {:builds
              [{:source-paths ["src"]
                :compiler {:output-to "resources/public/js/main.js"
                           :optimizations :whitespace
                           :pretty-print true}}]}
  :min-lein-version "2.1.2")
