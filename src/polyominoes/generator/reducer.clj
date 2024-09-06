(ns polyominoes.generator.reducer
  (:require [clojure.core.reducers :as r]
            [methodical.core :as m]
            [polyominoes.generator :as gen]))

(m/defmethod gen/generate :reducer
  [starting-from {::gen/keys [generate-from-one]}]
  (->> starting-from
       (r/mapcat generate-from-one)
       (into #{})))
