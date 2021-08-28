(ns core-test
    (:require [clojure.test :refer :all]
              [core :refer [app]]
              [clojure.string :as s]
              [clojure.data.json :as js]))

(deftest some-core-test
  (testing "Fixed, no fail."
    (is (zero? 0))))

(deftest base-health
  (testing "Basic request to app."
    (let [response (app {:uri "/health" :request-method :get})]
      (is (= 200 (:status response))))))

(deftest increment
  (testing
   (let [response (app {:uri "/inc" :request-method :post})
         body (js/read-json (:body response))]
     (is (= 200 (:status response)))
     (is (= 1 (:counter body)))))
  (app {:uri "/reset" :request-method :post}))

(deftest decrement
  (testing
   (let [response (app {:uri "/dec" :request-method :post})
         body (js/read-json (:body response))]
     (is (= 200 (:status response)))
     (is (= -1 (:counter body)))))
  (app {:uri "/reset" :request-method :post}))

(deftest reset
  (app {:uri "/inc" :request-method :post})
  (testing
   (let [response (app {:uri "/reset" :request-method :post})
         body (js/read-json (:body response))]
     (is (= 200 (:status response)))
     (is (= 0 (:counter body))))))
