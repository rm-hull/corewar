(ns corewar.addressing-mode-tests
  (:require
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.generators :as gen]
    [clojure.test.check.properties :as prop]
    [corewar.generators :refer [operand-generator]]
    [corewar.addressing-mode :as addr]
    [corewar.constants :as const]))

(def +test-runs+ 1000)

(defspec immediate-range +test-runs+
  (prop/for-all [v gen/int]
    (let [im (addr/immediate v)]
      (<= 0 im (dec const/core-size)))))

(defspec immediate-encoded-form +test-runs+
  (prop/for-all [v gen/int]
    (let [im (addr/immediate v)]
      (= (addr/encoded-form :immediate)
         (bit-shift-right im const/operand-bits)))))

(defspec immediate-value +test-runs+
  (prop/for-all [v gen/int]
    (let [im (addr/immediate v)]
      (= (addr/value im) (mod v const/core-size)))))

(defspec relative-range +test-runs+
  (prop/for-all [v gen/int]
    (let [re (addr/relative v)]
      (<= 0 (bit-and const/value-mask re) (dec const/core-size)))))

(defspec relative-encoded-form +test-runs+
  (prop/for-all [v gen/int]
    (let [re (addr/relative v)]
      (= (addr/encoded-form :relative)
         (bit-shift-right re const/operand-bits)))))

(defspec relative-value +test-runs+
  (prop/for-all [v gen/int]
    (let [re (addr/relative v)]
      (= (addr/value re) (mod v const/core-size)))))

(defspec indirect-range +test-runs+
  (prop/for-all [v gen/int]
    (let [in (addr/indirect v)]
      (<= 0 (bit-and const/value-mask in) (dec const/core-size)))))

(defspec indirect-encoded-form +test-runs+
  (prop/for-all [v gen/int]
    (let [in (addr/indirect v)]
      (= (addr/encoded-form :indirect)
         (bit-shift-right in const/operand-bits)))))

(defspec indirect-value +test-runs+
  (prop/for-all [v gen/int]
    (let [in (addr/indirect v)]
      (= (addr/value in) (mod v const/core-size)))))

(defspec valid-operand-check +test-runs+
  (prop/for-all [op operand-generator]
    (addr/valid? op)))
