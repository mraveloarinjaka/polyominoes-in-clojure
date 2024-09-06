(ns polyominoes.generator
  (:require [methodical.core :as m]))

(defn- dispatch-on-generator
  [_ {::keys [type]}]
  type)

(m/defmulti generate dispatch-on-generator)
