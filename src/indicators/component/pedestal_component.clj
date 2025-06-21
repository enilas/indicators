(ns indicators.component.pedestal-component
  (:require [charred.api :as charred]
            [com.stuartsierra.component :as component]
            [io.pedestal.connector :as connector]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.content-negotiation :as content-negotiation]
            [io.pedestal.http.http-kit :as http-kit]
            [schema.core :as s]))

(defn response
  ([status]
   (response status nil))
  ([status body]
   (merge
    {:status status
     :headers {"Content-Type" "application/json"}}
    (when body {:body (charred/write-json-str body)}))))

(def ok (partial response 200))

(def not-found (partial response 404))

(defn get-indicators
  [{:keys [in-memory-state-component]}]
  @(:state-atom in-memory-state-component))

(def get-indicators-handler
  {:name :get-indicator-by-id-handler
   :enter
   (fn [{:keys [dependencies] :as context}]
     (println "get-indicators-handler" (keys context))
     (let [indicators (get-indicators dependencies)
           response (ok indicators)]
       (assoc context :response response)))})

(defn get-indicator-by-id
  [{:keys [in-memory-state-component]} indicator-id]
  (->> @(:state-atom in-memory-state-component)
       (map :indicators)
       (flatten)
       (filter #(= (:id %) (Integer/parseInt indicator-id)))
       (first)))

(def get-indicator-by-id-handler
  {:name :get-indicator-by-id-handler
   :enter
   (fn [{:keys [dependencies] :as context}]
     (println "get-indicator-by-id-handler" (keys context))
     (let [request (:request context)
           indicator (get-indicator-by-id dependencies
                                          (-> request
                                              :path-params
                                              :indicator-id))
           response (if indicator
                      (ok indicator)
                      (not-found nil))]
       (assoc context :response response)))})

(defn inject-dependencies
  [dependencies]
  (interceptor/interceptor
   {:name  :inject-dependencies
    :enter (fn [context]
             (assoc context :dependencies dependencies))}))

(def content-negotiation-interceptor (content-negotiation/negotiate-content ["application/json"]))

(defn search-indicators
  [{:keys [in-memory-state-component]} indicator]
  ; This is currently for posting *new* data, not searching
  (swap! @(:state-atom in-memory-state-component) conj indicator))

(s/defschema
  IndicatorItem
  {:indicator s/Str
   :description s/Str
   :created s/Str
   :title s/Str
   :type s/Str
   :id s/Int})

(s/defschema
  IndicatorsOfCompromise
  {:industries []
   :tlp s/Str
   :description s/Str
   :created s/Str
   :tags []
   :modified s/Str
   :author_name s/Str
   :public s/Int
   :extract_source []
   :references []
   :targeted_countries []
   :indicators [IndicatorItem]})

(def post-indicator-search-handler
  {:name :post-indicator-search-handler
   :enter
   (fn [{:keys [dependencies] :as context}]
     (println "post-indicator-search-handler" (keys context))
     (let [request  (:request context)
           query (s/validate IndicatorsOfCompromise (:json-params request))]
       (search-indicators dependencies query)
       (assoc context :response (ok query))))})

(def routes
  #{["/indicators"               :get get-indicators-handler      :route-name :get-indicators]
    ["/indicators/:indicator-id" :get get-indicator-by-id-handler :route-name :get-indicator-by-id]
    ["/indicators/search"        :post [body-params/body-params
                                        post-indicator-search-handler] :route-name :post-indicator-search]})

(defrecord PedestalComponent
           [config
            in-memory-component]
  component/Lifecycle

  (start [component]
    (println "Starting PedestalComponent")
    (let [server (-> (connector/default-connector-map (-> config :server :port))
                     (connector/with-default-interceptors)
                     (connector/with-interceptor (inject-dependencies component))
                     (connector/with-interceptor content-negotiation-interceptor)
                     (connector/with-routes routes)
                     (http-kit/create-connector nil)
                     (connector/start!))]
      (assoc component :server server)))

  (stop [component]
    (println "Stopping PedestalComponent")
    (when-let [server (:server component)]
      (connector/stop! server))
    (assoc component :state nil)))

(defn new-pedestal-component
  [config]
  (map->PedestalComponent {:config config}))