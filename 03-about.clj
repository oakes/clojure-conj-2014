(defscreen about-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "About me" ui-skin)
              :row
              (label (str "Independent programmer" \newline
                          "Not a legit game developer" \newline
                          "Used to do crypto/stego" \newline
                          "Joined the dark side" \newline
                          "Quit in 2012 and started using Clojure" \newline
                          \newline
                          "Nightweb (anonymous networking client)" \newline
                          "Nightcode (IDE for beginners)" \newline
                          "play-clj (game library)" \newline
                          "Nightmod (game creation tool)")
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
