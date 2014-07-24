(ns corewar.parser
  (:require
    [clojure.string :as str]
    [corewar.instruction-set :as instr]
    [corewar.addressing-mode :as addr]))

(defn read-file [file]
  (->>
    (slurp file)
    str/split-lines
    (remove empty?)))

(defn add-line-numbers [program]
  (map vector (iterate inc 1) program))

(defn extract-label [[line-no prog-line]]
  (when-let [[_ label] (re-find #"^([a-zA-Z][a-zA-Z0-9_]*?):" prog-line)]
    [label line-no]))

(defn strip-label [prog-line]
  (if-let [[_ label stripped-code] (re-find #"(^[a-zA-Z0-9_]+:)?\s*(.*)" prog-line)]
    stripped-code
    prog-line))

(defn strip-comment [prog-line]
  (let [idx (.indexOf prog-line ";")]
    (if (neg? idx)
      prog-line
      (subs prog-line 0 idx))))

(defn get-labels [annotated-program]
  (->>
    annotated-program
    (map extract-label)
    (remove nil?)
    (into {})))

(defn make-label-resolver [annotated-program]
  (let [symbol-table (get-labels annotated-program)]
    (fn [label line-no]
      (when-not (empty? label)
        (if-let [ln (symbol-table label)]
          (- ln line-no)
          label)))))

(defn metadata-scraper [[line-no prog-line] label-resolver]
  (when-let [[_ k v] (re-find #"^;([a-z]*)[ -]+(.*)" prog-line)]
    {(keyword k) v}))

(defn tokens [prog-line]
  (->
    prog-line
    strip-label
    strip-comment
    str/trim
    (str/split #"[ ,\t]+")))

(defn pseudo-opcode [[line-no prog-line] label-resolver]
  (let [[opcode & operands] (tokens prog-line)
        operands (map #(label-resolver % line-no) operands)]
    (condp = opcode
      "org" {:start (dec (first operands))}
      ; TODO : add other pseudo ops here
      nil)))

(defn assemble-instruction [[line-no prog-line] label-resolver]
  (let [[opcode & operands] (tokens prog-line)
        operands (map #(label-resolver % line-no) operands)]
    (when (and opcode (pos? (count operands)))
      (when-let [instr (apply instr/parse opcode operands)]
        {:instr [instr]}))))

(defn parse-line [line label-resolver]
  (merge
    (metadata-scraper line label-resolver)
    (pseudo-opcode line label-resolver)
    (assemble-instruction line label-resolver)))

(defn assemble [program-filename]
  (let [annotated-program (add-line-numbers (read-file program-filename))
        label-resolver (make-label-resolver annotated-program)]
    (->>
      annotated-program
      (map #(parse-line % label-resolver))
      (reduce (partial merge-with concat)))))

(assemble "resources/dwarf.red")
