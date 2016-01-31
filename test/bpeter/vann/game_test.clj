(ns bpeter.vann.game_test
  (:require [clojure.test :refer :all]
            [bpeter.vann.card :refer :all]
            [bpeter.vann.game :refer :all]))

(deftest simple
  (testing "Fold wenn alle Kartenwerte kleiner 6 sind"
    (let [state {"Hand" [(ofdiamonds 5) (ofhearts 4)]
                 "Rundenname" "flop"
                 "Tisch" [(ofdiamonds 8)] }
          expected "rfold"]
      (is (= (play state) expected))))

  (testing "Check bei preflop Karten gt 7"
    (let [state {"Hand" [(ofdiamonds 6) (ofhearts 2)]
                 "Rundenname" "preflop"
                 "Tisch" [] }
          expected "rcheck"]
      (is (= (play state) expected))))

  (testing "Fold bei preflop Karten le 7"
    (let [state {"Hand" [(ofdiamonds 3) (ofhearts 2)]
                 "Rundenname" "preflop"
                 "Tisch" [] }  
          expected "rfold"]
      (is (= (play state) expected))))

  (testing "Check bei flop Karten gt 15"
    (let [state {"Hand" [(ofdiamonds 10) (ofhearts 8)]
                 "Rundenname" "flop"
                 "Tisch" [(ofclubs 9)] }  
          expected "rcheck"]
      (is (= (play state) expected))))

  (testing "Fold bei flop Karten le 13"
    (let [state {"Hand" [(ofdiamonds 8) (ofhearts 4)]
                 "Rundenname" "flop"
                 "Tisch" [(ofclubs 9)] }  
          expected "rfold"]
      (is (= (play state) expected))))

  (testing "Raise bei einem paar auf der Hand"
    (let [state {"Hand" [(ofdiamonds 8) (ofhearts 8)]
                 "Rundenname" "preflop"
                 "Tisch" [] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise falls ich ein paar oder besser in Kombination mit den Handkarten habe"
    (let [state {"Hand" [(ofdiamonds 8) (ofhearts 7)]
                 "Rundenname" "turncard"
                 "Tisch" [(ofclubs 8)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise falls ich ein Paar oder besser in Kombination mit den Handkarten habe"
    (let [state {"Hand" [(ofdiamonds 8) (ofhearts 7)]
                 "Rundenname" "turncard"
                 "Tisch" [(ofclubs 8) (ofhearts 8)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Kein Raise falls das Paar nur aus dem Tisch gekommen ist"
    (let [state {"Hand" [(ofdiamonds 8) (ofhearts 7)]
                 "Rundenname" "turncard"
                 "Tisch" [(ofclubs 9) (ofhearts 9)] }  
          expected "rcheck"]
      (is (= (play state) expected))))

  (testing "Raise bei einem Flush"
    (let [state {"Hand" [(ofdiamonds 8) (ofdiamonds 7)]
                 "Rundenname" "river"
                 "Tisch" [(ofdiamonds 10) (ofdiamonds 11) (ofdiamonds 12) (ofclubs 2)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise bei einem Straight"
    (let [state {"Hand" [(ofdiamonds 8) (ofdiamonds 7)]
                 "Rundenname" "river"
                 "Tisch" [(ofclubs 10) (ofdiamonds 9) (ofhearts 6) (ofclubs 2)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise bei einem Straight mit Ass am Ende"
    (let [state {"Hand" [(ofdiamonds 12) (ofdiamonds 14)]
                 "Rundenname" "river"
                 "Tisch" [(ofclubs 11) (ofdiamonds 10) (ofhearts 13) (ofclubs 2)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise bei einem Straight mit Ass als Eins"
    (let [state {"Hand" [(ofdiamonds 14) (ofdiamonds 2)]
                 "Rundenname" "river"
                 "Tisch" [(ofclubs 5) (ofdiamonds 3) (ofhearts 4) (ofclubs 10)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Raise bei allen Handkarten König oder besser"
    (let [state {"Hand" [(ofclubs 13) (ofdiamonds 14)]
                 "Rundenname" "turncard"
                 "Tisch" [(ofclubs 5) (ofdiamonds 6) (ofhearts 4)] }  
          expected "rraise"]
      (is (= (play state) expected))))

  (testing "Check als default"
    (let [state {"Hand" [(ofdiamonds 14) (ofdiamonds 2)]
                 "Rundenname" "river"
                 "Tisch" [(ofclubs 5) (ofdiamonds 6) (ofhearts 4) (ofclubs 10)] }  
          expected "rcheck"]
      (is (= (play state) expected))))
)

