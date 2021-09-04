(ns core-test
  (:require [clojure.test :refer :all]
            [core :refer [app]]
            [clojure.string :as s]
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

(deftest increment
  (testing
   (let [response (app {:uri "/inc" :request-method :post})
         body (read-body response)]
     (is (= 200 (:status response)))
     (is (= 1 (:counter body)))))
  (app {:uri "/reset" :request-method :post}))

(deftest decrement
  (testing
   (let [response (app {:uri "/dec" :request-method :post})
         body (read-body response)]
     (is (= 200 (:status response)))
     (is (= -1 (:counter body)))))
  (app {:uri "/reset" :request-method :post}))

(deftest reset
  (app {:uri "/inc" :request-method :post})
  (testing
   (let [response (app {:uri "/reset" :request-method :post})
         body (read-body response)]
     (is (= 200 (:status response)))
     (is (zero? (:counter body))))))

(deftest current
  (testing
   (let [response (app {:uri "/current" :request-method :get})
         body (read-body response)]
     (is (= 200 (:status response)))
     (is (zero? (:counter body))))))
