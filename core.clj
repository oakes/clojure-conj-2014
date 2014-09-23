(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")]
      (table [(label "Making Games\nat Runtime\nwith Clojure" ui-skin
                     :set-alignment (align :center))
              :row
              [(label "by Zach Oakes" (color :white))
               :pad-top 10]]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen 400)))

(set-game-screen! main-screen)
