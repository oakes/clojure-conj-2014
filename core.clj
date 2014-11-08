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
(load-game-file "14-good-hosted.clj")
(load-game-file "15-play-clj-init.clj")
(load-game-file "16-play-clj-core.clj")
(load-game-file "17-play-clj-neko.clj")
(load-game-file "18-play-clj-state.clj")
(load-game-file "19-play-clj-transform.clj")
(load-game-file "20-play-clj-entities.clj")
(load-game-file "21-play-clj-interop.clj")
(load-game-file "22-play-clj-dsl.clj")
(load-game-file "23-play-clj-dsl-bang.clj")
(load-game-file "24-play-clj-docs.clj")
(load-game-file "25-platformer.clj")
(load-game-file "26-functional.clj")
(load-game-file "27-logic.clj")
(load-game-file "28-dungeon-crawler.clj")

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
             [good-hosted-screen]
             [play-clj-init-screen]
             [play-clj-core-screen]
             [play-clj-neko-screen]
             [play-clj-state-screen]
             [play-clj-transform-screen]
             [play-clj-entities-background-screen play-clj-entities-screen]
             [play-clj-interop-screen]
             [play-clj-dsl-screen]
             [play-clj-dsl-bang-screen]
             [play-clj-docs-screen]
             [platformer-screen]
             [functional-screen]
             [logic-screen]
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
