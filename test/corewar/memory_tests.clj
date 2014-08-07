(ns corewar.memory-tests
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [corewar.memory :as mem]
    [corewar.constants :as const]))

(def +test-runs+ 1000)

(defspec clock-range-length +test-runs+
  (prop/for-all [start gen/nat
                 length gen/nat
                 max-size (gen/such-that pos? gen/nat)]
    (let [rng (mem/clock-range start (+ start length) max-size)]
      (= length  (count rng)))))

(defspec clock-range-limits +test-runs+
  (prop/for-all [start gen/nat
                 length (gen/such-that pos? gen/nat)
                 max-size (gen/such-that pos? gen/nat)]
    (let [rng (mem/clock-range start (+ start length) max-size)]
      (<= 0 (reduce min rng) (reduce max rng) max-size))))

;(defspec make-configuration-length +test-runs+
;  (prop/for-all [core-size (gen/such-that pos? gen/nat)
;                 program-sizes (gen/such-that not-empty (gen/list (gen/such-that pos? gen/nat)))]
;    (let [configurations (make-configuration-length core-size program-sizes)]
;      (= (count program-sizes) (count configurations)))))

