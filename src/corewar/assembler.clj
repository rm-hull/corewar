(ns corewar.assembler
  (:require
    [clojure.string :as str]
    [corewar.instruction-set :as instr]
    [corewar.addressing-mode :as addr]))

(defn ^:private ignore-exclusions? [^String prog-line]
  (or
    (.startsWith prog-line ";redcode-")
    (.startsWith prog-line ";name ")
    (.startsWith prog-line ";author ")
    (.startsWith prog-line ";strategy ")))

(defn ^:private strip-comment [^String prog-line]
  (let [idx (.indexOf prog-line ";")]
    (if (or (neg? idx))
      prog-line
      (subs prog-line 0 idx))))

(defn ^:private strip-non-metadata-comment [prog-line]
  (if (ignore-exclusions? prog-line)
    prog-line
    (strip-comment prog-line)))

(defn ^:private read-source [source-code]
  (->>
    (str/split-lines source-code)
    (map (comp str/trim strip-non-metadata-comment))
    (remove empty?)))

(defn ^:private add-line-numbers [program]
  (map vector (iterate inc 1) program))

(defn ^:private extract-label [[line-no prog-line]]
  (when-let [[_ label] (re-find #"^([a-zA-Z][a-zA-Z0-9_]*?):" prog-line)]
    [label line-no]))

(defn ^:private strip-label [prog-line]
  (if-let [[_ label stripped-code] (re-find #"(^[a-zA-Z][a-zA-Z0-9_]+:)?\s*(.*)" prog-line)]
    stripped-code
    prog-line))

(defn ^:private get-labels [annotated-program]
  (->>
    annotated-program
    (map extract-label)
    (remove nil?)
    (into {})))

(defn ^:private make-label-resolver [annotated-program]
  (let [symbol-table (get-labels annotated-program)]
    (fn [label line-no]
      (when-not (empty? label)
        (if-let [ln (symbol-table label)]
          (- ln line-no)
          label)))))

(defn ^:private metadata-scraper [[line-no prog-line] label-resolver]
  (when-let [[_ k v] (re-find #"^;([a-z]*)[ -]+(.*)" prog-line)]
    {(keyword k) v}))

(defn ^:private tokenize [prog-line]
  (->
    prog-line
    strip-label
    strip-comment
    str/trim
    (str/split #"[ ,\t]+")))

(defn ^:private pseudo-opcode [[line-no prog-line] label-resolver]
  (let [[opcode & operands] (tokenize prog-line)
        operands (map #(label-resolver % line-no) operands)]
    (condp = opcode
      "org" {:start (dec (first operands))}
      ; TODO : add other pseudo ops here
      nil)))

(defn ^:private assemble-instruction [[line-no prog-line] label-resolver]
  (let [[opcode & operands] (tokenize prog-line)
        operands (map #(label-resolver % line-no) operands)]
    (when (and opcode (pos? (count operands)))
      (when-let [instr (apply instr/parse opcode operands)]
        {:instr [instr]}))))

(defn ^:private parse-line [line label-resolver]
  (merge
    (metadata-scraper line label-resolver)
    (pseudo-opcode line label-resolver)
    (assemble-instruction line label-resolver)))

(defn assemble
  "Builds an assembly from the given redcode program. Returns a map comprising:

     :instr - a sequence of machine code instructions
     :start - an offset in :instr where the program should begin execution

   Optional:

     :name  - the redcode program data (scraped from metadata)
     :author - the prescribed author (scraped from metadata)
     :strategy - notes associated with the strategy employed (scraped from metadata)
     :redcode - the spec version used.  "
  [source-code]
  (let [annotated-program (add-line-numbers (read-source source-code))
        label-resolver (make-label-resolver annotated-program)]
    (->>
      annotated-program
      (map #(parse-line % label-resolver))
      (reduce (partial merge-with concat)))))

(defn disassemble
  "Takes a list of machine code instructions and produces an assembly listing"
  [machine-code]
  (map instr/to-string machine-code))

(comment

  (def assembly (assemble (slurp "resources/dwarf.red")))
  (disassemble (:instr assembly))

)
