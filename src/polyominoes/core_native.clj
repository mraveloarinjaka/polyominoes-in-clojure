(ns polyominoes.core-native
  (:require [borkdude.deflet :refer [deflet]]
            [taoensso.timbre :as log]
            [tech.v3.datatype.argops :as ops]
            [tech.v3.libs.neanderthal]
            [uncomplicate.commons.core :refer [release with-release] :as ccore]
            [uncomplicate.neanderthal
             [core :as ucore]
             [math :as math]
             [native :as native]
             [auxil :as auxil]]))

(log/set-ns-min-level! :info)

(defn rotate90!
  [polyomino]
  (let [xs (ucore/row polyomino 0)
        ys (ucore/row polyomino 1)]
    (ucore/rot! xs ys 0 -1)
    polyomino))

(defn rotate180!
  [polyomino]
  (let [xs (ucore/row polyomino 0)
        ys (ucore/row polyomino 1)]
    (ucore/rot! xs ys -1 0)
    polyomino))

(defn rotate270!
  [polyomino]
  (let [xs (ucore/row polyomino 0)
        ys (ucore/row polyomino 1)]
    (ucore/rot! xs ys 0 1)
    polyomino))

(defn mirror!
  [polyomino]
  (let [m (native/dge 2 2 [1 0 0 -1])]
    (ucore/mm m polyomino)))

(defn apply-xf
  ([xf mirror?]
   (let [mirror (if mirror? mirror! identity)]
     (comp mirror
           xf
           ucore/copy)))
  ([xf]
   (apply-xf xf false)))

(defn ->all-forms
  [polyomino]
  (let [all-xfs (juxt identity
                      (apply-xf rotate90!)
                      (apply-xf rotate180!)
                      (apply-xf rotate270!)
                      (comp mirror! ucore/copy)
                      (apply-xf rotate90! true)
                      (apply-xf rotate180! true)
                      (apply-xf rotate270! true))]
    (all-xfs polyomino)))

(def R2 2)

(defn ->to-origin-v
  [polyomino]
  (let [N (ucore/ncols polyomino)
        xs (ucore/row polyomino 0)
        ys (ucore/row polyomino 1)
        min-ix (ucore/imin xs)
        min-iy (ucore/imin ys)
        min-x (ucore/entry xs min-ix)
        min-y (ucore/entry ys min-iy)]
    (native/dge R2 N (cycle [min-x min-y]))))

(defn ->to-origin!
  [polyomino]
  (with-release [to-origin-v (->to-origin-v polyomino)]
    (ucore/axpby! -1 to-origin-v 1 polyomino)))

#_(let [polyomino (native/dge R2 2 [1 1 1 2])]
    (->to-origin! polyomino))
; #RealGEMatrix[double, mxn:2x2, layout:column]
;    ▥       ↓       ↓       ┓    
;    →       0.00    0.00         
;    →       0.00    1.00         
;    ┗                       ┛    
; 

(defn ->cols-hash-base-N
  [polyomino]
  (with-release [N (ucore/ncols polyomino)
                 n-v (native/dv [1 N])]
    (ucore/mv (ucore/trans polyomino) n-v)))

#_(with-release [polyomino (native/dge R2 3 [0 1 1 1 1 2])]
    (->cols-hash-base-N (->to-origin! polyomino)))
; #RealBlockVector[double, n:3, stride:1]
; [   0.00    1.00    4.00 ]
; 

(defn ->permutation-idx
  [polyomino]
  (with-release [hash-base-N (->cols-hash-base-N polyomino)]
    (->> hash-base-N
         ops/argsort
         (mapv inc)
         native/iv)))

#_(with-release [polyomino (native/dge R2 3 [1 1 1 2 0 1])
                 idx (->permutation-idx polyomino)]
    (print idx))

(defn ->canonical-repr!
  [polyomino]
  (with-release [permutation-idx (->permutation-idx (->to-origin! polyomino))]
    (auxil/permute-cols! polyomino permutation-idx)))

#_(with-release [polyomino (native/dge R2 3 [1 1 1 2 0 1])]
    (->canonical-repr! polyomino)
    (print polyomino))

(defn lexicographic-compare
  [p1 p2]
  (let [N (ucore/ncols p1)]
    (with-release [v1 (->cols-hash-base-N p1)
                   v2 (->cols-hash-base-N p2)]
      (loop [i 0]
        (if (< i N)
          (let [x1 (ucore/entry v1 i)
                x2 (ucore/entry v2 i)]
            (cond
              (math/f< x1 x2) -1
              (math/f< x2 x1) 1
              :else (recur (inc i))))
          0)))))

(defn ->canonical-form!
  [polyomino]
  (with-release [all-forms (->all-forms polyomino)]
    (let [canonical-form (->> all-forms
                              (mapv ->canonical-repr!)
                              (apply sorted-set-by lexicographic-compare)
                              first)]
      (ucore/copy! canonical-form polyomino))))

#_(with-release [polyomino (native/dge R2 3 [1 1 1 2 0 1])]
    (->canonical-form! polyomino)
    (print polyomino))

(def NB-NEIGHBORS 4)
(def NB-ROWS-PER-NEIGHBOR 2)

(def TRANSLATIONS-TO-NEIGHBORS (native/dge NB-ROWS-PER-NEIGHBOR NB-NEIGHBORS [0 1 -1 0 0 -1 1 0]))

(defn ->one-point-neighbors
  [xy]
  (let [xy4 (native/dge NB-ROWS-PER-NEIGHBOR NB-NEIGHBORS (cycle xy))]
    (ucore/axpy! TRANSLATIONS-TO-NEIGHBORS xy4)))

#_(with-release [origin (native/dge R2 1 [1 1])]
    (->one-point-neighbors origin))
; #RealGEMatrix[double, mxn:2x4, layout:column]
;    ▥       ↓       ↓       ↓       ↓       ┓    
;    →       1.00    0.00    1.00    2.00         
;    →       2.00    1.00    0.00    1.00         
;    ┗                                       ┛    
; 

(defn ith-point-kth-neighbor-starting-line
  [ith kth]
  (let [number-of-rows-for-neighbors (* NB-NEIGHBORS NB-ROWS-PER-NEIGHBOR)]
    (+ (* ith number-of-rows-for-neighbors) (* NB-ROWS-PER-NEIGHBOR kth))))

(defn ->ith-point-kth-neighbor
  [neighbors ith kth mcols]
  (ucore/submatrix neighbors (ith-point-kth-neighbor-starting-line ith kth) 0 2 mcols))

(defn ->neighbors
  [polyomino]
  (let [N (ucore/ncols polyomino)
        neighbors (native/dge (* NB-NEIGHBORS NB-ROWS-PER-NEIGHBOR N) (inc N))]
    (doseq [i (range N)
            :let [ith-xy (ucore/col polyomino i)
                  ith-xy-neighbors (->one-point-neighbors ith-xy)]
            k (range NB-NEIGHBORS)
            :let [neighbor (->ith-point-kth-neighbor neighbors i k (inc N))
                  neighbor-last-column (ucore/col neighbor N)]]
      (ucore/copy! polyomino (ucore/submatrix neighbor R2 N))
      (ucore/copy! (ucore/col ith-xy-neighbors k) neighbor-last-column)
      (->canonical-form! neighbor))
    neighbors))

#_(with-release [origin (native/dge R2 1 [0 0])]
    (->neighbors origin))

(defn has-duplicate?
  [previous-point current-point]
  (if (= previous-point current-point)
    (reduced {:duplicate current-point})
    current-point))

(defn valid?
  [polyomino]
  (let [invalid-point (native/dv -2 -2)]
    (nil? (:duplicate (reduce has-duplicate? invalid-point (ucore/cols polyomino))))))

(defn valid-neighbors
  [neighbors]
  (let [M (ucore/mrows neighbors)
        nb-neighbors (/ M NB-ROWS-PER-NEIGHBOR)
        N (ucore/ncols neighbors)]
    (for [neighbor-idx (range nb-neighbors)
          :let [neighbor (ucore/submatrix neighbors (* neighbor-idx NB-ROWS-PER-NEIGHBOR) 0 R2 N)]
          :when (valid? neighbor)]
      neighbor)))

#_(with-release [origin (native/dge R2 1 [1 1])]
    (valid-neighbors (->neighbors origin)))

(defn from-one-polyomino
  [polyomino]
  (with-release [neighbors (->neighbors polyomino)]
    (mapv ucore/copy (valid-neighbors neighbors))))

#_(with-release [origin (native/dge R2 1 [1 1])]
    (->> (from-one-polyomino origin)
         (apply sorted-set-by lexicographic-compare)))

(defn from-polyominoes
  [polyominoes]
  (log/debug :from-polyominoes)
  (with-release [generated (->> polyominoes
                                (pmap from-one-polyomino)
                                (apply concat))]
    (for [polyomino (apply sorted-set-by lexicographic-compare generated)]
      (ucore/copy polyomino))))

(defn generate
  ([]
   (let [initial (vector (native/dge R2 1 [0 0]))]
     (lazy-seq (cons initial (generate initial)))))
  ([polyominoes]
   (let [next-batch (from-polyominoes polyominoes)]
     (lazy-seq (cons next-batch (generate next-batch))))))

(defn count-n
  [N]
  (log/debug :count-n N)
  (loop [n 1 polyominoes (vector (native/dge R2 1 [0 0]))]
    (if (< n N)
      (let [next-polyominoes (from-polyominoes polyominoes)]
        (log/debug :first (first next-polyominoes))
        (log/debug :last (last next-polyominoes))
        (release polyominoes)
        (recur (inc n) next-polyominoes))
      (let [result (count polyominoes)]
        (release polyominoes)
        result))))

(defn -main
  [& args]
  (let [cells (parse-long (first args))]
    (println "There are" (count-n cells) "polyominoes with" cells "squares.")
    (shutdown-agents)))

(comment

  (deflet

    (count-n 8)

    #_>)

  (deflet

    (take 3 (generate))

    #_>)

  (comment))

