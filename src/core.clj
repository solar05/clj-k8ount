(ns core
  (:require [ring.adapter.jetty :as ring]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clj-time.core :as t]
            [routes :refer [routes]])
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
      (wrap-canonical-logs)
      (wrap-json-response)))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (start port)
    (println (str "Server started at " port " port!"))))
