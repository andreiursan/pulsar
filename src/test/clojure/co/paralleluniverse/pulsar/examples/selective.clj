(ns co.paralleluniverse.pulsar.examples.selective
  "A very simple selective-receive example"
  (:require [co.paralleluniverse.pulsar.core :refer :all]
            [co.paralleluniverse.pulsar.actors :refer :all])
  (:refer-clojure :exclude [promise await]))


(defsfn adder []
  (loop []
    (receive
      [from tag [:add a b]] (! from tag [:sum (+ a b)]))
    (recur)))

(defsfn computer [adder]
  (loop []
    (receive [m]
             [from tag [:compute a b c d]] (let [tag1 (maketag)]
                                             (! adder [@self tag1 [:add (* a b) (* c d)]])
                                             (receive
                                               [tag1 [:sum sum]]  (! from tag [:result sum])
                                               :after 10          (! from tag [:error "timeout!"])))
             :else (println "Unknown message: " m))
    (recur)))

(defsfn curious [nums computer]
  (when (seq nums)
    (let [[a b c d] (take 4 nums)
          tag       (maketag)]
      (! computer @self tag [:compute a b c d])
      (receive [m]
               [tag [:result res]]  (println a b c d "->" res)
               [tag [:error error]] (println "ERROR: " a b c d "->" error)
               :else (println "Unexpected message" m))
      (recur (drop 4 nums) computer))))

(defn -main []
  (let [ad (spawn adder)
        cp (spawn computer ad)
        cr (spawn curious (take 20 (repeatedly #(rand-int 10))) cp)]
    (join cr)
    :ok))
