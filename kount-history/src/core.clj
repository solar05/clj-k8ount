(ns core
  (:require [ring.adapter.jetty :as ring]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clj-time.core :as t]
            [routes :refer [routes]]
            [clojure.java.io :as io])
  (:gen-class))

(def request-ids (atom 0))

(defn- wrap-canonical-logs [func]
  (fn [& args]
    (let [init-time (t/now)
          params (first args)
          history-time (str init-time)
          result (apply func args)
          {:keys [uri request-method remote-addr query-string]} params
          req-time (t/in-millis (t/interval init-time (t/now)))]
      (println
       (str
        "[" history-time "] "
        "req_id=" (swap! request-ids inc) " "
        "exec_time=" req-time "ms "
        "http_path=" uri " "
        "http_method=" (name request-method) " "
        "addr=" remote-addr " "
        (when-not (nil? query-string) (str "query_str='" query-string "'"))
        "http_status=" (:status result)))
      result)))

(def app
  (-> routes
      (wrap-cors :access-control-allow-origin [#".*"] :access-control-allow-methods [:get :post])
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-canonical-logs)
      (wrap-json-response)))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn- ensure-history-created []
  (when-not (.exists (io/file "history.txt"))
    (spit "history.txt" "Your history:\n")))

(defn -main [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "6000"))]
    (ensure-history-created)
    (start port)
    (println (str "Server started at " port " port!"))))
