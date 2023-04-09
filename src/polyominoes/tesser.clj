(ns polyominoes.tesser
  (:require [polyominoes.generator :as gen]
            [tesser.core :as t]))

(defonce CHUNK 100)

(defmethod gen/generate :tesser
  [{::gen/keys [starting-from generate-from-one]}]
  (println ::generate)
  (->> (t/map #(into [] (generate-from-one %)))
       (t/into [])
       (t/tesser (t/chunk CHUNK starting-from))
       (apply concat)
       (into #{})))
