(ns bpeter.collection)

(defn count? [expected-count]
  (fn [collection]
    (= expected-count (count collection))))

(defn in?  [element collection]  
  (some #(= element %) collection))

(defn count-max-col-length [col-of-cols]
  (reduce max 0 (map count col-of-cols)))

(defn continous-sequences
  "Creates partitions of continous numerical sequences in the given collection.
   In order to the the maximum continous sequnce col should be sorted."[col]
  (if (seq col)
    (loop [col col cur [(first col)] all []]
      (if (> (count col) 1)
        (if (= (inc (first col)) (second col))
          (recur (rest col) (conj cur (second col)) all)
          (recur (rest col) [(second col)] (conj all cur)))
        (conj all cur)))
    []))

