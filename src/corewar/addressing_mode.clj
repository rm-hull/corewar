(ns corewar.addressing-mode
  "Encodes the addressing mode as part of the assembly process"
  (:require [corewar.constants :as const]))

; No need for an immediate encoded-form as it is zero
(def ^:const relative-encoded-form 0x01)
(def ^:const indirect-encoded-form 0x02)

(defn immediate
  "The number is the operand"
  [value]
  (bit-and const/value-mask value))

(defn relative
  "The number specifies an offset from the current instruction. Mars
   adds the offset to the address of the current instruction; the
   number stored at the location reached in this way is the operand."
  [value]
  (bit-or
    (bit-shift-left relative-encoded-form const/operand-bits)
    (immediate value)))

(defn indirect
  "The number specifies an offset from the current instruction to a location
   where the relative address of the operand is found. Mars adds the offset to
   the address of the current instruction and retrieves the number stored at
   the specified location; this number is then interpreted as an offset from
   its own address. The number found at this second location is the operand."
  [value]
  (bit-or
    (bit-shift-left indirect-encoded-form const/operand-bits)
    (immediate value)))
