(defproject vann "0.1.0-SNAPSHOT"
  :description "Pokerbot Vann"
  :url "https://github.com/dedeibel/seibert-poker-bot-vann"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [aleph "0.4.1-beta2"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/clojurescript "1.7.228"]]
  :jvm-opts ["-XX:+UseConcMarkSweepGC"]
  :source-paths ["src-common" "src-clj"]
  :main bpeter.vann.core
  :aot [bpeter.vann.core]
  :plugins [[lein-cljsbuild "1.1.2"]
            [lein-resource "15.10.2"]]
  :hooks [leiningen.resource
          leiningen.cljsbuild]
  :cljsbuild {:builds
              [{:id "vann"
                :source-paths ["src-common" "src-cljs"]
                :compiler {
                           :main "bpeter.vann.core"
                           :output-to "target-js/vann.js"
                           :output-dir "target-js/vann"
                           :asset-path "./vann"
                           :optimizations :simple
                           :source-map "target-js/vann.js.map"
                           :pretty-print false
                           ;:optimizations :advanced
                           ;:source-map true
                           ;:pretty-print true
                           }}]}
  :resource {
             :resource-paths [ ["resources-cljs" {
                                                  :target-path "target-js" } ] ]
             :silent false
             :verbose false}
  :profiles { }
  :clean-targets ^{:protect false} [
                                    "target-js/" 
                                    :target-path]
  )
