(ns soccer.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [soccer.core :as sut]))

(deftest parse-input-line
  (testing "input is parsed even if team names have spaces"
    (is (= [["Sharks" 3] ["Jets" 2]] (sut/parse-input-line "Sharks 3, Jets 2")))
    (is (= [["Long Team Name" 3] ["Another Name" 2]] (sut/parse-input-line "Long Team Name 3, Another Name 2")))))

(deftest judge-result
  (testing "we can accurately judge wins, losses, and draws"
    (is (= [["Sharks" sut/win-fn] ["Jets" sut/lose-fn]] (sut/judge-result [["Sharks" 3] ["Jets" 2]])))
    (is (= [["Sharks" sut/lose-fn] ["Jets" sut/win-fn]] (sut/judge-result [["Sharks" 2] ["Jets" 3]])))
    (is (= [["Sharks" sut/draw-fn] ["Jets" sut/draw-fn]] (sut/judge-result [["Sharks" 2] ["Jets" 2]])))))

(deftest score-outcome
  (testing "score functions work as expected"
    (is (= 3 (sut/win-fn 0)))
    (is (= 0 (sut/lose-fn 0)))
    (is (= 1 (sut/draw-fn 0))))
  (testing "scores updated when team exists in table as well as when it doesn't"
    (is (= {"Jets" 3} (sut/score-outcome {"Jets" 0} ["Jets" sut/win-fn])))
    (is (= {"Jets" 3} (sut/score-outcome {} ["Jets" sut/win-fn])))))

(deftest assign-ranks
  (testing "ranks are skipped properly for ties"
    (is (= [[1 10] [2 12] [2 12] [4 15] [5 17] [5 17] [5 17] [8 18] [9 21]]
           (sut/assign-ranks identity [10 12 12 15 17 17 17 18 21])))))

(deftest format-for-output
  (testing "output is correct for singular & plural point values"
    (is (= "3. Jets, 3 pts" (sut/format-for-output [3 ["Jets" 3]])))
    (is (= "3. Jets, 1 pt" (sut/format-for-output [3 ["Jets" 1]])))))

(deftest parse-lines
  (testing "the whole shebang"
    (let [input    (-> (io/resource "sample-input.txt") slurp str/split-lines)
          expected (-> (io/resource "expected-output.txt") slurp str/split-lines)]
      (is (= expected (sut/parse-lines input))))))
