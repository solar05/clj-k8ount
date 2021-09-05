(ns core
  (:require [ring.adapter.jetty :as ring]
            [compojure.core :refer [GET POST defroutes]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response header]]
            [ring.middleware.cors :refer [wrap-cors]])
  (:gen-class))

(def counter (atom 0))

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

(def handler (wrap-cors routes
                        :access-control-allow-origin [#".*"]
                        :access-control-allow-methods [:get :post]))

(def app (wrap-json-response handler))

(defn start [port]
  (ring/run-jetty app {:port port :join? false}))

(defn -main [& _args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4000"))]
    (start port)
    (println (str "Server started at " port " port!"))))
