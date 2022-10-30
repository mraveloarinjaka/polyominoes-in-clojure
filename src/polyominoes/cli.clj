(ns polyominoes.cli
  (:require [polyominoes.core :refer [nbOfPolyominoes]]))

(defn -main
  [& args]
  (println (nbOfPolyominoes args)))

