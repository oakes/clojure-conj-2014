; quests

(def quests
  [{:title "Welcome to Quest Quest!"
    :body ""
    :reward "Falling Unlocked!"}

   {:title "Safety First"
    :body "The ground is fast approaching, you must land safely!"
    :reward "Move Right Unlocked!"}

   {:title "Right of Way"
    :body "Get a feel for your surroundings, press d or right arrow to move right as far as your legs will carry you!"
    :reward "Move Left Unlocked!"}

   {:title "Left Alone"
    :body "Continue to explore your new surroundings, press press a or left arrow to move left!"
    :reward "Attack Unlocked!"}

   {:title "A Trial By Combat"
    :body "Your first enemy blocks the path! You must defeat it to continue. Apply everything you've learned so far to vanquish this beast!"
    :reward "Leveling Up Has Made You Stronger!"}

   {:title "Another Trial By Combat"
    :body "A scarier enemiy blocks your path! Vanquish it to proceed!"
    :reward "Leveling Up Has Made You Stronger!"}

   {:title "An Elite Leader"
    :body "An extremely cunning and ferocious stands between you and the nearby village! Fight with all your power to overcome it!"
    :reward "Pick-up Item Unlocked!"}

   {:title "Johnney Applegatherer"
    :body "Those villagers look very hungry. Gather all of their apples to keep them from starving to death!"
    :reward "Jump Unlocked!"}

   {:title "Launch Over It!"
    :body "Tighten the muscles in your legs to form a spring and launch yourself over the rock."
    :reward "Jump-Attack Unlocked!"}

   {:title "The Epic Raid Boss"
    :body "Destroy the final boss to see what lies inside the treasure room!"
    :reward "Open Treasure Unlocked!"}

   {:title "End Game"
    :body "Congratulations, you've reached the peak of your power! Surely untold adventures lie before you."
    :reward "End-Game Content Unlocked!"}])

; ui

(defn make-unit-frames
  "Initialzes the unit frames, starting information is hardcoded."
  []
  (table [:row [(assoc (label (str "HP: " 10)
                              (color :white))
                       :id :health)]
          :row [(assoc (label (str "MP: " 10)
                              (color :white))
                       :id :mana)]
          :row [(assoc (label (str "LVL: " 1)
                              (color :white))
                       :id :level)]]
         :set-position 40 355))

(defn make-quest-table
  [{:keys [title body]}]
  (let [title-label (assoc (label title
                                  (color :white)
                                  :set-scale 1.2 1.2)
                           :id :quest-title)
        body-label (assoc (label body
                                 (color :white))
                          :id :quest-body)]
    (table [:row [title-label]
            :row [body-label]]
           :set-position 400 366)))

(defn make-fps
  []
  (assoc (label "0" (color :white))
         :id :fps
         :x 5))

#_(defn update-all-elements
    [screen entities]
    (refresh-unit-frames)
    (refresh-quest))

; utils

;;; Screen vars
(def vertical-tiles-q 12)
(def pixels-per-tile-q 32)
(def camera-height-q 6)

;;; Physics vars
(def duration-q 0.15)
(def damping-q 0.5)
(def max-velocity-q 8)
(def max-jump-velocity-q 24)
(def gravity-q -1.5)
(def deceleration-q 0.9)

;;; Input handlers
(defn touched-q?
  [key]
  (and (game :touched?)
       (case key
         :down (< (game :y) (/ (game :height) 3))
         :up (> (game :y) (* (game :height) (/ 2 3)))
         :left (< (game :x) (/ (game :width) 3))
         :right (> (game :x) (* (game :width) (/ 2 3)))
         false)))

;;; Camera controls
(defn move-camera-q!
  "The camera tracks the player if above 8 or 0. It Centers the camera on the world when player is below 8."
  [screen x y]
  (if (< y camera-height-q)
    (if (pos? y)
      (position! screen x camera-height-q))
    (position! screen x y)))

;;; World handlers
(defn out-of-bounds-q?
  [y height]
  (< y (- height)))

;;; Movement handlers
(defn decelerate-q
  [velocity]
  (let [velocity (* velocity deceleration-q)]
    (if (< (Math/abs velocity) damping-q)
      0
      velocity)))

(defn get-x-velocity-q
  [{:keys [id x-velocity]}]
  (case id
    :player (cond
              (or (key-pressed? :dpad-left) (touched-q? :left))
              (* -1 max-velocity-q)
              (or (key-pressed? :dpad-right) (touched-q? :right))
              max-velocity-q
              :else
              x-velocity)
    x-velocity))

(defn get-y-velocity-q
  [{:keys [id y-velocity can-jump?]}]
  (case id
    :player (cond
              (and can-jump? (or (key-pressed? :dpad-up) (touched-q? :up)))
              max-jump-velocity-q
              :else
              y-velocity)
    y-velocity))

(defn get-direction-q
  [{:keys [x-velocity direction]}]
  (cond
    (pos? x-velocity) :right
    (neg? x-velocity) :left
    :else
    direction))

(defn get-touching-tile-q
  [screen {:keys [x y width height]} & layer-names]
  (let [layers (map #(tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (some #(when (tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))

(defn properties->hash-map
  [properties]
  (apply hash-map (interleave (map keyword (.getKeys properties))
                              (.getValues properties))))

(defn downscale-x-and-y
  [properties]
  (assoc properties
         :x (/ (:x properties) pixels-per-tile-q)
         :y (/ (:y properties) pixels-per-tile-q)))

(defn make-checkpoint
  [map-object]
  (let [checkpoint (bean map-object)]
    (merge checkpoint (-> (properties->hash-map (:properties checkpoint))
                          downscale-x-and-y))))

;; FIXME
(defn- touching-checkpoint?
  [entity checkpoint]
  (or (>= (:x checkpoint) (:x entity))
      (>= (:y checkpoint) (:y entity))
      (>= (:height checkpoint) (:height entity))
      (>= (:width checkpoint) (:width entity))))

(defn get-touching-checkpoint-q
  "Loads the checkpoints layer from the tile map on the main-screen."
  [screen entity]
  (let [objects (map-objects (tiled-map-layer screen "checkpoints"))
        checkpoints (map make-checkpoint objects)]
    (filter #(touching-checkpoint? entity %) checkpoints)))

(defn near-entity-q?
  [{:keys [x y id] :as e} e2 min-distance]
  (and (not= id (:id e2))
       (nil? (:draw-time e2))
       (pos? (:health e2))
       (< (Math/abs ^double (- x (:x e2))) min-distance)
       (< (Math/abs ^double (- y (:y e2))) min-distance)))

;; FIXME near should be touching
(defn near-entities-q?
  [entities entity min-distance]
  (some #(near-entity-q? entity % min-distance) entities))

;;; Combat handlers
(defn fight [e1 e2])

#_(defn process-fighting
    [e1 e2]
    (when (and (touching? e1 e2)
               (not (have-fought? e1 e2)))
      (fight e1 e2)))

(defn process-damage
  [{:keys [health wounds] :as entity}]
  (assoc entity :health (- health wounds)))

; entities

(defn create-player-q
  [{:keys [level image x y]}]
  (assoc image
         :id :player
         :x x
         :y y
         :width 2
         :height 2
         :x-velocity 0
         :y-velocity 0
         :level level
         :health (* 10 level)
         :wounds 0
         :can-jump? false
         :direction :left
         :right image
         :left (texture image :flip true false)
         :jump-sound (sound "quest-quest/jump.wav")))

(defn create-enemy-q
  [{:keys [image level x y id]}]
  (assoc image
         :id id
         :x x
         :y y
         :level level
         :x-velocity 0
         :y-velocity 0
         :width 1
         :height level
         :direction :right
         :health (* 10 level)
         :wounds 0))

(defn level-up
  [screen {:keys [player? level] :as entity}]
  (if player?
    (assoc entity
           :level (inc level))
    entity))

(defn ^:private enable-jump?
  [y-velocity can-jump?]
  (if (pos? y-velocity)
    false
    can-jump?))

(defn move-q
  [{:keys [delta-time]} {:keys [x y can-jump?] :as entity}]
  (let [x-velocity (get-x-velocity-q entity)
        y-velocity (+ (get-y-velocity-q entity) gravity-q)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change)
            (not= 0 y-change))
      (assoc entity
             :x-velocity (decelerate-q x-velocity)
             :y-velocity (decelerate-q y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change)
             :can-jump? (enable-jump? y-velocity can-jump?))
      entity)))

(defn prevent-move-q
  [screen {:keys [x y x-change y-change] :as entity}]
  (let [old-x (- x x-change)
        old-y (- y y-change)
        entity-x (assoc entity :y old-y)
        entity-y (assoc entity :x old-x)
        up? (> y-change 0)]
    (merge entity
           (when (get-touching-tile-q screen entity-x "walls")
             {:x-velocity 0 :x-change 0 :x old-x})
           (when-let [tile (get-touching-tile-q screen entity-y "walls")]
             {:y-velocity 0 :y-change 0 :y old-y :can-jump? (not up?)}))))

(defn animate-q
  [screen {:keys [x-velocity y-velocity
                  right left] :as entity}]
  (let [direction (get-direction-q entity)]
    (merge entity
           (if (= direction :right) right left)
           {:direction direction})))

(defn spawn-all
  "returns a vector containing all of the starting entities"
  []
  (vector (create-player-q {:image (texture "quest-quest/quester.png") :level 1 :x 20 :y 69})
          (create-enemy-q {:image (texture "quest-quest/first-enemy.png") :level 1 :id :enemy-first :x 45 :y 10})
          (create-enemy-q {:image (texture "quest-quest/first-enemy.png") :level 2 :id :enemy-second :x 60 :y 10})
          (create-enemy-q {:image (texture "quest-quest/first-enemy.png") :level 3 :id :enemy-three :x 75 :y 10})
          (create-enemy-q {:image (texture "quest-quest/first-enemy.png") :level 10 :id :boss :x 200 :y 80})))

; core

(declare main-screen ui-screen)

(defn update-screen-q!
  "Used in the render function to focus the camera on the player and reset
  the screen if the player goes out of bounds."
  [screen entities]
  (doseq [{:keys [x y height id]} entities]
    (case id
      :player (do
                (move-camera-q! screen x y)
                (when (out-of-bounds-q? y height)
                  (restart-game!)))
      entities))
  entities)

(defn play-sounds-q!
  [entities]
  (doseq [{:keys [play-sound]} entities]
    (when play-sound
      (sound! play-sound :play)))
  (map #(dissoc % :play-sound) entities))

(defscreen quest-quest-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic))
    (update! screen :renderer (orthogonal-tiled-map "quest-quest/world.tmx" (/ 1 pixels-per-tile-q)))
    (spawn-all))

  :on-render
  (fn [screen entities]
    (clear! (/ 135 255) (/ 206 255) (/ 235 255) 100)
    #_(screen! ui-screen :on-update-ui :entities entities)

    (->> entities
         (map (fn [entity]
                (->> entity
                     #_(level-up screen)
                     (move-q screen)
                     (prevent-move-q screen)
                     (animate-q screen))))
         play-sounds-q!
         (render! screen)
         (update-screen-q! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen vertical-tiles-q)
    nil))

(defscreen quest-quest-ui-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    (vector (make-quest-table (first quests))
            (make-unit-frames)
            (make-fps)))

  :on-render
  (fn [screen entities]
    (render! screen
             (for [entity entities]
               (case (:id entity)
                 :fps (doto entity (label! :set-text (str (game :fps))))
                 entity))))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (height! screen (:height screen))
    nil)

  :on-update-ui
  (fn [screen entities]
    #_(update-all-elements screen entities)))
