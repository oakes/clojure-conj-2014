(defscreen good-hosted-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "It's Good to be Hosted" medium-style)
              :row
              (label (str \newline
                          "The argument has been turned on its head" \newline
                          "Being hosted on CLR / JVM is huge advantage" \newline
                          "Many alt langs are on their own island" \newline
                          "Can make toy games, but shippable games are hard")
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
