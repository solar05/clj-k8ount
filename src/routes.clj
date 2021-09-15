(ns routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [controller :refer [current decrement health increment reset]]))

(defroutes routes
  (GET "/health" [] health)
  (POST "/inc" [] increment)
  (POST "/dec" [] decrement)
  (POST "/reset" [] reset)
  (GET "/current" [] current))
