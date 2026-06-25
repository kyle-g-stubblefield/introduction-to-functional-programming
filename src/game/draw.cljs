(ns game.draw
  "Rendering functions. Side effects live here and only here.
  
  draw-state receives the current state and renders it to the canvas.
  It must never modify state — only read from it.
  
  This separation is the core FP architecture insight:
    pure logic  →  game.state
    side effects →  game.draw  (this file)"
  (:require [quil.core :as q]
            [game.state :as state]))

;; ---------------------------------------------------------------------------
;; Color palette
;; ---------------------------------------------------------------------------

(def background-color [30 30 46])    ; dark navy
(def floor-color      [69 71 90])    ; muted purple-grey
(def player-color     [137 180 250]) ; soft blue
(def text-color       [205 214 244]) ; near white
(def accent-color     [243 139 168]) ; pink

;; ---------------------------------------------------------------------------
;; Component draw functions
;; ---------------------------------------------------------------------------
;; Each function draws one part of the scene.
;; Convention: draw-* functions take only what they need, not the whole state.

(defn draw-background []
  (apply q/background background-color))

(defn draw-floor []
  (apply q/fill floor-color)
  (q/no-stroke)
  (q/rect 0 (- state/canvas-height 10)
          state/canvas-width 10))

(defn draw-player [player]
  (apply q/fill player-color)
  (q/no-stroke)
  (q/ellipse (:x player)
             (:y player)
             (* 2 (:radius player))
             (* 2 (:radius player))))

(defn draw-score [score]
  (apply q/fill text-color)
  (q/text-size 20)
  (q/text (str "Score: " score) 20 36))

(defn draw-controls []
  (apply q/fill (conj text-color 120)) ; semi-transparent
  (q/text-size 14)
  (q/text "← → move   ↑ jump   space stop   p pause   r reset"
          20
          (- state/canvas-height 20)))

(defn draw-pause-overlay []
  (q/fill 0 0 0 140)
  (q/rect 0 0 state/canvas-width state/canvas-height)
  (apply q/fill accent-color)
  (q/text-size 48)
  (q/text-align :center :center)
  (q/text "PAUSED" (/ state/canvas-width 2) (/ state/canvas-height 2))
  (q/text-align :left :baseline))

;; ---------------------------------------------------------------------------
;; Main draw function — called every frame by quil
;; ---------------------------------------------------------------------------

(defn draw-state
  "GameState -> nil (side effects only)
  Renders the current state to the canvas.
  The to-draw equivalent from big-bang."
  [state]
  (draw-background)
  (draw-floor)
  (draw-player (:player state))
  (draw-score (:score state))
  (draw-controls)
  (when (:paused? state)
    (draw-pause-overlay)))
