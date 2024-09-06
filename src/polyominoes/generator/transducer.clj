(ns polyominoes.generator.transducer
  (:require [clojure.core.reducers :as r]
            [methodical.core :as m]
            [polyominoes.generator :as gen]))

(m/defmethod gen/generate :transducer
  [starting-from {::gen/keys [generate-from-one-xf]}]
  (->> starting-from
       (r/mapcat #(eduction (generate-from-one-xf %) %))
       (into #{})))
