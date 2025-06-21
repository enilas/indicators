(ns indicators.component.elasticsearch-component
  (:require [clj-http.conn-mgr :refer [make-reusable-conn-manager]]
            [clojurewerkz.elastisch.rest :as elasticsearch]
            [com.stuartsierra.component :as component]))

; Work in progress! Not currently implemented

(defrecord ElasticsearchComponent [host port elasticsearch-connection]
  component/Lifecycle

  (start [component]
    (if (nil? elasticsearch-connection)
      (do
        (println "Starting elasticsearch component")
        (let [elasticsearch-url (format "http://%s:%s" host port)
              connection (elasticsearch/connect elasticsearch-url
                                                {:connection-manager (make-reusable-conn-manager {:timeout 10})
                                                 :throw-exceptions? false})]
          (assoc component
                 :elasticsearch-connection connection)))
      (do
        (println "Elasticsearch component already started")
        component)))

  (stop [this]
    (if elasticsearch-connection
      (do
        (println "Stopping elasticsearch component")
        (assoc this
               :elasticsearch-connection nil))
      (do
        (println "Elasticsearch component is nil")
        this))))

(defn new-elasticsearch-component
  [config]
  (map->ElasticsearchComponent {:config config}))