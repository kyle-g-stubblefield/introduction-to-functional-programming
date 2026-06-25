(ns game.draw
  "Rendering functions. Side effects live here and only here.

  draw-state receives the p5 instance and current state,
  and renders it to the canvas. It never modifies state.

  All p5 calls go through the `p` instance — never via js/p5 globals.
  This is p5 instance mode: safe, encapsulated, no global pollution.

  FP architecture:
    pure logic   → game.state
    side effects → game.draw  (this file)")

;; ---------------------------------------------------------------------------
;; Color helpers
;; ---------------------------------------------------------------------------

(defn- fill! [p r g b]
  (.fill p r g b))

(defn- fill-alpha! [p r g b a]
  (.fill p r g b a))

(defn- no-stroke! [p]
  (.noStroke p))

(defn- stroke! [p r g b]
  (.stroke p r g b))

;; ---------------------------------------------------------------------------
;; Component draw functions
;; ---------------------------------------------------------------------------

(defn- draw-background! [p]
  (.background p 30 30 46))

(defn- draw-floor! [p canvas-height canvas-width]
  (fill! p 69 71 90)
  (no-stroke! p)
  (.rect p 0 (- canvas-height 10) canvas-width 10))

(defn- draw-player! [p player]
  (fill! p 137 180 250)
  (no-stroke! p)
  (.ellipse p
            (:x player)
            (:y player)
            (* 2 (:radius player))
            (* 2 (:radius player))))

(defn- draw-score! [p score]
  (fill! p 205 214 244)
  (.textSize p 20)
  (.text p (str "Score: " score) 20 36))

(defn- draw-controls! [p canvas-height]
  (fill-alpha! p 205 214 244 120)
  (.textSize p 14)
  (.text p
         "← → move   ↑ jump   space stop   p pause   r reset"
         20
         (- canvas-height 20)))

(defn- draw-pause-overlay! [p canvas-width canvas-height]
  (fill-alpha! p 0 0 0 140)
  (.rect p 0 0 canvas-width canvas-height)
  (fill! p 243 139 168)
  (.textSize p 48)
  (.textAlign p (.-CENTER p) (.-CENTER p))
  (.text p "PAUSED" (/ canvas-width 2) (/ canvas-height 2))
  (.textAlign p (.-LEFT p) (.-BASELINE p)))

;; ---------------------------------------------------------------------------
;; Main draw function
;; ---------------------------------------------------------------------------

(defn draw-state
  "p5 GameState -> nil (side effects only)
  Renders the current state to the canvas each frame.
  The to-draw equivalent from big-bang."
  [p state]
  (let [w (.-width p)
        h (.-height p)]
    (draw-background! p)
    (draw-floor! p h w)
    (draw-player! p (:player state))
    (draw-score! p (:score state))
    (draw-controls! p h)
    (when (:paused? state)
      (draw-pause-overlay! p w h))))
