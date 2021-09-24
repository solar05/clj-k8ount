(ns core-test
  (:require [clojure.test :refer :all]
            [core :refer [app]]
            [ring.mock.request :as mock]
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

(deftest count-sum
  (testing
   (let [response (app (-> (mock/request :post "/count")
                           (mock/json-body {:number "20"})))
         body (read-body response)]
     (is (= 200 (:status response)))
     (is (= 210 (:number body))))))
