(ns polyominoes.generator.tesser
  (:refer-clojure :exclude [filter map map mapcat set])
  (:require [methodical.core :as m]
            [polyominoes.generator :as gen]
            [tesser.core :as t]))

(def CHUNK 10)

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- fromOnePolyomino
  [polyomino neighbors retrieve-canonical-form]
  (let [polyomino-len (count polyomino)
        polyomino-set (clojure.core/set polyomino)]
    (->> (t/mapcat neighbors)
         (t/map (partial conj polyomino-set))
         (t/filter #(< polyomino-len (count %)))
         (t/map retrieve-canonical-form)
         (t/set)
         (t/tesser (t/chunk 1 polyomino)))))

#_{:clj-kondo/ignore [:unresolved-var]}
(m/defmethod gen/generate :tesser
  [starting-from {::gen/keys [neighbors retrieve-canonical-form]}]
  (->> (t/mapcat #(fromOnePolyomino % neighbors retrieve-canonical-form))
       (t/set)
       (t/tesser (t/chunk CHUNK starting-from))))

(comment

  (comment))
