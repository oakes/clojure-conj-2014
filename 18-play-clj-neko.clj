(defscreen play-clj-neko-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [[(label "play-clj and neko" medium-style)
               :colspan 2]
              :row
              [(image "images/defscreen.png" :set-scaling (scaling :fit))
               :pad 5 5 5 5]
              [(image "images/defactivity.png" :set-scaling (scaling :fit))
               :pad 5 5 5 5]]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
