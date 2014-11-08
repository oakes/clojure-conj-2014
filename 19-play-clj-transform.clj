(defscreen play-clj-transform-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "List transformations" medium-style)
              :row
              (label (str \newline
                          "Mostly you just transform `entities` and return it" \newline
                          "Same thing you already do in any Clojure program")
                     small-style)
              :row
              (image "images/on-render.png" :set-scaling (scaling :fit))]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
