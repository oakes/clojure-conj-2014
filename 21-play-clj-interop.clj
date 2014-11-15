(defscreen play-clj-interop-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "When :x and :y are not enough" medium-style)
              :row
              (label (str \newline
                          "Entities contain Java object in :object key" \newline
                          \newline
                          "(let [entity (texture \"clojure.png\")]
  (doto ^TextureRegion (:object entity)
    (.flip true false)
    (.setRegion 0 0 100 100))
  entity)")
                     small-style)]
             :align (align :center)
             :set-fill-parent true)))
  
  :on-render
  (fn [screen entities]
    (render! screen entities))
  
  :on-resize
  (fn [screen entities]
    (height! screen (:height screen))))
