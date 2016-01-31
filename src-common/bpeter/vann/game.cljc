(ns bpeter.vann.game)

(def PREFIX "Vann:")

#?(:clj
  (defn log [& args]
    (apply println args))
  :cljs
  ; TODO fix .log apply problem and use (str a)
  (defn log
    ([a]      (.log js/console PREFIX a))
    ([a b]    (.log js/console PREFIX a b))
    ([a b c]  (.log js/console PREFIX a b c))
    ([a b c d]  (.log js/console PREFIX a b c d))))

(defn rank-symbol-to-number [rank-symbol-str]
  (case rank-symbol-str
    "J" 11
    "Q" 12
    "K" 13
    "A" 14))

(defn parse-int [string]
  #?(:clj
      (try
        (Integer/parseInt string)
        (catch NumberFormatException e
          nil))
      :cljs
      (let [number (js/parseInt string)]
        (if (js/isNaN number)
          nil
          number))))

(defn parse-rank [string]
  (let [rank-str (apply str (butlast string))
        num (parse-int rank-str)]
    (if (nil? num)
      (rank-symbol-to-number rank-str)
      num)))

(defn parse-suit [string]
  (case (first (reverse string))
    \u2660 :spades
    \u2665 :hearts
    \u2666 :diamonds
    \u2663 :clubs))

(defn parse-card-string [string]
  {
   :rank (parse-rank string)
   :suit (parse-suit string)
   })

(defn count? [n]
  (fn [col]
    (= n (count col))))

(defn group-hand-by-rank [hand]
  (vals (group-by :rank hand)))

(defn pair? [hand]
  (->> hand
       group-hand-by-rank
       (some (count? 2))))

(defn at-least-same-ranks? [hand n]
  (->> hand
       group-hand-by-rank
       (some #(>= (count %) n))))

(defn count-same-rank [hand]
  (->> hand
       group-hand-by-rank
       (map count)
       (apply max 0)))

(defn hand [state]
  (map parse-card-string (get state "Hand")))

(defn table [state]
  (map parse-card-string (get state "Tisch")))

(defn combined [state]
  (into (hand state) (table state)))

(defn rank-sum [hand]
  (reduce + (map :rank hand)))

(defn every-rank? [hand pred & pred-args]
  (every? #(apply pred % pred-args) (map :rank hand)))

(defn flush? [combined]
  (some
    #(>= (count %) 5)
    (vals (group-by :suit combined))))

; col must be sorted
(defn continous-sequences [col]
  (loop [col col cur [(first col)] all []]
    (if (> (count col) 1)
      (if (= (inc (first col)) (second col))
        (recur (rest col) (conj cur (second col)) all)
        (recur (rest col) [(second col)] (conj all cur)))
      (conj all cur))))

(defn max-coll-length [col-of-cols]
  (->> col-of-cols
       (map count)
       sort
       last))

(defn straight? [combined]
  (->> combined
       (map :rank)
       sort
       continous-sequences
       max-coll-length
       (< 5)))

(defn round [state]
  (get state "Rundenname"))

(defn determine-fold-threshold [state]
  (condp = (round state)
    "preflop" 7
    "flop" 13
    "turncard" 15
    "river" 16
    "showdown" 17))

(defn import-state [input-state]
  #?(:clj
    input-state
    :cljs
    (js->clj input-state)))

(defn ^:export play [input-state]
  (let [state (import-state input-state)
        hand (hand state)
        combined-hand (combined state)
        table (table state)
        action (round state)
        rank-sum-fold-threshold (determine-fold-threshold state)
        same-rank-hand (count-same-rank hand)
        same-rank-table (count-same-rank table)
        same-rank-combined (count-same-rank combined-hand)]
    (cond
      (pair? hand) "rraise"
      (and
        (>= same-rank-combined 2)
        (> same-rank-combined same-rank-table)) "rraise"
      (flush? (combined state)) "rraise"
      (straight? combined-hand) "rraise"
      (every-rank? hand >= 13) "rraise"
      (every-rank? hand < 6) "rfold"
      (< (rank-sum hand) rank-sum-fold-threshold) "rfold"
      :else "rcheck")))

; Use for debugging in fn play
;      (log "pair" (pair? hand))
;      (log "more" (and
;        (>= same-rank-combined 2)
;        (> same-rank-combined same-rank-table)))
;      (log "flush" (flush? (combined state)))
;      (log "straight" (straight? combined-hand))
;      (log "rank>13" (every-rank? hand >= 13))
;      (log "rank<6" (every-rank? hand < 6))
;      (log "lt foldsthesh?" (< (rank-sum hand) rank-sum-fold-threshold))

; State Example: 
; {"Spieler":
;  [{"Name":"Spieler A",
;    "letzteAktion":"-",
;    "Stack":"-85123",
;    "Einsatz":"1"},
;   {"Name":"Spieler B",
;    "letzteAktion":"-",
;    "Stack":"-85124",
;    "Einsatz":"2"},
;   {"Name":"Minimeee",
;    "letzteAktion":"check",
;    "Stack":"89545",
;    "Einsatz":"2"},
;   {"Name":"Spieler Ben",
;    "letzteAktion":"-",
;    "Stack":"78423",
;    "Einsatz":"0"}],
;  "Einsatz":"0",
;  "Hand":["3\u2666", "7\u2665"],
;  "Hoechsteinsatz":"2",
;  "Rundenname":"preflop",
;  "Tisch":[],
;  "Pot":"5",
;  "Stack":"78423",
;  "LetzteAktion":"-"}  

