(ns polyominoes.generator
  (:require [methodical.core :as m]))

(m/defmulti generate ::type)
