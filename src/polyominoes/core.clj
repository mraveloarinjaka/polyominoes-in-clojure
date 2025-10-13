(ns polyominoes.core
  (:require [babashka.cli :as cli]
            [clojure.core.reducers :as r]
            [clojure.pprint :as pp]
            [clojure.set]
            [methodical.core :as m]
            [polyominoes.generator :as gen]
            [taoensso.timbre :as log]))

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

(defn- rotate-point
  "Rotate a single point by k*90 degrees CCW."
  [k [x y]]
  (case (mod k 4)
    0 [x y]
    1 [(- y) x]
    2 [(- x) (- y)]
    3 [y (- x)]))

(defn- rotate-by-k
  "Return a function that rotates a polyomino by k*90 degrees CCW."
  [k]
  (fn [polyomino]
    (mapv (partial rotate-point k) polyomino)))

(defn- mirror
  [polyomino]
  (mapv
   (fn [[x y]] [(* x -1) y])
   polyomino))

(defn- retrieveRotationsAndMirror
  [polyomino]
  {:pre [(seq polyomino)]}
  (let [rots (mapv (fn [k] ((rotate-by-k k) polyomino)) (range 4))
        mirrors (mapv mirror rots)]
    (into [] (concat rots mirrors))))

(defn- retrieveCanonicalForm
  [polyomino]
  (->> polyomino
       retrieveRotationsAndMirror
       (r/map (comp translateToOrigin sort))
       (into (sorted-set))
       first))

#_(retrieveCanonicalForm [[0 1] [1 1] [0 0] [1 2]])
#_(retrieveCanonicalForm #{[0 1] [1 1] [0 0] [1 2]})

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

(m/defmethod gen/generate :default
  [starting-from {::gen/keys [generate-from-one]}]
  (->> starting-from
       (pmap #(into [] (generate-from-one %)))
       (apply concat)
       (into #{})))

(m/defmethod gen/generate :before :default
  [_ {generator ::gen/type
      nb-calls :nb-calls
      :as args}]
  (log/debug (pp/cl-format nil "generator ~a called ~r time~:p" generator nb-calls))
  args)

(defn- generate
  [args]
  (let [base-input (assoc args
                          ::gen/neighbors neighbors
                          ::gen/retrieve-canonical-form retrieveCanonicalForm
                          ::gen/generate-from-one fromOnePolyomino
                          ::gen/generate-from-one-xf fromOnePolyominoTransducer)
        step-fn (fn [{:keys [polyominoes nb-calls]}]
                  (let [input (assoc base-input :nb-calls nb-calls)
                        next-gen (gen/generate polyominoes input)]
                    {:polyominoes next-gen
                     :nb-calls (inc nb-calls)}))]
    (->> {:polyominoes [[[0 0]]] :nb-calls 0}
         (iterate step-fn)
         (map :polyominoes))))

(defn nbOfPolyominoes
  {:org.babashka/cli {:coerce {:cells :long
                               :generator :keyword}
                      :args->opts [:cells]}}
  [{:keys [cells generator]
    :or {generator :default}
    :as args}]
  {:pre [(number? cells) (> cells 0)]}
  (count (nth (generate (assoc args ::gen/type generator)) (dec cells))))

(defn -main
  [& args]
  (let [cli-spec (get (meta #'nbOfPolyominoes) :org.babashka/cli)]
    (println (nbOfPolyominoes (cli/parse-opts args cli-spec)))))

(comment

  (-main "5" :generator "tesser")

  (nbOfPolyominoes {:cells 6})
  (nbOfPolyominoes {:cells 5
                    :generator :transducer})
  (nbOfPolyominoes {:cells 12
                    :generator :tesser})
  (nbOfPolyominoes {:cells 5
                    :generator :reducer})

  (comment))
