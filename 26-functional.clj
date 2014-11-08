(defscreen functional-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Functional Programming in Games" medium-style)
              :row
              (label (str \newline
                          "Same benefits as in other kinds of software" \newline
                          "    Easy parallelism" \newline
                          "    Immutable entities simplify change (i.e., time rewinding)" \newline
                          "Functional idioms are already being adopted" \newline
                          "    Carmack on functional prog in C++: http://goo.gl/oZvaon" \newline
                          "    ECS architecture replacing OO-style inheritance")
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
