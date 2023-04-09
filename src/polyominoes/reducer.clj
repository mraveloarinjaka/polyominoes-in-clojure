(ns polyominoes.reducer
  (:require [polyominoes.generator :as gen]
            [clojure.core.reducers :as r]))

(defmethod gen/generate :reducer
  [{::gen/keys [starting-from generate-from-one]}]
  (println ::generate)
  (->> starting-from
       (r/mapcat generate-from-one)
       (into #{})))
