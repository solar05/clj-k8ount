(ns core-test
  (:require [clojure.test :refer :all]
            [core :refer [app]]
            [clojure.data.json :as js]))

(defn- read-body [response]
  (js/read-json (:body response)))

(deftest some-core-test
  (testing "Fixed, no fail."
    (is (zero? 0))))

(deftest base-health
  (testing "Basic request to app."
    (let [response (app {:uri "/health" :request-method :get})
          body (first (read-body response))]
      (is (= 200 (:status response)))
      (is (= "I'm alive!" body)))))
