(ns corewar.addressing-mode
  "Encodes the addressing mode as part of the assembly process.

   There are various ways of specifying memory addresses in an
   assembly-language program. In order to make the execution of a Redcode
   program independent of its position in memory, a special form of relative
   addressing is used. Again, your version of Redcode may have different
   addressing modes or additional ones, although you should be aware when you
   choose modes that Mars will load your Redcode program at an address in CORE
   that cannot be predicted in advance."
 (:require [corewar.constants :as const]))

(def encoded-form {
  :immediate 0x00
  :relative  0x01
  :indirect  0x02
  :undefined 0x03
})

(def repr {
  :immediate #(str \# %)
  :relative str
  :indirect #(str \@ %)
  :undefined (constantly nil)
})

(def ^:private inverted-form (into {} (map (fn [[k v]] [v k]) encoded-form)))

(defn ^:private encode [addressing-mode value]
  (bit-or
    (bit-shift-left (encoded-form addressing-mode) const/operand-bits)
    (bit-and const/value-mask value)))

(def undefined
  (encode :undefined 0))

(defn immediate
  "The number is the operand"
  [value]
  (encode :immediate value))

(defn relative
  "The number specifies an offset from the current instruction. Mars
   adds the offset to the address of the current instruction; the
   number stored at the location reached in this way is the operand."
  [value]
  (encode :relative value))

(defn indirect
  "The number specifies an offset from the current instruction to a location
   where the relative address of the operand is found. Mars adds the offset to
   the address of the current instruction and retrieves the number stored at
   the specified location; this number is then interpreted as an offset from
   its own address. The number found at this second location is the operand."
  [value]
  (encode :indirect value))

(defn addressing-mode [operand]
  (inverted-form
    (bit-shift-right operand const/operand-bits)))

(defn value [operand]
  (bit-and const/value-mask operand))

(defn valid? [operand]
  (not (nil? (addressing-mode operand))))

(defn to-string [operand]
  (when operand
    (when-let [addr-mode (addressing-mode operand)]
      ((repr addr-mode) (value operand)))))
