(defproject org.clojars.fdiotalevi/file-kit "0.2.0-SNAPSHOT"
  :warn-on-reflection true
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :java-source-paths ["src-java"]
  :main file-kit.core
  :plugins [[lein-marginalia "0.7.1"]]
  :dependencies
    [[org.clojure/clojure "1.5.1"]
     [commons-io/commons-io "2.4"]])
