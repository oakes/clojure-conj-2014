(defscreen logic-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Logic Programming in Games" medium-style)
              :row
              (label (str \newline
                          "Representing Game Dialogue as Expressions in First-Order Logic" \newline
                          "    http://goo.gl/B6F0hH (via @swannodette)" \newline
                          "    by Kaylen Wheeler" \newline
                          "    Dialogues are data structures instead of strings" \newline
                          "A Logical Approach to Building Dungeons" \newline
                          "    http://goo.gl/mBfLwO" \newline
                          "    by Anthony Smith and Joanna Bryson" \newline
                          "    Generating levels for a roguelike game" \newline)
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
