(ns corewar.addressing-mode-tests
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [corewar.addressing-mode :as addr]
    [corewar.constants :as const]))

(def +test-runs+ 1000)

(defspec immediate-range +test-runs+
  (prop/for-all [v gen/int]
    (let [im (addr/immediate v)]
      (<= 0 im (dec const/core-size)))))

(defspec immediate-modulo +test-runs+
  (prop/for-all [v gen/int]
    (let [im (addr/immediate v)]
      (= (mod v const/core-size) im))))

(defspec relative-range +test-runs+
  (prop/for-all [v gen/int]
    (let [re (addr/relative v)]
      (<= 0 (bit-and const/value-mask re) (dec const/core-size)))))

(defspec relative-encoded-form +test-runs+
  (prop/for-all [v gen/int]
    (let [re (addr/relative v)]
      (= addr/relative-encoded-form
         (bit-shift-right re const/operand-bits)))))

(defspec indirect-range +test-runs+
  (prop/for-all [v gen/int]
    (let [in (addr/indirect v)]
      (<= 0 (bit-and const/value-mask in) (dec const/core-size)))))

(defspec indirect-encoded-form +test-runs+
  (prop/for-all [v gen/int]
    (let [in (addr/indirect v)]
      (= addr/indirect-encoded-form
         (bit-shift-right in const/operand-bits)))))
