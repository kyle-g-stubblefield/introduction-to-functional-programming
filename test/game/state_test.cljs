(ns game.state-test
  "Tests for game.state pure functions.
  
  This is the check-expect equivalent from HtDP/Racket.
  Every function in game.state should have tests here.
  
  HtDP design recipe step 3: write examples before implementation.
  Get in the habit of writing these tests first."
  (:require [cljs.test :refer [deftest is testing]]
            [game.state :as state]))

;; ---------------------------------------------------------------------------
;; apply-gravity tests
;; ---------------------------------------------------------------------------

(deftest apply-gravity-increases-vy
  (testing "gravity adds to vertical velocity each frame"
    (let [player {:x 100 :y 300 :vx 0 :vy 0 :radius 20}
          result (state/apply-gravity player)]
      (is (= (:vy result) state/gravity)))))

(deftest apply-gravity-accumulates
  (testing "gravity accumulates over multiple frames"
    (let [player {:x 100 :y 300 :vx 0 :vy 0 :radius 20}
          after-two-frames (-> player
                               state/apply-gravity
                               state/apply-gravity)]
      (is (= (:vy after-two-frames) (* 2 state/gravity))))))

;; ---------------------------------------------------------------------------
;; apply-velocity tests
;; ---------------------------------------------------------------------------

(deftest apply-velocity-moves-player
  (testing "velocity moves player position"
    (let [player {:x 100 :y 300 :vx 5 :vy -3 :radius 20}
          result (state/apply-velocity player)]
      (is (= (:x result) 105))
      (is (= (:y result) 297)))))

(deftest apply-velocity-zero-velocity-no-movement
  (testing "zero velocity means no movement"
    (let [player {:x 100 :y 300 :vx 0 :vy 0 :radius 20}
          result (state/apply-velocity player)]
      (is (= (:x result) 100))
      (is (= (:y result) 300)))))

;; ---------------------------------------------------------------------------
;; clamp-to-floor tests
;; ---------------------------------------------------------------------------

(deftest clamp-stops-at-floor
  (testing "player below floor is moved to floor"
    (let [player {:x 100 :y 999 :vx 0 :vy 10 :radius 20}
          result (state/clamp-to-floor player)
          expected-floor (- state/canvas-height (:radius player))]
      (is (= (:y result) expected-floor))
      (is (= (:vy result) 0)))))

(deftest clamp-leaves-airborne-player-alone
  (testing "player above floor is not affected"
    (let [player {:x 100 :y 200 :vx 0 :vy 5 :radius 20}
          result (state/clamp-to-floor player)]
      (is (= (:y result) 200))
      (is (= (:vy result) 5)))))

;; ---------------------------------------------------------------------------
;; handle-key tests
;; ---------------------------------------------------------------------------

(deftest handle-key-pause-toggle
  (testing "p key toggles paused state"
    (let [state  (state/initial-state)
          paused (state/handle-key state {:key :p})]
      (is (true? (:paused? paused)))
      (is (false? (:paused? (state/handle-key paused {:key :p})))))))

(deftest handle-key-reset
  (testing "r key resets to initial state"
    (let [modified (-> (state/initial-state)
                       (assoc :score 42)
                       (assoc-in [:player :x] 999))
          reset    (state/handle-key modified {:key :r})]
      (is (= reset (state/initial-state))))))

(deftest handle-key-jump-only-on-floor
  (testing "up key only applies jump when player is on floor"
    (let [on-floor   (assoc-in (state/initial-state)
                               [:player :y]
                               (- state/canvas-height 20))
          in-air     (assoc-in (state/initial-state)
                               [:player :y] 100)
          jumped     (state/handle-key on-floor {:key :up})
          no-jump    (state/handle-key in-air  {:key :up})]
      (is (= (get-in jumped  [:player :vy]) state/jump-force))
      (is (= (get-in no-jump [:player :vy]) 0)))))
