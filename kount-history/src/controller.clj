(ns controller
  (:require [ring.util.response :refer [response header]]
            [clj-time.core :as t]))

(defonce history "history.txt")

(defn health [_]
  (header
   (response ["I'm alive!"]) "Content-Type" "application/json"))

(defn write-history [params]
  (let [body (:body params)
        text (:text body)
        timestamp (str "[" (t/now) "]")]
    (spit history (str timestamp ": " text "\n") :append true)
    (header
     (response {:timestamp timestamp})
     "Content-Type" "application/json")))

(defn show-history [_]
  (header
   (response {:history (slurp history)})
   "Content-Type" "application/json"))
