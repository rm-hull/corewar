(ns corewar.instruction-set
  "Encodes the instruction set as part of the assembly process.

   Core War programs are written in an assembly-type language called Redcode.
   The eight instructions included in the version of the language presented
   here are by no means the only ones possible; indeed, the original
   implementation of Core War, done on a minicomputer, had a larger instruction
   set. If there are many kinds of instructions, however, the encoded form of
   each instruction takes up more space, and so the area of memory needed for
   CORE must be larger. Mars, the program that interprets Redcode programs,
   also grows as the size of the instruction set increases. The complexity of
   your Core War implementation may be constrained by the amount of memory
   available in your computer.

   If you choose to a create your own Redcode instruction set, two points
   should be kept in mind. First, each Redcode instruction must occupy a single
   location in CORE. In many assembly languages an instruction can extend over
   multiple addresses, but not in Redcode. Second, there are no registers
   available for Redcode programs; all data are kept in CORE and manipulated
   there."
  (:require
    [clojure.string :as str]
    [corewar.constants :as const]
    [corewar.addressing-mode :as addr]))

(def encoded-form {
  :dat 0x00
  :mov 0x01
  :add 0x02
  :sub 0x03
  :jmp 0x04
  :jmz 0x05
  :djz 0x06
  :cmp 0x07
})

(def ^:private inverted-form (into {} (map (fn [[k v]] [v k]) encoded-form)))

(defn ^:private encode [opcode a b]
  (when-let [code (encoded-form opcode)]
    (bit-or
      (bit-shift-left (bit-and code const/opcode-mask) const/opcode-position)
      (bit-shift-left (bit-and a const/operand-mask) const/operand-position)
      (bit-shift-left (bit-and b const/operand-mask) 0))))

(defn dat
  "Initialize location to value B."
  [b]
  (encode :dat addr/undefined b))

(defn mov
  "Move A into location B."
  [a b]
  (encode :mov a b))

(defn add
  "Add operand A to contents of location B and store result in location B."
  [a b]
  (encode :add a b))

(defn sub
  "Subtract operand A to contents of location B and store result in location B."
  [a b]
  (encode :sub a b))

(defn jmp
  "Jump to location B."
  [b]
  (encode :jmp addr/undefined b))

(defn jmz
  "If operand A is 0, jump to location B; otherwise continue with next
   instruction."
  [a b]
  (encode :jmz a b))

(defn djz
  "Decrement contents of location A by 1. If location A now holds 0,
   jump to location B; otherwise continue with next instruction."
  [a b]
  (encode :djz a b))

(defn cmp
  "Compare operand A with operand B. If they are not equal, skip next
   instruction; otherwise continue with next instruction."
  [a b]
  (encode :cmp a b))

(defn opcode [instr]
  (inverted-form
    (bit-shift-right instr const/opcode-position)))

(defn operand-a [instr]
  (bit-and
    const/operand-mask
    (bit-shift-right instr const/operand-position)))

(defn operand-b [instr]
  (bit-and const/operand-mask instr))

(defn valid? [instr]
  (and
    (opcode instr)
    (valid? (operand-a instr))
    (valid? (operand-b instr))
    ; TODO: also need to cross-check A & B's addressing modes
    ))

(defn to-string [instr]
  (str/join " "
    (remove nil?
      (list
        (name (opcode instr))
        (addr/to-string (operand-a instr))
        (addr/to-string (operand-b instr))))))

(defn parse
  ([opcode operand-b]
   (parse opcode nil operand-b))
  ([opcode operand-a operand-b]
    (encode
      (keyword (str/lower-case opcode))
      (addr/parse operand-a)
      (addr/parse operand-b))))
