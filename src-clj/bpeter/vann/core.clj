(ns bpeter.vann.core
  (:require
    [bpeter.vann.game :as g]
    [bpeter.vann.system :as sys]
    [aleph.http :as http]
    [manifold.deferred :as d]
    [manifold.stream :as s]
    [clojure.data.json :as json])
  (:gen-class))

(defn connect [url]
  (let [connection @(http/websocket-client url)]
    (sys/log "started connection" connection)
    connection))

(defn stop [connection]
  (sys/log "stopping - closing connection")
  (s/close! connection)
  (sys/log "closed")
  connection)

(defn response-ok? [response]
  (= response "o"))

(defn sign-up-message [table player-name pass]
  (str "p" table "\n" player-name "\n" pass))

(defn sign-up [connection table player-name pass]
  (sys/log "signing up")
  (s/put! connection (sign-up-message table player-name pass)))

(def print-exception-fn 
  (fn [ex]
    (sys/log "ERROR: " ex)
    nil))

(defn action [data]
  (let [command (subs data 0 1)
        state-string (subs data 1)
        state (json/read-str state-string)]
    (g/play state)))

(defn mainloop [connection]
  (d/loop []
    (->
      (d/let-flow [msg (s/take! connection)]
        (if msg
          (d/let-flow [response (d/future (action msg))
                       result (s/put! connection response)]
            (when result
              (d/recur)))
          (if (s/closed? connection)
            (sys/log "connection closed, exiting mainloop")
            (do
              (sys/log "msg empty, sleeping")
              (Thread/sleep 200)
              (d/recur)))))
      (d/catch print-exception-fn))))

(defn enter-mainloop-if-response-is-ok [connection]
  (sys/log "entering mainloop")
  (if (response-ok? @(s/take! connection))
    @(mainloop connection)
    (sys/log "response negative, could not register with server")))

(defn defconvar [connection]
  (def con connection)
  connection)

(defn play [url table player-name pass] 
  (sys/log "starting")
  (doto (connect url)
    defconvar
    (sign-up table player-name pass)
    enter-mainloop-if-response-is-ok
    stop))

(defn -main [url table player-name pass & args]
  (play url table player-name pass))

(defn replstart [& args]
  (sys/log "replstart")
  (future 
    (play 
      "ws://localhost:8080"
      "f546402fX020dXd55fXbb75Xda327935a146"
      "Vann"
      "TR8T0R")
    (sys/log "replstop")))

#_(replstart)
#_(s/close! con)

