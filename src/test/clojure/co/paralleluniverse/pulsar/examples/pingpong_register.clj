(ns co.paralleluniverse.pulsar.examples.pingpong-register
  "The classic ping-pong example from the Erlang tutorial"
  (:require [co.paralleluniverse.pulsar.core :refer :all]
            [co.paralleluniverse.pulsar.actors :refer :all])
  (:refer-clojure :exclude [promise await]))

;; This example is intended to be a line-by-line translation of the canonical
;; Erlang [ping-pong example](http://www.erlang.org/doc/getting_started/conc_prog.html#id67347),
;; so it is not written in idiomatic Clojure.

(defsfn ping [n]
  (if (== n 0)
    (do
      (! :pong :finished)
      (println "ping finished"))
    (do
      (! :pong [:ping @self])
      (receive
        :pong (println "Ping received pong"))
      (recur (dec n)))))

(defsfn pong []
  (receive
    :finished (println "Pong finished")
    [:ping ping] (do
                   (println "Pong received ping")
                   (! ping :pong)
                   (recur))))

(defn -main []
  (register! :pong (spawn pong))
  (spawn ping 3)
  :ok)

#_(defn -main []
    (let [a1 (register! :pong (spawn pong))
          b1 (spawn ping 3)]
      (join a1)
      (join b1)
      (System/exit 0)))
