(defproject indicators "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [clj-http "3.13.0"]
                 [cheshire "6.0.0"]
                 [clojurewerkz/elastisch "5.0.0-beta1"]
                 [com.cnuernber/charred "1.037"]
                 [com.stuartsierra/component "1.1.0"]
                 [com.stuartsierra/component.repl "0.2.0"]
                 [io.pedestal/pedestal.http-kit "0.8.0-beta-1"]
                 [org.clojure/clojure "1.11.1"]
                 [org.slf4j/slf4j-simple "2.0.17"]
                 [prismatic/schema "1.4.1"]]
  :resource-paths ["resources"]
  :main ^:skip-aot indicators.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
