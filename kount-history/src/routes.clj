(ns routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [controller :refer [health write-history show-history]]))

(defroutes routes
  (GET "/health" [] health)
  (POST "/write" [] write-history)
  (GET "/lookup" [] show-history))
