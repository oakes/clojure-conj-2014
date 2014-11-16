(defscreen games-as-art-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Games as Art" medium-style)
              :row
              (label (str \newline
                          "Art is the selective imitation of reality" \newline
                          "Ebert claimed games can't be art: http://goo.gl/AfSQ39" \newline
                          "He was right...about games today" \newline
                          "But as a medium they are capable of it")
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
