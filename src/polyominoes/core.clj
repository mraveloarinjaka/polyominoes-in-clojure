(ns polyominoes.core
  (:require [clojure.set]
            [clojure.core.reducers :as r]
            [tesser.core :as t])
  (:gen-class))

(defn findOrigin
  [polyomino]
  (reduce
    (fn [[resX resY] [x y]] (vector (min resX x) (min resY y)))
    polyomino))

(defn translateToOrigin
  [polyomino]
  (let [[originX originY] (findOrigin polyomino)]
    (mapv
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
  (mapv rotator polyomino))

(defn mirror
  [polyomino]
  (mapv
    (fn [[x y]] (vector (* x -1) y))
    polyomino))

(defn retrieveRotationsAndMirror
  [polyomino]
  ((juxt
    identity
    (partial rotate rotateOnePoint90)
    (partial rotate rotateOnePoint180)
    (partial rotate rotateOnePoint270)
    mirror
    (comp (partial rotate rotateOnePoint90) mirror)
    (comp (partial rotate rotateOnePoint180) mirror)
    (comp (partial rotate rotateOnePoint270) mirror))
   polyomino))

(defn retrieveCanonicalForm
  [polyomino]
  (->> polyomino
       retrieveRotationsAndMirror
       (r/map (comp translateToOrigin sort))
       (into (sorted-set))
       first))

(defn neighbors
  [[x y]]
  [[(dec x) y]
   [(inc x) y]
   [x (dec y)]
   [x (inc y)]])

(defn fromOnePolyomino
  [polyomino]
  (->> polyomino
       (r/mapcat neighbors)
       (r/remove (set polyomino))
       (r/map (partial conj polyomino))
       (r/map retrieveCanonicalForm)))

(defn fromOnePolyominoTransducer
  [polyomino]
  (comp
   (mapcat neighbors)
   (remove (set polyomino))
   (map (partial conj polyomino))
   (map retrieveCanonicalForm)))

(defonce CHUNK 100)

(defonce GENERATORS
  {:tesser
   (fn [polyominoes]
     (->> (t/map #(into [] (fromOnePolyomino %)))
          (t/into [])
          (t/tesser (t/chunk CHUNK polyominoes))
          (apply concat)
          (into #{})))

   :pmap
   (fn [polyominoes]
     (->> polyominoes
          (pmap #(into [] (fromOnePolyomino %)))
          (apply concat)
          (into #{})))

   :transducer
   (fn [polyominoes]
     (->> polyominoes
          (r/mapcat #(into [] (fromOnePolyominoTransducer %) %))
          (into #{})))

   :reducer
   (fn [polyominoes]
     (->> polyominoes
          (r/mapcat #(into [] (fromOnePolyomino %)))
          (into #{})))})

(defn generate
  ([generator]
   (let [initialResult [[[0 0]]]]
     (cons initialResult (generate generator initialResult))))
  ([generator polyominoes]
   (let [generated (generator polyominoes)]
     (lazy-seq (cons generated (generate generator generated))))))

(defn nbOfPolyominoes
  [{:keys [cells generator]
    :or   {generator :tesser}}]
  {:pre [(number? cells) (> cells 0)]}
  (count (nth (generate (get GENERATORS generator (:tesser GENERATORS))) (dec cells))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [N (-> args
              first
              Integer/parseInt)]
    (nbOfPolyominoes {:cells N})))

(comment

  (nbOfPolyominoes {:cells 5})
  (nbOfPolyominoes {:cells 5 :generator :transducer})
  (nbOfPolyominoes {:cells 5 :generator :reducer})
  (nbOfPolyominoes {:cells 12})
  (nbOfPolyominoes {:cells 12 :generator :reducer})

  )
