(defscreen gateway-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Gamedev -> Clojure" medium-style)
              :row
              (label (str \newline
                          "Gamedev is a gateway drug" \newline
                          "Learned C++ at 15 by making games" \newline
                          "A solid gamedev \"story\" is useful" \newline
                          "Brings in new people from diverse backgrounds")
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
