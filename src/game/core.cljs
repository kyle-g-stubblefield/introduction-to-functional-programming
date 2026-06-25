(ns game.core
  "Entry point. Creates the p5 sketch and wires the big-bang pattern.

  p5 is loaded as a global via <script> tag in index.html.
  We use p5's instance mode so the sketch is self-contained
  and doesn't pollute the global namespace."
  (:require [game.state :as state]
            [game.draw  :as draw]))

;; ---------------------------------------------------------------------------
;; p5 instance mode — the big-bang equivalent
;; ---------------------------------------------------------------------------
;; p5 instance mode takes a function that receives the p5 object (here: `p`).
;; We attach setup/draw/keyPressed to it, then mount to the canvas element.
;;
;; This maps directly onto big-bang:
;;   setup        → returns initial state, stored in an atom
;;   draw         → calls draw/draw-state with current state each frame
;;   keyPressed   → calls state/handle-key, swaps the atom

(defonce !state (atom nil))

(defn- make-sketch [p]
  ;; setup — runs once
  (set! (.-setup p)
        (fn []
          (.createCanvas p state/canvas-width state/canvas-height)
          (.frameRate p 60)
          (reset! !state (state/initial-state))))

  ;; draw — runs every frame, pure read of state
  (set! (.-draw p)
        (fn []
          (draw/draw-state p @!state)))

  ;; keyPressed — pure state transition via swap!
  (set! (.-keyPressed p)
        (fn []
          (swap! !state state/handle-key
                 {:key (keyword (.-key p))}))))

(defonce sketch-instance (atom nil))

(defn init []
  (let [instance (new js/p5 make-sketch
                      (.getElementById js/document "canvas"))]
    (reset! sketch-instance instance)))

;; Hot reload — remove old sketch and restart
(defn ^:dev/after-load reload []
  (when-let [s @sketch-instance]
    (.remove s))
  (init))
