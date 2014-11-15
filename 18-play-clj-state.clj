(defscreen play-clj-state-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "State in play-clj" medium-style)
              :row
              (label (str \newline
                          "All screen functions take `screen` and `entities`" \newline
                          "`screen` is a hash-map to store singleton values" \newline
                          "`entities` is a vector to store...entities")
                     small-style)
              :row
              (image "images/on-show.png" :set-scaling (scaling :fit))]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
