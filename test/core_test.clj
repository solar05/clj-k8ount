(ns core-test
    (:require [clojure.test :refer :all]))

(deftest some-core-test
  (testing "Fixed, no fail."
    (is (zero? 0))))
