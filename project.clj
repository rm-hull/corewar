(defproject rm-hull/corewar "0.0.1-SNAPSHOT"
  :description "A small Clojure/ClojureScript library for creating colour swatches"
  :url "https://github.com/rm-hull/corewar"
  :license {
    :name "The MIT License (MIT)"
    :url "http://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [org.clojure/clojurescript "0.0-2280"]
    [org.clojure/core.async "0.1.303.0-886421-alpha"]
    [rm-hull/cljs-test "0.0.8-SNAPSHOT"]]
  :scm {:url "git@github.com:rm-hull/corewar.git"}
  :plugins [
    [codox "0.8.10"]
    [lein-cljsbuild "1.0.3"]
    [com.birdseye-sw/lein-dalap "0.1.1"]]
  :hooks [
    leiningen.dalap
    ;leiningen.cljsbuild
    ]
  :source-paths ["src"]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.5.9"]]}}
  :cljsbuild {
    :repl-listen-port 9000
    :repl-launch-commands
      {"firefox" ["firefox"]}
    :test-commands  {"phantomjs"  ["phantomjs" "target/unit-test.js"]}
    :builds {
      :main {
        :source-paths ["target/generated-src"]
        :jar true
        :compiler {
          :output-to "target/corewar.js"
          :source-map "target/corewar.map"
          :static-fns true
          ;:optimizations :advanced
          :pretty-print true }}
      :test {
        :source-paths ["target/generated-src" "test"]
        :incremental? true
        :compiler {
          :output-to "target/unit-test.js"
          :source-map "target/unit-test.map"
          :static-fns true
          :optimizations :whitespace
          :pretty-print true }}}}
  :codox {
    :sources ["src"]
    :output-dir "doc/api"
    :src-dir-uri "http://github.com/rm-hull/corewar/blob/master/"
    :src-linenum-anchor-prefix "L" }
  :min-lein-version "2.4.2"
  :global-vars {*warn-on-reflection* true})
