(ns core
  (:require [ring.adapter.jetty :as ring]
            [compojure.core :refer [GET defroutes]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response header]])
  (:gen-class))

(defn health [_]
  (header (response []) "Content-Type" "application/json"))

(defroutes routes
  (GET "/health" [] health))

(def app (wrap-json-response routes))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (start port)
    (println (str "Server started at " port " port!"))))
