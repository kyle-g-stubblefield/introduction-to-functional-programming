(ns game.state
  "Pure state functions. No side effects allowed in this namespace.
  
  Every function here takes state and returns new state.
  This is the heart of functional game architecture.
  
  HtDP design recipe is enforced by convention:
    1. Data definition (see below)
    2. Function signature in docstring
    3. Examples in test/game/state_test.cljs
    4. Implementation")

;; ---------------------------------------------------------------------------
;; Data Definitions
;; ---------------------------------------------------------------------------
;; A GameState is a map:
;;   {:player  Player
;;    :score   Number
;;    :paused? Boolean}
;;
;; A Player is a map:
;;   {:x      Number   -- horizontal position in pixels
;;    :y      Number   -- vertical position in pixels
;;    :vx     Number   -- horizontal velocity (pixels per frame)
;;    :vy     Number   -- vertical velocity (pixels per frame)
;;    :radius Number}  -- collision radius in pixels
;;
;; This is the ClojureScript equivalent of Racket structs.
;; Keywords (:x :y :vx :vy) replace struct accessors (posn-x posn-y).

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def canvas-width  800)
(def canvas-height 600)
(def gravity       0.4)
(def jump-force   -10)
(def move-speed    4)

;; ---------------------------------------------------------------------------
;; Initial State
;; ---------------------------------------------------------------------------

(defn initial-player []
  {:x      100
   :y      300
   :vx     0
   :vy     0
   :radius 20})

(defn initial-state []
  {:player  (initial-player)
   :score   0
   :paused? false})

;; ---------------------------------------------------------------------------
;; Pure Update Functions
;; ---------------------------------------------------------------------------
;; Each function: State -> State (or a sub-map -> sub-map)
;; No mutation. No side effects. Always returns a new map.

(defn apply-gravity
  "Player -> Player
  Increases vertical velocity by gravity constant each frame."
  [player]
  (update player :vy + gravity))

(defn apply-velocity
  "Player -> Player
  Moves player by its current velocity."
  [player]
  (-> player
      (update :x + (:vx player))
      (update :y + (:vy player))))

(defn clamp-to-floor
  "Player -> Player
  Prevents player from falling below the canvas floor."
  [player]
  (let [floor (- canvas-height (:radius player))]
    (if (> (:y player) floor)
      (assoc player :y floor :vy 0)
      player)))

(defn on-floor?
  "Player -> Boolean
  Returns true if the player is resting on the floor."
  [player]
  (>= (:y player) (- canvas-height (:radius player))))

(defn update-player
  "Player -> Player
  Applies all physics rules in order."
  [player]
  (-> player
      apply-gravity
      apply-velocity
      clamp-to-floor))

(defn update-state
  "GameState -> GameState
  Called every frame by quil. The on-tick equivalent from big-bang.
  Pure function — no side effects."
  [state]
  (if (:paused? state)
    state
    (update state :player update-player)))

;; ---------------------------------------------------------------------------
;; Input Handling
;; ---------------------------------------------------------------------------
;; handle-key : GameState KeyEvent -> GameState
;; Also pure — takes state and event, returns new state.

(defn handle-key
  "GameState KeyEvent -> GameState
  Handles keyboard input. Returns new state with input applied."
  [state event]
  (let [key-name (name (:key event))]
    (cond
      ;; Jump — only when on the floor (prevents double-jump)
      (and (= key-name "up")
           (on-floor? (:player state)))
      (assoc-in state [:player :vy] jump-force)

      ;; Move left
      (= key-name "left")
      (assoc-in state [:player :vx] (- move-speed))

      ;; Move right
      (= key-name "right")
      (assoc-in state [:player :vx] move-speed)

      ;; Stop horizontal movement
      (= key-name " ")
      (assoc-in state [:player :vx] 0)

      ;; Pause toggle
      (= key-name "p")
      (update state :paused? not)

      ;; Reset
      (= key-name "r")
      (initial-state)

      :else state)))
