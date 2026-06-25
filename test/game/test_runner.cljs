(ns game.test-runner
  (:require [cljs.test :refer [run-tests]]
            [game.state-test]))

(defn main []
  (run-tests 'game.state-test))
