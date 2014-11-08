(defscreen play-clj-init-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage) :camera (orthographic))
    (let [ui-skin (skin "skin/uiskin.json")
          medium-font (skin! ui-skin :get-font "medium-font")
          medium-style (style :label medium-font (color :white))
          small-font (skin! ui-skin :get-font "small-font")
          small-style (style :label small-font (color :white))]
      (table [(label "Getting Started with play-clj" medium-style)
              :row
              (label (str \newline
                          "lein new play-clj hello-world" \newline
                          \newline
                          "hello-world/" \newline
                          "    android/" \newline
                          "    desktop/" \newline
                          "    ios/" \newline
                          \newline
                          "Android uses lein-droid + clojure-android port" \newline
                          "iOS uses lein-fruit + RoboVM" \newline
                          "Mobile is experimental" \newline
                          "Web backend not supported (maybe later via TeaVM?)")
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
