(ns polyominoes.core
  (:require [clojure.set])
  (:gen-class))

(defn findOrigin
  [polyomino]
  (reduce 
    (fn [[resX resY] [x y]] (vector (min resX x) (min resY y)))
    polyomino))

(defn translateToOrigin
  [polyomino]
  (let [[originX originY] (findOrigin polyomino)]
    (map
      (fn [[x y]] (vector (- x originX) (- y originY)))
      polyomino)))

(defn rotateOnePoint90 
  [[x y]]
  (vector (* y -1) x))

(defn rotateOnePoint180 
  [[x y]]
  (vector (* x -1) (* y -1)))

(defn rotateOnePoint270 
  [[x y]]
  (vector y (* x -1)))

(defn rotate
  [rotator polyomino]
  (map #(rotator %) polyomino))

(defn mirror
  [polyomino]
  (map
    (fn [[x y]] (vector (* x -1) y))
    polyomino))

(defn retrieveRotationsAndMirror
  [polyomino]
  (let [mirrored (mirror polyomino)]
    (list
      polyomino
      (rotate rotateOnePoint90 polyomino)
      (rotate rotateOnePoint180 polyomino)
      (rotate rotateOnePoint270 polyomino)
      mirrored
      (rotate rotateOnePoint90 mirrored)
      (rotate rotateOnePoint180 mirrored)
      (rotate rotateOnePoint270 mirrored))))

(defn retrieveCanonicalForm
  [polyomino]
  (->> polyomino
       retrieveRotationsAndMirror
       (pmap (comp vec translateToOrigin sort))
       (apply sorted-set)
       first))

(defn neighbors
  [[x y]]
  (vector (vector (- x 1) y)
          (vector (+ x 1) y)
          (vector x (- y 1))
          (vector x (+ y 1))))

(defn adjacents
  [polyomino]
  (reduce (fn [result point]
            (reduce 
              conj 
              result 
              (remove (set polyomino) (neighbors point))))
          #{}
          polyomino))

(defn generateFromOnePolyomino
  [polyomino]
  (->> polyomino
       adjacents
       (map (comp retrieveCanonicalForm vec #(conj polyomino %)))))

(defn generate
  ([]
   (let [initialResult [[[0 0]]]]
     (cons initialResult (generate initialResult))))
  ([polyominos]
   (let [generated 
         (->> polyominos 
              (pmap generateFromOnePolyomino)
              (apply concat)
              set
              sequence)]
     (lazy-seq (cons generated (generate generated))))))

(defn nbOfPolyominos
  [N]
  {:pre [(number? N) (> N 0)]}
  (count (nth (generate) (dec N))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      first
      Integer/parseInt
      nbOfPolyominos
      println))
