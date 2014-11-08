(defscreen play-clj-docs-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :black))]
      (table [(image "images/docs.png" :set-scaling (scaling :fit))]
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
