(load-game-file "01-title.clj")
(load-game-file "02-quote.clj")
(load-game-file "03-about.clj")
(load-game-file "04-projects.clj")
(load-game-file "05-gateway.clj")
(load-game-file "06-class.clj")
(load-game-file "07-problem.clj")
(load-game-file "08-indie.clj")
(load-game-file "09-gameplay.clj")
(load-game-file "10-graphics.clj")
(load-game-file "11-artistic.clj")
(load-game-file "12-tools.clj")
(load-game-file "13-hosted.clj")
(load-game-file "14-unity-libgdx.clj")
(load-game-file "15-good-hosted.clj")
(load-game-file "16-play-clj-init.clj")
(load-game-file "17-play-clj-core.clj")
(load-game-file "18-play-clj-neko.clj")
(load-game-file "19-play-clj-state.clj")
(load-game-file "20-play-clj-transform.clj")
(load-game-file "21-play-clj-entities.clj")
(load-game-file "22-play-clj-interop.clj")
(load-game-file "23-play-clj-dsl.clj")
(load-game-file "24-play-clj-dsl-bang.clj")
(load-game-file "25-play-clj-docs.clj")
(load-game-file "26-platformer.clj")
(load-game-file "27-functional.clj")
(load-game-file "28-logic.clj")
(load-game-file "29-dungeon-crawler.clj")
(load-game-file "30-interactivity.clj")
(load-game-file "31-graffiti.clj")
(load-game-file "32-street-music.clj")
(load-game-file "33-mural.clj")
(load-game-file "34-playful-experimentation.clj")
(load-game-file "35-games-as-art.clj")
(load-game-file "36-bottom-line.clj")
(load-game-file "37-quest-quest.clj")

(declare background-screen)

(def slides [[title-screen]
             [quote-screen]
             [about-screen]
             [projects-screen]
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
             [dungeon-crawler-screen dungeon-crawler-overlay-screen]
             [interactivity-screen]
             [graffiti-screen]
             [street-music-screen]
             [mural-screen]
             [playful-experimentation-screen]
             [games-as-art-screen]
             [bottom-line-screen]
             [quest-quest-screen quest-quest-ui-screen]])

(defn slide-num
  []
  (or (:slide (game-pref)) 1))

(defn set-slide!
  [n]
  (when-let [screens (get slides (- n 1))]
    (game-pref! :slide n)
    (apply set-game-screen! background-screen screens)))

(defscreen background-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (assoc (shape :filled) :id :background))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  
  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen height)
    (let [c1 (color :black)
          c2 (color 79/256 90/256 100/256 1)]
      (for [e entities]
        (case (:id e)
          :background (shape e :rect 0 0 width height c1 c1 c2 c2)
          e))))
  
  :on-key-down
  (fn [screen entities]
    (cond
      (= (:key screen) (key-code :space))
      (set-slide! (+ (slide-num) 1))
      (= (:key screen) (key-code :backspace))
      (set-slide! (- (slide-num) 1)))))

(set-slide! (slide-num))
