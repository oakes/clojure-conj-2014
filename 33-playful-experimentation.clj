(defscreen playful-experimentation-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Playful Experimentation" medium-style)
              :row
              (label (str \newline
                          "I love watching artists engage in their craft" \newline
                          "They constantly iterate (playing a chord, painting over a spot)" \newline
                          "A compiler can't tell you if your art is any good" \newline
                          "The Sound of Silence was made in a bathroom" \newline
                          "Imagine if he couldn't hear his guitar while playing it")
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
