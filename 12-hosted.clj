(defscreen hosted-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Managed & Hosted Languages" medium-style)
              :row
              (label (str \newline
                          "Gamedevs traditionally reject GC" \newline
                          "Things have changed in last half-decade" \newline
                          "Many indie games are now made in C# / Java" \newline
                          "Indies have scarce resources, need to ship")
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
