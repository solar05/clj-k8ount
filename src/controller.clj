(ns controller
  (:require [ring.util.response :refer [response header]]))

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
