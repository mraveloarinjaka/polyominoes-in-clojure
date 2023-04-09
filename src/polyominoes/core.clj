(ns polyominoes.core
  (:require [clojure.core.reducers :as r]
            [polyominoes.generator :as gen]
            [clojure.set]))

(defn- findOrigin
  [polyomino]
  (reduce
   (fn [[resX resY] [x y]] [(min resX x) (min resY y)])
   polyomino))

(defn- translateToOrigin
  [polyomino]
  (let [[originX originY] (findOrigin polyomino)]
    (mapv
     (fn [[x y]] [(- x originX) (- y originY)])
     polyomino)))

(defn- rotateOnePoint90
  [[x y]]
  [(* y -1) x])

(defn- rotateOnePoint180
  [[x y]]
  [(* x -1) (* y -1)])

(defn- rotateOnePoint270
  [[x y]]
  [y (* x -1)])

(defn- rotate
  ([rotation polyomino]
   (mapv rotation polyomino))
  ([rotation]
   (fn [polyomino]
     (rotate rotation polyomino))))

(defn- mirror
  [polyomino]
  (mapv
   (fn [[x y]] [(* x -1) y])
   polyomino))

(defn- retrieveRotationsAndMirror
  [polyomino]
  ((juxt
    identity
    (rotate rotateOnePoint90)
    (rotate rotateOnePoint180)
    (rotate rotateOnePoint270)
    mirror
    (comp (rotate rotateOnePoint90) mirror)
    (comp (rotate rotateOnePoint180) mirror)
    (comp (rotate rotateOnePoint270) mirror))
   polyomino))

(defn- retrieveCanonicalForm
  [polyomino]
  (->> polyomino
       retrieveRotationsAndMirror
       (r/map (comp translateToOrigin sort))
       (into (sorted-set))
       first))

#_(retrieveCanonicalForm [[0 1] [1 1] [0 0] [1 2]])

(defn- neighbors
  [[x y]]
  [[(dec x) y]
   [(inc x) y]
   [x (dec y)]
   [x (inc y)]])

(defn- fromOnePolyomino
  [polyomino]
  (->> polyomino
       (r/mapcat neighbors)
       (r/remove (set polyomino))
       (r/map (partial conj polyomino))
       (r/map retrieveCanonicalForm)))

#_(let [res (fromOnePolyomino [[0 1] [1 1] [0 0] [1 2]])]
    (into [] res))

(defn- fromOnePolyominoTransducer
  [polyomino]
  (comp
   (mapcat neighbors)
   (remove (set polyomino))
   (map (partial conj polyomino))
   (map retrieveCanonicalForm)))

(defmethod gen/generate :default
  [{::gen/keys [starting-from generate-from-one]}]
  (println ::generate)
  (->> starting-from
       (pmap #(into [] (generate-from-one %)))
       (apply concat)
       (into #{})))

(defn- generate
  ([args]
   (let [initial-result [[[0 0]]]]
     (lazy-seq (cons initial-result (generate args initial-result)))))
  ([args polyominoes]
   (let [input (assoc args
                      ::gen/starting-from polyominoes
                      ::gen/generate-from-one fromOnePolyomino
                      ::gen/generate-from-one-xf fromOnePolyominoTransducer)
         generated (gen/generate input)]
     (lazy-seq (cons generated (generate args generated))))))

(defn nbOfPolyominoes
  {:org.babashka/cli {:coerce {:cells :long}}}
  [{:keys [cells generator] :as args}]
  {:pre [(number? cells) (> cells 0)]}
  (count (nth (generate (assoc args ::gen/type generator)) (dec cells))))

(comment

  (nbOfPolyominoes {:cells 10})
  (nbOfPolyominoes {:cells 5 :generator :transducer})
  (nbOfPolyominoes {:cells 5 :generator :reducer})
  (nbOfPolyominoes {:cells 5 :generator :tesser})

  (comment))
