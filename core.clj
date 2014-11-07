(load-game-file "01-title.clj")
(load-game-file "02-quote.clj")
(load-game-file "03-about.clj")
(load-game-file "04-gateway.clj")
(load-game-file "05-class.clj")
(load-game-file "06-problem.clj")
(load-game-file "07-indie.clj")
(load-game-file "08-gameplay.clj")
(load-game-file "09-graphics.clj")
(load-game-file "10-artistic.clj")
(load-game-file "11-tools.clj")
(load-game-file "12-hosted.clj")
(load-game-file "13-unity-libgdx.clj")

(load-game-file "00-dungeon-crawler.clj")

(declare overlay-screen)

(def slides [[title-screen]
             [quote-screen]
             [about-screen]
             [gateway-screen]
             [class-screen]
             [problem-screen]
             [indie-screen]
             [gameplay-screen]
             [graphics-screen]
             [artistic-screen]
             [tools-screen]
             [hosted-screen]
             [unity-libgdx-screen]
             [dungeon-crawler-screen dungeon-crawler-overlay-screen]])

(defn slide-num
  []
  (or (:slide (game-pref)) 1))

(defn set-slide!
  [n]
  (when-let [screens (get slides (- n 1))]
    (game-pref! :slide n)
    (apply set-game-screen! overlay-screen screens)))

(defscreen overlay-screen
  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :space))
      (set-slide! (+ (slide-num) 1))
      (= (:key screen) (key-code :backspace))
      (set-slide! (- (slide-num) 1)))))

(set-slide! (slide-num))
