(ns controller
  (:require [ring.util.response :refer [response header bad-request]]))

(defonce maximum 99999)

(defn- count-numbers [number]
  (apply + (range 1 (inc number))))

(defn health [_]
  (header
   (response ["I'm alive!"]) "Content-Type" "application/json"))

(defn count-sum [params]
  (let [body (:body params)
        number (Integer/parseInt (:number body))]
    (header
     (cond
       (nil? body) (bad-request ["No number provided!"])
       (> number maximum) (bad-request ["Number is too high!"])
       :else (response {:number (count-numbers number)}))
     "Content-Type" "application/json")))
