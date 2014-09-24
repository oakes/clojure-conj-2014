(load-game-file "slide01.clj")
(load-game-file "slide02.clj")

(declare overlay-screen)

(def slides [slide-1-screen
             slide-2-screen])

(defn slide-num
  []
  (or (:slide (game-pref)) 1))

(defn set-slide!
  [n]
  (when-let [slide (get slides (- n 1))]
    (game-pref! :slide n)
    (set-game-screen! overlay-screen slide)))

(defscreen overlay-screen
  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :space))
      (set-slide! (+ (slide-num) 1))
      (= (:key screen) (key-code :backspace))
      (set-slide! (- (slide-num) 1)))))

(set-slide! (slide-num))
