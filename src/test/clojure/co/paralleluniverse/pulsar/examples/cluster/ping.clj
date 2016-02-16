(ns co.paralleluniverse.pulsar.examples.cluster.ping
  "A distributed version of the classic ping-pong example"
  (:require [co.paralleluniverse.pulsar.core :refer :all]
            [co.paralleluniverse.pulsar.actors :refer :all])
  (:refer-clojure :exclude [promise await]))

;; for running see comment in pong.clj

(defsfn ping [n]
  (dotimes [i n]
    (! :pong [:ping @self])
    (receive
      :pong (println "Ping received pong")))
  (! :pong :finished)
  (println "ping finished"))

(defn -main []
  (println "Ping started")
  (whereis :pong)
  #_(when (nil? (whereis :pong))
    (println "Waiting for pong to register...")
    (loop []
      (when (nil? (whereis :pong))
        (Thread/sleep 500)
        (recur))))
  (join (spawn ping 3))
  :ok)
