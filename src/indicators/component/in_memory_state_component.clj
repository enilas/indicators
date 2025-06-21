(ns indicators.component.in-memory-state-component
  (:require [charred.api :as charred]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]))

(def database (-> "indicators.json"
                  (io/resource)
                  (slurp)
                  (charred/read-json :key-fn keyword)))

(def example-indicators
  [{:description "",
    :tags ["suricata" "cowrie" "p0f" "glastopf" "dionaea" "conpot" "elastichoney"],
    :revision 1,
    :extract_source [],
    :name "Public Feeds from Yoroi - past 6h",
    :public 1,
    :indicators
    [{:indicator "85.93.20.243",
      :description "",
      :created "2018-07-09T18:02:40",
      :title "",
      :content "",
      :type "IPv4",
      :id 460576}
     {:indicator "221.194.44.211",
      :description "On: 2018-07-09T17:53:05.541000OS detected: Linux 3.11 and newer",
      :created "2018-07-09T18:02:40",
      :title "",
      :content "",
      :type "IPv4",
      :id 671506}]}
   {:description "",
    :tags ["suricata" "cowrie" "p0f" "glastopf" "dionaea" "conpot" "elastichoney"],
    :revision 1,
    :extract_source [],
    :name "Public Feeds from Yoroi - past 6h",
    :public 1,
    :indicators
    [{:indicator "85.93.20.243",
      :description "",
      :created "2018-07-09T18:02:40",
      :title "",
      :content "",
      :type "IPv4",
      :id 4605760}
     {:indicator "221.194.44.211",
      :description "On: 2018-07-09T17:53:05.541000OS detected: Linux 3.11 and newer",
      :created "2018-07-09T18:02:40",
      :title "",
      :content "",
      :type "IPv4",
      :id 671500}]}])

(defrecord InMemoryStateComponent
           [config]
  component/Lifecycle

  (start [component]
    (println "Starting InMemoryStateComponent")
    (assoc component :state-atom (atom database)))

  (stop [component]
    (println "Stopping InMemoryStateComponent")
    (assoc component :state-atom nil)))

(defn new-in-memory-state-component
  [config]
  (map->InMemoryStateComponent {:config config}))