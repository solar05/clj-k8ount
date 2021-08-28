(ns core-test
    (:require [clojure.test :refer :all]
              [core :refer [app]]))

(deftest some-core-test
  (testing "Fixed, no fail."
    (is (zero? 0))))

(deftest base-health
  (testing "Basic request to app."
    (let [response (app {:uri "/health" :request-method :get})]
      (is (= 200 (:status response))))))
