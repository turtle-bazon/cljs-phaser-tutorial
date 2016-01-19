(defproject phaser-tutorial "0.1.0-SNAPSHOT"
  :description "Phaser tutorial in ClojureScript"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [phzr "0.1.0-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-3"]]
  :source-paths ["src"]
  :profiles {:dev {:dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                  [org.clojure/tools.reader "0.10.0"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.0-3"]]
                   :plugins [[cider/cider-nrepl "0.10.0"]
                             [refactor-nrepl "1.2.0"]]}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild {:builds
              [{:id "dev"
		:figwheel {:websocket-host :js-client-host}
                :source-paths ["src"]
                :compiler {:main phaser-tutorial.core
                           :asset-path "js/out"
                           :output-to "resources/public/js/main.js"
                           :source-map-timestamp true}}
               {:id "prod"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/main.js"
                           :main phaser-tutorial.core
                           :optimizations :advanced
                           :pretty-print false}}]}
  :min-lein-version "2.1.2")
