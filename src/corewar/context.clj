(ns corewar.context
  (:require
    [corewar.constants :as const]
    [corewar.instruction-set :as instr]))

(defn read-memory
  "Extracts the current instruction from the context, returns memory[index]"
  [{:keys [memory index]}]
  (memory index))

(defn write-memory
  "Updates the memory at the given address in the context. Also adds
   the address to an :updated vector"
  [context address value]
  (->
    context
    (assoc-in [:memory address] value)
    (update-in [:updated] conj address)))

(defn inc-index
  "Non-destructive incrementing update on the index/address-pointer, ensuring that the
   index always wraps round the limit of the memory"
  [{:keys [memory] :as context}]
  (let [inc-mod  #(mod (inc %) const/core-size)]
    (update-in context [:index] inc-mod)))

(defn set-index
  [{:keys [memory index] :as context} delta]
  (assoc context
    :index (mod (+ index delta) const/core-size)))
