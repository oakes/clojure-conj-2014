(defscreen artistic-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(stack [(image "images/passage-gravitation.jpg"
                             :set-scaling (scaling :fit))
                      (label "Passage (2007) and Gravitation (2008)" small-style
                             :set-alignment (align :bottom-left))])
              (stack [(image "images/papers-please.jpg"
                             :set-scaling (scaling :fit))
                      (label "Papers, Please (2013)" small-style
                             :set-alignment (align :bottom-left))])]
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
