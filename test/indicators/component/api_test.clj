(ns indicators.component.api-test
  (:require [charred.api :as charred]
            [clj-http.client :as client]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [indicators.core :as core]))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))

(defn get-free-port
  []
  (with-open [socket (java.net.ServerSocket. 0)]
    (.getLocalPort socket)))

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

(deftest content-negotiation-test
  (let [port (get-free-port)]
    #_{:clj-kondo/ignore [:unresolved-symbol]}
    (with-system [sut (core/indicators-system {:server {:port port}})]
      (is (= {:body "Not Acceptable"
              :status 406}
             (-> (str "http://localhost:" port "/indicators")
                 (client/get {:accept :edn
                              :throw-exceptions false})
                 (select-keys [:body :status])))))))

(deftest get-indicators-test
  (let [port (get-free-port)]
    #_{:clj-kondo/ignore [:unresolved-symbol]}
    (with-system [sut (core/indicators-system {:server {:port port}})]
      (is (= {:status 200}
             (-> (str "http://localhost:" port "/indicators")
                 (client/get {:accept :json
                              :as :json})
                 ; Currently shows all indicators, what's a viable way to test this?
                 (select-keys [:status])))))))

(deftest get-indicator-by-id-test
  (let [port (get-free-port)
        indicator-id 460576]
    #_{:clj-kondo/ignore [:unresolved-symbol]}
    (with-system [sut (core/indicators-system {:server {:port port}})]
      (is (= {:body  {:indicator "85.93.20.243",
                      :description "",
                      :created "2018-07-09T18:02:40",
                      :title "",
                      :content "",
                      :type "IPv4",
                      :id 460576}
              :status 200}
             (-> (str "http://localhost:" port "/indicators/" indicator-id)
                 (client/get {:accept :json
                              :as :json})
                 (select-keys [:body :status]))))
      (is (= {:body ""
              :status 404}
             (-> (str "http://localhost:" port "/indicators/" 111)
                 (client/get {:accept :json
                              :as :json
                              :throw-exceptions false})
                 (select-keys [:body :status])))))))

(comment (deftest post-indicator-search-test
           #_{:clj-kondo/ignore [:unresolved-symbol]}
           (let [port (get-free-port)]
             (with-system
               [system (core/indicators-system {:server {:port port}})]
               (is (= {:body example-indicators
                       :status 200}
                      (-> (str "http://localhost:" port "/indicators/search")
                          (client/post {:accept :json
                                        :content-type :json
                                        :as :json
                                        :body (charred/write-json-str example-indicators)})
                          (select-keys [:body :status]))))))))

(deftest get-indicator-by-type)

(deftest search-indicators-by-author)

(deftest search-indicators-by-created-date-range)

(deftest search-indicators-by-description)

(deftest search-indicators-by-tags)

(deftest search-indicators-by-targeted-countries)

(deftest search-indicators-by-type)