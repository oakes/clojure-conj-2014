(defscreen interactivity-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Interactivity" medium-style)
              :row
              (label (str \newline
                          "Biggest selling point; it changes the end product" \newline
                          "Interactivity is not hot-swapping" \newline
                          "It's the REPL (connected to your game), stupid!" \newline
                          "Particularly important to artistic endeavors" \newline
                          "    Overtone" \newline
                          "    Quil")
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