(ns core
  (:require [ring.adapter.jetty :as ring]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response header]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clj-time.core :as t])
  (:gen-class))

(def counter (atom 0))

(def request-ids (atom 0))

(defn- wrap-counter []
  (header (response {:counter @counter}) "Content-Type" "application/json"))

(defn health [_]
  (header
   (response ["I'm alive!"]) "Content-Type" "application/json"))

(defn increment [_]
  (swap! counter inc)
  (wrap-counter))

(defn decrement [_]
  (swap! counter dec)
  (wrap-counter))

(defn reset [_]
  (reset! counter 0)
  (wrap-counter))

(defn current [_]
  (wrap-counter))

(defroutes routes
  (GET "/health" [] health)
  (POST "/inc" [] increment)
  (POST "/dec" [] decrement)
  (POST "/reset" [] reset)
  (GET "/current" [] current))

(defn wrap-canonical-logs [func]
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
	"http_status="(:status result)))
      result)))

(def handler (wrap-canonical-logs
              (wrap-cors routes
                         :access-control-allow-origin [#".*"]
                         :access-control-allow-methods [:get :post])))

(def app (wrap-json-response handler))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (start port)
    (println (str "Server started at " port " port!"))))
