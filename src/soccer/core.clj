(ns soccer.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def result-regex #"^([\w\s]+) (\d+), ([\w\s]+) (\d+)$")

(def win-fn (partial + 3))   ; these functions describe how to modify
(def lose-fn identity) ;   a score when the corresponding condition
(def draw-fn inc)      ;   happens

;; parse string into game result: "Sharks 3, Jets 2" => [["Sharks" 3] ["Jets" 2]]
(defn parse-input-line [text]
  (when-let [[_ t1 score1 t2 score2] (re-find result-regex text)]
    [[t1 (Integer. score1)]
     [t2 (Integer. score2)]]))

;; given a game result, return a vector of tuples containing teams and
;; operations that should be applied to their scores
;;    ex: [["Sharks" 3] ["Jets" 2]] => [["Sharks" win-fn] ["Jets" lose-fn]]
(defn judge-result [[[t1 score1] [t2 score2]]]
  (cond
    (= score1 score2) [[t1 draw-fn] [t2 draw-fn]]
    (> score1 score2) [[t1 win-fn] [t2 lose-fn]]
    :else             [[t1 lose-fn] [t2 win-fn]]))

;; apply a score update instruction to the score map:
;; {"Sharks" 5, "Jets" 3} with ["Sharks" draw-fn] => {"Sharks" 6, "Jets" 3}
(defn score-outcome [scores [team update-fn]]
  (update scores team (fnil update-fn 0)))

;; compose a list of compare functions into a single one. we'll apply it
;; to `sort-by` to get one field ascending and another descending
(defn multi-compare [comp-fns]
  (fn [xs ys]
    (or (->> (map (fn [f x y] (f x y)) comp-fns xs ys)
             (drop-while zero?)
             first) 0)))

;; assign ranks to a score table with tail recursion. `score-fn` returns a
;; score when applied to an item. logic: if (this score == last score), then
;; this-rank = last-rank, otherwise this-rank = list-position.
(defn assign-ranks
  ([score-fn score-list] (assign-ranks score-fn score-list [] nil nil 1))
  ([score-fn score-list result last-score last-rank base-rank]
   (if (empty? score-list)
     result
     (let [item  (first score-list)
           score (score-fn item)
           rank  (if (= score last-score) last-rank base-rank)]
       (recur score-fn (rest score-list) (conj result [rank item])
              score rank (inc base-rank))))))

;; format the string that will be printed
(defn format-for-output [[rank [team score]]]
  (format "%d. %s, %d pt%s" rank team score (if (= 1 score) "" "s")))

;; accept a list of strings and returns a list of strings (this is the core logic)
(defn parse-lines [lines]
  (->> lines                          ; start with a list of strings
       (map parse-input-line)         ; => list of game results
       (mapcat judge-result)          ; => list of teams + score update operations
       (reduce score-outcome {})      ; => map of team name to final score
       (sort-by (juxt second first)   ; => output list sorted properly
                (multi-compare [#(compare %2 %1) compare]))
       (assign-ranks second)          ; => ranks tagged to each list item
       (map format-for-output)))      ; => strings to be printed

;; accept filename as a parameter or raw data from stdin
(defn -main [& args]
  (let [buffer (if args (io/file (first args)) (java.io.BufferedReader. *in*))
        result (-> buffer slurp str/split-lines parse-lines)]
    (doall (map println result))))
