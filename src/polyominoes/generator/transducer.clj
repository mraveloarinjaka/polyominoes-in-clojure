(ns polyominoes.generator.transducer
  (:require [polyominoes.generator :as gen]
            [clojure.core.reducers :as r]))

(defmethod gen/generate :transducer
  [{::gen/keys [starting-from generate-from-one-xf]}]
  (println ::generate)
  (->> starting-from
       (r/mapcat #(eduction (generate-from-one-xf %) %))
       (into #{})))
