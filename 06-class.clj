(defscreen class-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(image "images/class2.jpg" :set-scaling (scaling :fit))
              (image "images/class3.jpg" :set-scaling (scaling :fit))
              :row
              (image "images/class1.jpg" :set-scaling (scaling :fit))
              (label "Photo credit:\nAmbridge Connection" small-style)]
             :set-fill-parent true
             :pad (float 50) (float 0) (float 0) (float 0))))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
