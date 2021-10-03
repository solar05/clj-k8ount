(ns controller
  (:require [ring.util.response :refer [response header]]
            [clj-http.client :as http]))

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

(defonce summer-url (str "http://" (or (System/getenv "SUMMER_URL") "localhost") ":5000"))

(defonce ping-url (str summer-url "/health"))

(defn ping-summer [_]
  (let [response (http/get ping-url)
        status (:status response)]
    (if (= status 200)
      (header
       (response ["It's alive!"]) "Content-Type" "application/json")
      (header
       (response ["He sleep!"]) "Content-Type" "application/json"))))
