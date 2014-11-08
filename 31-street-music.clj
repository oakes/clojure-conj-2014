(defscreen street-music-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(image "images/street-music.jpg" :set-scaling (scaling :fit))
              :row
              (label "Photo credit:\nLaura M. on yelp.com (http://goo.gl/lfMSHt)" small-style)]
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
