(ns bpeter.vann.core
  (:require [bpeter.vann.game :as g]))

(def PREFIX "Vann:")
(defn log
  ([a]      (.log js/console PREFIX a))
  ([a b]    (.log js/console PREFIX a b))
  ([a b c]  (.log js/console PREFIX a b c)))

(defn parseJSON [x]
  (js->clj (.parse (.-JSON js/window) x)))

(def websocket* (atom nil))
(def clientdata (atom { :table nil :player-name nil :pass nil}))

(defn- sendm [m]
  (.send @websocket* m))

(defn connect [url]
  (let [connection (reset! websocket* (js/WebSocket. url))]
    connection))

(defn response-ok? [response]
  (= response "o"))

(defn sign-up-message [clientdata]
  (str "p" (:table clientdata) "\n" (:player-name clientdata) "\n" (:pass clientdata)))

(defn sign-up [clientdata]
  (log "signing up")
  (sendm (sign-up-message clientdata)))

(defn handle-action [data]
  (let [command (subs data 0 1)
        state-string (subs data 1)
        state (parseJSON state-string)]
    (sendm (g/play state))))

(declare handle-data)

(defn handle-setup-response [data]
  (if (response-ok? data)
    (do
      (log "signed up successfully")
      (reset! handle-data handle-action))
    (log "negative sign-up response:" (subs data 1))))

(def handle-data (atom handle-setup-response))

(defn mainloop []
  (doall
    (map #(aset @websocket* (first %) (second %))
         [["onopen" (fn [] 
                      (log "socket open")
                      (sign-up @clientdata)
                      )]
          ["onclose" (fn [] (log "socket closed"))]
          ["onerror" (fn [e] (log (str "socket error" e)))]
          ["onmessage" (fn [m]
                           (@handle-data (.-data m))
                           )]]))
   (.addEventListener js/window "unload" 
             (fn []
               (.close @websocket*)
               (reset! @websocket* nil)))
  (log "websocket initialized"))

(defn ^:export main [url table player-name pass] 
  (log "starting to play:" (str url " " table " " player-name " " pass))
  (reset! clientdata {:table table :player-name player-name :pass pass})
  (connect url)
  (mainloop))

