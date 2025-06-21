(ns indicators.core
  (:require [com.stuartsierra.component :as component]
            [indicators.config :as config]
            ; [indicators.component.elasticsearch-component :as elasticsearch-component]
            [indicators.component.in-memory-state-component :as in-memory-state-component]
            [indicators.component.pedestal-component :as pedestal-component])
  (:gen-class))

(defn indicators-system
  [config]
  (component/system-map
   :in-memory-state-component (in-memory-state-component/new-in-memory-state-component
                               config)
   ; :elasticsearch-component   (elasticsearch-component/new-elasticsearch-component config)
   :pedestal-component        (component/using
                               (pedestal-component/new-pedestal-component
                                config)
                               [:in-memory-state-component])))

(defn -main
  "Starts the API system"
  []
  (let [system (-> (config/read-config)
                   (indicators-system)
                   (component/start-system))]
    (println "Starting Indicators API Service with config")
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(component/stop-system system)))))
