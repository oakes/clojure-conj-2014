(defscreen unity-libgdx-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :black))]
      (table [(image "images/unity.png" :set-scaling (scaling :fit))
              (image "images/libgdx.png" :set-scaling (scaling :fit))
              :row
              (label "CLR / Mono" small-style)
              (label "JVM" small-style)
              :row
              (label "Desktop+Mobile+Web+Consoles" small-style)
              (label "Desktop+Mobile+Web" small-style)
              :row
              (label "2D+3D (focused on 3D)" small-style)
              (label "2D+3D (focused on 2D)" small-style)
              :row
              (label "Can use Clojure (via Arcadia)" small-style)
              (label "Can use Clojure (via play-clj or directly)" small-style)]
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (clear! 1 1 1 1)
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
