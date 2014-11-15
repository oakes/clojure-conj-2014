; utils

(def ^:const vertical-tiles-p 20)
(def ^:const duration-p 0.15)
(def ^:const damping-p 0.5)
(def ^:const max-velocity-p 14)
(def ^:const max-jump-velocity-p 56)
(def ^:const deceleration-p 0.9)
(def ^:const gravity-p -2.5)

(defn decelerate-p
  [velocity]
  (let [velocity (* velocity deceleration-p)]
    (if (< (Math/abs velocity) damping-p)
      0
      velocity)))

(defn touched?
  [key]
  (and (game :touched?)
       (case key
         :down (< (game :y) (/ (game :height) 3))
         :up (> (game :y) (* (game :height) (/ 2 3)))
         :left (< (game :x) (/ (game :width) 3))
         :right (> (game :x) (* (game :width) (/ 2 3)))
         false)))

(defn get-x-velocity-p
  [entity]
  (if (:player? entity)
    (cond
      (or (key-pressed? :dpad-left) (touched? :left))
      (* -1 max-velocity-p)
      (or (key-pressed? :dpad-right) (touched? :right))
      max-velocity-p
      :else
      (:x-velocity entity))
    (:x-velocity entity)))

(defn get-y-velocity-p
  [entity]
  (if (:player? entity)
    (cond
      (and (:can-jump? entity)
           (or (key-pressed? :dpad-up) (touched? :up)))
      max-jump-velocity-p
      :else
      (:y-velocity entity))
    (:y-velocity entity)))

(defn get-direction-p
  [entity]
  (cond
    (> (:x-velocity entity) 0) :right
    (< (:x-velocity entity) 0) :left
    :else (:direction entity)))

(defn get-touching-tile-p
  [screen entity & layer-names]
  (let [layers (map #(tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int (:x entity))
                             (+ (:x entity) (:width entity)))
               tile-y (range (int (:y entity))
                             (+ (:y entity) (:height entity)))]
           (some #(when (tiled-map-cell % tile-x tile-y)
                    [tile-x tile-y])
                 layers))
         (drop-while nil?)
         first)))

; entities

(defn create-player-p
  []
  (let [stand (texture "platformer/player_stand.png")
        jump (texture "platformer/player_jump.png")
        walk [(texture "platformer/player_walk1.png")
              (texture "platformer/player_walk2.png")
              (texture "platformer/player_walk3.png")]]
    (assoc stand
           :stand-right stand
           :stand-left (texture stand :flip true false)
           :jump-right jump
           :jump-left (texture jump :flip true false)
           :walk-right (animation duration-p
                                  walk
                                  :set-play-mode (play-mode :loop-pingpong))
           :walk-left (animation duration-p
                                 (map #(texture % :flip true false) walk)
                                 :set-play-mode (play-mode :loop-pingpong))
           :width 1
           :height (/ 26 18)
           :x-velocity 0
           :y-velocity 0
           :x 12
           :y 10
           :player? true
           :can-jump? false
           :direction :right)))

(defn move-p
  [screen entity]
  (let [x-velocity (get-x-velocity-p entity)
        y-velocity (+ (get-y-velocity-p entity) gravity-p)
        x-change (* x-velocity (:delta-time screen))
        y-change (* y-velocity (:delta-time screen))]
    (if (or (not= 0 x-change) (not= 0 y-change))
      (assoc entity
             :x-velocity (decelerate-p x-velocity)
             :y-velocity (decelerate-p y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ (:x entity) x-change)
             :y (+ (:y entity) y-change)
             :can-jump? (if (> y-velocity 0) false (:can-jump? entity)))
      entity)))

(defn animate-p
  [screen {:keys [x-velocity y-velocity
                  stand-right stand-left
                  jump-right jump-left
                  walk-right walk-left] :as entity}]
  (let [direction (get-direction-p entity)]
    (merge entity
           (cond
             (not= y-velocity 0)
             (if (= direction :right) jump-right jump-left)
             (not= x-velocity 0)
             (if (= direction :right)
               (animation->texture screen walk-right)
               (animation->texture screen walk-left))
             :else
             (if (= direction :right) stand-right stand-left))
           {:direction direction})))

(defn prevent-move-p
  [screen entity]
  (let [old-x (- (:x entity) (:x-change entity))
        old-y (- (:y entity) (:y-change entity))
        entity-x (assoc entity :y old-y)
        entity-y (assoc entity :x old-x)
        up? (> (:y-change entity) 0)]
    (merge entity
           (when (get-touching-tile-p screen entity-x "walls")
             {:x-velocity 0 :x-change 0 :x old-x})
           (when (get-touching-tile-p screen entity-y "walls")
             {:y-velocity 0 :y-change 0 :y old-y :can-jump? (not up?)}))))

; core

(defn update-screen-p!
  [screen entities]
  (doseq [entity entities]
    (when (:player? entity)
      (position! screen (:x entity) (/ vertical-tiles-p 2))
      (when (< (:y entity) (- (:height entity)))
        (restart-game!))))
  entities)

(defscreen platformer-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (orthogonal-tiled-map "platformer/level.tmx" 1/16))
    (create-player-p))
  
  :on-render
  (fn [screen entities]
    (clear! 0.5 0.5 1 1)
    (->> entities
         (map (fn [entity]
                (->> entity
                     (move-p screen)
                     (prevent-move-p screen)
                     (animate-p screen))))
         (render! screen)
         (update-screen-p! screen)))
  
  :on-resize
  (fn [screen entities]
    (height! screen vertical-tiles-p)))
