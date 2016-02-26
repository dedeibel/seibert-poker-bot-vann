(ns bpeter.collection_test
  (:require [clojure.test :refer :all]
            [bpeter.collection :refer :all]))

(deftest test-count?
  (testing "empty sequence"
      (is ((count? 0) '()))
      (is ((count? 0) [])))
  (testing "one elem"
      (is ((count? 1) ["something"])))
  (testing "multiple"
      (is ((count? 3) [3 2 1])))
  (testing "negative"
      (is (not ((count? 2) [3 2 1])))))

(deftest test-in?
  (testing "empty sequence"
      (is (not (in? 0 '[])))
      (is (not (in? 0 '()))))
  (testing "one elem"
      (is (in? 1 [1])))
  (testing "multiple"
      (is (in? 3 [2 3 1]))
      (is (in? :a [2 3 :a]))
      (is (in? 3 [3 2 1])))
  (testing "negative"
      (is (not (in? :a [2 3 :b])))))

(deftest test-count-max-col-length
  (testing "empty sequence"
      (is (= 0 (count-max-col-length [[] []])))
      (is (= 0 (count-max-col-length []))))
  (testing "all equal"
      (is (= 1 (count-max-col-length [[:a] [2]]))))
  (testing "multiple"
      (is (= 3 (count-max-col-length [[:a] [:c :b 2] [] [2]])))))

(deftest test-continous-sequences
  (testing "empty sequence"
      (is (= (continous-sequences []) [])))
  (testing "one sequence"
      (is (= (continous-sequences [1 2 3 4]) [[1 2 3 4]])))
  (testing "multiple sequences"
      (is (= (continous-sequences [9 6 7 8 1 2 0 3 4])
             [[9] [6 7 8] [1 2] [0] [3 4]])))
  (testing "reverse it not a sequence"
      (is (= (continous-sequences [3 2 1]) [[3] [2] [1]])))
  (testing "negative values work"
      (is (= (continous-sequences [-1 0 1 5]) [[-1 0 1] [5]])))
  )

