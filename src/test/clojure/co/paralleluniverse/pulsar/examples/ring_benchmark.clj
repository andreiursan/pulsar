(ns co.paralleluniverse.pulsar.examples.ring-benchmark
  "An implementation of the ring benchmark using actors"
  (:require [co.paralleluniverse.pulsar.core :refer :all]
            [co.paralleluniverse.pulsar.actors :refer :all])
  (:refer-clojure :exclude [promise await])
  (:import [co.paralleluniverse.actors Actor]))


(defn spawn-relay-actor [^Actor prev n]
  (if (== n 0)
    prev
    (let [actor (spawn :mailbox-size 10
                       #(loop []
                          (! prev (inc (receive)))
                          (recur)))]
      (recur actor (dec n)))))


(defn -main [M1 N1]
  (let [M (Integer/parseInt M1)
        N (Integer/parseInt N1)]
    (println "M: " M " N: " N)
    (dotimes [i 1000]
      (let [num-messages
            (time
             (let [manager
                   (spawn :mailbox-size 10
                          #(let [last-actor (spawn-relay-actor @self (dec N))]
                             (! last-actor 1) ; start things off
                             (loop [j (int 1)]
                               (let [m (receive)]
                                 (if (< j M)
                                   (do
                                     (! last-actor (inc m))
                                     (recur (inc j)))
                                   m)))))]
               (join manager)))]
        (println i ": Messages " num-messages)))))
