(ns polyominoes.cli
  (:require [polyominoes.core :refer [nbOfPolyominoes]]
            [polyominoes.generator tesser reducer transducer]))

(defn -main
  [& args]
  (println (nbOfPolyominoes args)))

