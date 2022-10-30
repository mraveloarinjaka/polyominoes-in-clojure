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
       (map (comp vec translateToOrigin sort))
       (apply sorted-set)
       first))

(defn neighbors
  [[x y]]
  (list (vector (- x 1) y)
        (vector (+ x 1) y)
        (vector x (- y 1))
        (vector x (+ y 1))))

(defn adjacents
  [polyomino]
  (let 
    [potentialAdjacents
     (reduce
       (fn [result point]
         (reduce conj result (neighbors point)))
       #{}
       polyomino)]
    (clojure.set/difference potentialAdjacents (set polyomino))))

(defn generateFromOnePolyomino
  [polyomino]
  (->> polyomino
      adjacents
      (map #(conj polyomino %))
      ;(map retrieveCanonicalForm)
      (pmap retrieveCanonicalForm)
      set
      sequence))

(defn generate
  ([]
   (let [initialResult [[[0 0]]]]
   (concat initialResult (generate initialResult))))
  ([polyominos]
   (let [generated 
         (->> polyominos
              ;(mapcat generateFromOnePolyomino)
              (pmap generateFromOnePolyomino)
              concat
              set
              sequence)]
     (lazy-seq (cons generated (generate generated))))))

(defn nbOfPolyominos
  [N]
  {:pre [(instance? Number N) (> N 0)]}
  (count (nth (generate) (dec N))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> args
      first
      Integer/parseInt
      nbOfPolyominos
      println))
