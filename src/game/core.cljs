(ns game.core
  "Entry point. Wires together setup/update/draw — the big-bang pattern.
  
  This namespace should stay small. Add game logic to game.state and game.draw."
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [game.state :as state]
            [game.draw  :as draw]))

;; ---------------------------------------------------------------------------
;; Sketch definition
;; ---------------------------------------------------------------------------
;; This is the ClojureScript equivalent of Racket's big-bang:
;;
;;   (big-bang initial-state
;;     [on-tick  update-state]   →  :update  state/update-state
;;     [to-draw  draw-state]     →  :draw    draw/draw-state
;;     [on-key   handle-key])    →  :key-pressed state/handle-key
;;
;; :middleware [m/fun-mode] is what enables the pure functional update pattern.
;; Without it, quil uses mutable state internally. Always include it.

(defn- setup []
  (q/frame-rate 60)
  (state/initial-state))

(q/defsketch game
  :host       "canvas"
  :size       [800 600]
  :setup      setup
  :update     state/update-state
  :draw       draw/draw-state
  :key-pressed state/handle-key
  :middleware  [m/fun-mode])

;; ---------------------------------------------------------------------------
;; Hot reload hook — shadow-cljs calls this after each file save
;; ---------------------------------------------------------------------------
(defn ^:dev/after-load reload []
  ;; quil re-runs the sketch automatically; nothing needed here usually.
  ;; Add any dev-time reset logic if needed.
  )

(defn init []
  ;; Called once when the page first loads (see shadow-cljs.edn :init-fn)
  )
