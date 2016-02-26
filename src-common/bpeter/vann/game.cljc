(ns bpeter.vann.game
  (:require [bpeter.vann.card   :as c]
            [bpeter.collection  :as col]
            [bpeter.vann.system :as sys]))

(defn group-by-rank [hand]
  (vals (group-by :rank hand)))

(defn pair? [hand]
  (->> hand
       group-by-rank
       (some (col/count? 2))))

(defn at-least-same-ranks? [hand n]
  (->> hand
       group-by-rank
       (some #(>= (count %) n))))

(defn count-same-rank [hand]
  (->> hand
       group-by-rank
       (map count)
       (apply max 0)))

(defn hand [state]
  (map c/parse-card-string (get state "Hand")))

(defn table [state]
  (map c/parse-card-string (get state "Tisch")))

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

(defn add-ace-as-one [ranklist]
  (if (col/in? c/ace ranklist)
    (cons 1 ranklist)
    ranklist))

(defn straight? [combined]
  (->> combined
       (map :rank)
       add-ace-as-one
       sort
       col/continous-sequences
       col/count-max-col-length
       (<= 5)))

(defn round [state]
  (get state "Rundenname"))

(defn determine-fold-threshold [state]
  (condp = (round state)
    "preflop" 7
    "flop" 13
    "turncard" 15
    "river" 16
    "showdown" 17))

(defn ^:export play [input-state]
  (let [state (sys/import-from-system-datastructure input-state)
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

