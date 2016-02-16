(ns co.paralleluniverse.pulsar.examples.cluster.pong
  "A distributed version of the classic ping-pong example"
  (:require [co.paralleluniverse.pulsar.core :refer :all]
            [co.paralleluniverse.pulsar.actors :refer :all])
  (:refer-clojure :exclude [promise await]))

;; This example is intended to be a line-by-line translation of the canonical
;; Erlang [ping-pong example](http://www.erlang.org/doc/getting_started/conc_prog.html#id67347),
;; so it is not written in idiomatic Clojure.
;; run this with
;; lein with-profile cluster update-in :jvm-opts conj '"-Dgalaxy.nodeId=1"' '"-Dgalaxy.port=7051"' '"-Dgalaxy.slave_port=8051"' -- run -m co.paralleluniverse.pulsar.examples.cluster.pong
;; and after that
;; lein with-profile cluster update-in :jvm-opts conj '"-Dgalaxy.nodeId=2"' '"-Dgalaxy.port=7052"' '"-Dgalaxy.slave_port=8052"' -- run -m co.paralleluniverse.pulsar.examples.cluster.ping

(defsfn pong []
  (println "Pong started")
  (register! :pong @self)
  (loop []
    (receive
      :finished (println "Pong finished")
      [:ping ping] (do
                     (println "Pong received ping")
                     (! ping :pong)
                     (recur)))))

(defn -main []
  (join (spawn pong))
  :ok)
