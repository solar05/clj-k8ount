(ns routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [controller :refer [health count-sum]]))

(defroutes routes
  (GET "/health" [] health)
  (POST "/count" [] count-sum))
