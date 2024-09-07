(ns polyominoes.generator.tesser
  (:require [methodical.core :as m]
            [polyominoes.generator :as gen]
            [tesser.core :as t]))

(def CHUNK 100)

(m/defmethod gen/generate :tesser
  [starting-from  {::gen/keys [generate-from-one]}]
  (->> (t/mapcat generate-from-one)
       (t/set)
       (t/tesser (t/chunk CHUNK starting-from))))

