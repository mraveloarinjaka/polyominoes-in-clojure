(ns polyominoes.generator.tesser
  (:require [methodical.core :as m]
            [polyominoes.generator :as gen]
            [tesser.core :as t]))

(defonce CHUNK 100)

(m/defmethod gen/generate :tesser
  [starting-from  {::gen/keys [generate-from-one]}]
  (->> (t/map #(into [] (generate-from-one %)))
       (t/into [])
       (t/tesser (t/chunk CHUNK starting-from))
       (apply concat)
       (into #{})))
