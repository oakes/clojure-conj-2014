(defscreen play-clj-entities-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "What are entities?" medium-style)
              :row
              (label (str \newline
                          "Records that implement the Entity protocol" \newline
                          \newline
                          "(shape :filled :set-color (color :red) :rect 0 0 10 10)" \newline
                          "(texture \"image.png\")" \newline
                          "(nine-patch \"image.png\")" \newline
                          "(particle-effect \"particles/fire.p\")" \newline
                          "(model \"knight.g3dj\")" \newline
                          "; ui entities (not enough room to list all)" \newline)
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))

(defscreen play-clj-entities-background-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    [(assoc (shape :filled
                   :set-color (color :red)
                   :rect 0 0 100 100)
            :x 10 :y 500)
     (assoc (texture "images/clojure.png")
            :x 10 :y 350 :width 100 :height 100)
     (assoc (particle-effect "particle-effect/fire.p")
            :x 20 :y 200)])
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
