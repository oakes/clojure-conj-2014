; utils

(def ^:const vertical-tiles 4)
(def ^:const pixels-per-tile 64)
(def ^:const duration 0.2)
(def ^:const damping 0.5)
(def ^:const deceleration 0.9)
(def ^:const map-width 40)
(def ^:const map-height 40)
(def ^:const aggro-distance 2)
(def ^:const attack-distance 0.25)
(def ^:const grid-tile-size 256)
(def ^:const directions [:w :nw :n :ne
                         :e :se :s :sw])
(def ^:const velocities [[-1 0] [-1 1] [0 1] [1 1]
                         [1 0] [1 -1] [0 -1] [-1 -1]])

(def ^:const bar-w 20)
(def ^:const bar-h 80)
(def ^:const npc-bar-h 0.1)

(defn on-layer?
  [screen {:keys [width height] :as entity} & layer-names]
  (let [{:keys [x y]} (screen->isometric screen entity)
        layers (map #(tiled-map-layer screen %) layer-names)]
    (->> (for [tile-x (range (int x) (+ x width))
               tile-y (range (int y) (+ y height))]
           (-> (some #(tiled-map-cell % tile-x tile-y) layers)
               nil?
               not))
         (filter identity)
         first
         nil?
         not)))

(defn entity-rect
  [{:keys [x y x-feet y-feet width height]} min-distance]
  (rectangle (- (+ x x-feet)
                (/ min-distance 4))
             (- (+ y y-feet)
                (/ min-distance 4))
             (- (+ width (/ min-distance 2))
                (* 2 x-feet))
             (- (+ height (/ min-distance 2))
                (* 2 y-feet))))

(defn near-entity?
  [e e2 min]
  (and (not= (:id e) (:id e2))
       (> (:health e2) 0)
       (rectangle! (entity-rect e min) :overlaps (entity-rect e2 min))))

(defn near-entities?
  [entities entity min-distance]
  (some #(near-entity? entity % min-distance) entities))

(defn invalid-location?
  [screen entities entity]
  (or (near-entities? entities entity 0)
      (on-layer? screen entity "walls")))

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))

(defn ^:private get-player-velocity
  [{:keys [x-velocity y-velocity max-velocity]}]
  (if (and (game :touched?) (button-pressed? :left))
    (let [x (float (- (game :x) (/ (game :width) 2)))
          y (float (- (game :y) (/ (game :height) 2)))
          x-adjust (* max-velocity (Math/abs (double (/ x y))))
          y-adjust (* max-velocity (Math/abs (double (/ y x))))]
      [(* (Math/signum x) (min max-velocity x-adjust))
       (* (Math/signum y) (min max-velocity y-adjust))])
    [(cond
       (key-pressed? :dpad-left) (* -1 max-velocity)
       (key-pressed? :dpad-right) max-velocity
       :else x-velocity)
     (cond
       (key-pressed? :dpad-down) (* -1 max-velocity)
       (key-pressed? :dpad-up) max-velocity
       :else y-velocity)]))

(defn ^:private get-npc-axis-velocity
  [{:keys [max-velocity]} diff]
  (cond
    (> diff attack-distance) (* -1 max-velocity)
    (< diff (* -1 attack-distance)) max-velocity
    :else 0))

(defn ^:private get-npc-aggro-velocity
  [npc me]
  (let [r1 (entity-rect npc attack-distance)
        r2 (entity-rect me attack-distance)
        x-diff (- (rectangle! r1 :get-x) (rectangle! r2 :get-x))
        y-diff (- (rectangle! r1 :get-y) (rectangle! r2 :get-y))]
    (if-not (rectangle! r1 :overlaps r2)
      [(get-npc-axis-velocity npc x-diff)
       (get-npc-axis-velocity npc y-diff)]
      [0 0])))

(defn ^:private get-npc-velocity
  [entities {:keys [last-attack attack-interval
                    x-velocity y-velocity max-velocity]
             :as entity}]
  (let [me (find-first :player? entities)]
    (if (near-entity? entity me aggro-distance)
      (get-npc-aggro-velocity entity me)
      (if (>= last-attack attack-interval)
        [(* max-velocity (- (rand-int 3) 1))
         (* max-velocity (- (rand-int 3) 1))]
        [x-velocity y-velocity]))))

(defn get-velocity
  [entities {:keys [player? npc?] :as entity}]
  (cond
    player? (get-player-velocity entity)
    npc? (get-npc-velocity entities entity)
    :else [0 0]))

(defn get-direction
  [x-velocity y-velocity]
  (some->> velocities
           (filter (fn [[x y]]
                     (and (= x (int (Math/signum (float x-velocity))))
                          (= y (int (Math/signum (float y-velocity)))))))
           first
           (.indexOf velocities)
           (nth directions)))

(defn get-direction-to-entity
  [{:keys [x y x-feet y-feet last-direction] :as e} e2]
  (or (get-direction (- (+ (:x e2) (:x-feet e2)) (+ x x-feet))
                     (- (+ (:y e2) (:y-feet e2)) (+ y y-feet)))
      last-direction))

(defn find-id
  [entities id]
  (find-first #(= id (:id %)) entities))

(defn split-texture
  [path size mask-size]
  (let [start (/ (- size mask-size) 2)
        grid (texture! (texture path) :split size size)]
    (doseq [row grid
            item row]
      (texture! item :set-region item start start mask-size mask-size))
    grid))

(defn can-attack?
  [e e2]
  (and e2
       (not= (:npc? e) (:npc? e2))
       (> (:health e) 0)
       (>= (:last-attack e) (:attack-interval e))
       (near-entity? e e2 attack-distance)))

(defn get-entity-at-cursor
  [screen entities]
  (let [coords (input->screen screen (input! :get-x) (input! :get-y))]
    (find-first (fn [{:keys [x y width height npc? health] :as entity}]
                  (-> (rectangle x y width height)
                      (rectangle! :contains (:x coords) (:y coords))
                      (and npc? (> health 0))))
                entities)))

; entities

(defn create
  [grid mask-size]
  (let [moves (zipmap directions
                      (map #(animation duration (take 4 %)) grid))
        attacks (zipmap directions (map #(texture (nth % 4)) grid))
        specials (zipmap directions (map #(texture (nth % 5)) grid))
        hits (zipmap directions (map #(texture (nth % 6)) grid))
        deads (zipmap directions (map #(texture (nth % 7)) grid))
        texture-size (/ mask-size grid-tile-size)
        start-direction :s]
    (assoc (texture (get-in grid [(.indexOf directions start-direction) 0]))
           :width texture-size
           :height texture-size
           :moves moves
           :attacks attacks
           :specials specials
           :hits hits
           :deads deads
           :x-velocity 0
           :y-velocity 0
           :x-feet 0
           :y-feet 0
           :last-attack 0
           :attack-interval 1
           :direction start-direction
           :health 10
           :wounds 0
           :damage 2)))

(defn create-player
  []
  (let [path "dungeon-crawler/characters/male_light.png"
        mask-size 128
        grid (split-texture path grid-tile-size mask-size)]
    (assoc (create grid mask-size)
           :player? true
           :max-velocity 2
           :attack-interval 0.25
           :health 40
           :hurt-sound (sound "dungeon-crawler/playerhurt.wav")
           :death-sound (sound "dungeon-crawler/death.wav"))))

(defn create-npc
  [path]
  (let [mask-size 256
        grid (split-texture path grid-tile-size mask-size)]
    (assoc (create grid mask-size)
           :npc? true
           :max-velocity 2
           :x-feet 0.35
           :y-feet 0.35
           :hurt-sound (sound "dungeon-crawler/monsterhurt.wav"))))

(defn create-ogre
  []
  (assoc (create-npc "dungeon-crawler/characters/ogre.png")
         :type :ogre
         :max-velocity 1))

(defn create-elemental
  []
  (assoc (create-npc "dungeon-crawler/characters/elemental.png")
         :type :elemental))

(defn create-magician
  []
  (assoc (create-npc "dungeon-crawler/characters/magician.png")
         :type :magician))

(defn create-skeleton
  []
  (assoc (create-npc "dungeon-crawler/characters/skeleton.png")
         :type :skeleton))

(defn create-zombie
  []
  (assoc (create-npc "dungeon-crawler/characters/zombie.png")
         :type :zombie))

(defn create-werewolf
  []
  (assoc (create-npc "dungeon-crawler/characters/werewolf.png")
         :type :werewolf
         :width 2
         :height 2))

(defn update-health-bar
  [bar entity]
  (when entity
    (let [bar-x (:x entity)
          bar-y (+ (:y entity) (:height entity))
          bar-w (:width entity)
          pct (/ (:health entity) (+ (:health entity) (:wounds entity)))]
      (shape bar
             :set-color (color :red)
             :rect bar-x bar-y bar-w npc-bar-h
             :set-color (color :green)
             :rect bar-x bar-y (* bar-w pct) npc-bar-h))))

(defn move
  [{:keys [delta-time]} entities {:keys [x y health] :as entity}]
  (let [[x-velocity y-velocity] (get-velocity entities entity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (cond
      (= health 0)
      (assoc entity :x-velocity 0 :y-velocity 0)
      (or (not= 0 x-change) (not= 0 y-change))
      (assoc entity
             :x-velocity (decelerate x-velocity)
             :y-velocity (decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change))
      :else
      entity)))

(defn ^:private recover
  [{:keys [last-attack health direction] :as entity}]
  (if (and (>= last-attack 0.5) (> health 0))
    (merge entity
           (-> (get-in entity [:moves direction])
               (animation! :get-key-frame 0)
               texture))
    entity))

(defn animate
  [screen {:keys [x-velocity y-velocity] :as entity}]
  (if-let [direction (get-direction x-velocity y-velocity)]
    (let [anim (get-in entity [:moves direction])]
      (merge entity
             (animation->texture screen anim)
             {:direction direction}))
    (recover entity)))

(defn prevent-move
  [screen entities {:keys [x y x-change y-change] :as entity}]
  (let [old-x (- x x-change)
        old-y (- y y-change)
        x-entity (assoc entity :y old-y)
        y-entity (assoc entity :x old-x)]
    (merge entity
           (when (invalid-location? screen entities x-entity)
             {:x-velocity 0 :x-change 0 :x old-x})
           (when (invalid-location? screen entities y-entity)
             {:y-velocity 0 :y-change 0 :y old-y}))))

(defn adjust
  [{:keys [delta-time]} {:keys [last-attack attack-interval npc?] :as entity}]
  (assoc entity
         :last-attack (if (and npc? (>= last-attack attack-interval))
                        0
                        (+ last-attack delta-time))))

(defn attack
  [screen {:keys [x y x-feet y-feet damage] :as attacker} victim entities]
  (map (fn [{:keys [id direction] :as e}]
         (cond
           (= id (:id attacker))
           (let [direction (or (when victim
                                 (get-direction-to-entity attacker victim))
                               direction)]
             (merge e
                    {:last-attack 0
                     :direction direction}
                    (when (> (:health e) 0)
                      (get-in e [:attacks direction]))))
           (= id (:id victim))
           (if attacker
             (let [health (max 0 (- (:health victim) damage))]
               (merge e
                      {:last-attack 0
                       :health health
                       :wounds (+ (:wounds victim) damage)
                       :play-sound (if (and (= health 0) (:death-sound victim))
                                     (:death-sound victim)
                                     (:hurt-sound victim))}
                      (if (> health 0)
                        (get-in e [:hits direction])
                        (get-in e [:deads direction]))))
             e)
           :else
           e))
         entities))

; rooms

(require '[clojure.core.logic :as l]
         'clojure.set)

(def ^:const cols 4)
(def ^:const rows 4)
(def ^:const size 10)

(defn find-valid-rooms
  [screen rooms entities entity]
  (l/run* [q]
          (l/membero q rooms)
          (l/featurec q {:start? false})
          (l/fresh [e type]
                   (l/== e entity)
                   (l/featurec e {:type type})
                   (l/project [type]
                              (l/featurec q {:end? (= type :werewolf)})))))

(defn locations
  [room]
  (let [room-x (* size (:x room))
        room-y (* size (:y room))]
    (for [tile-x (range room-x (+ room-x size))
          tile-y (range room-y (+ room-y size))]
      {:x tile-x :y tile-y})))

(defn randomize-location
  [screen rooms entities {:keys [width height] :as entity}]
  (->> (find-valid-rooms screen rooms entities entity)
       (map locations)
       flatten
       (map (partial isometric->screen screen))
       shuffle
       (drop-while #(invalid-location? screen entities (merge entity %)))
       first
       (merge entity)))

(defn randomize-locations
  [screen rooms entities entity]
  (conj entities
        (-> (if (:npc? entity)
              (randomize-location screen rooms entities entity)
              entity)
            (assoc :id (count entities)))))

(defn get-rand-neighbor
  [rooms {:keys [x y] :as room}]
  (try
    (->> #{(assoc room :x (- x 1))
           (assoc room :y (- y 1))
           (assoc room :x (+ x 1))
           (assoc room :y (+ y 1))}
         (clojure.set/intersection (set rooms))
         vec
         rand-nth)
    (catch Exception _)))

(defn connect-room!
  [screen r1 r2]
  (let [rand-spot (+ 1 (rand-int (- size 3)))
        x-diff (- (:x r2) (:x r1))
        y-diff (- (:y r2) (:y r1))]
    (doseq [i (range size)
            :let [x (+ (* (:x r1) size)
                       rand-spot
                       (* x-diff i))
                  y (+ (* (:y r1) size)
                       rand-spot
                       (* y-diff i))]]
      (doto (tiled-map-layer screen "walls")
        (tiled-map-layer! :set-cell x y nil)
        (tiled-map-layer! :set-cell (+ x 1) y nil)
        (tiled-map-layer! :set-cell x (+ y 1) nil)
        (tiled-map-layer! :set-cell (+ x 1) (+ y 1) nil)))))

(defn connect-rooms!
  [screen rooms room]
  (let [visited-room (assoc room :visited? true :end? false)
        rooms (map #(if (= % room) visited-room %) rooms)]
    (if-let [next-room (get-rand-neighbor rooms room)]
      (do
        (connect-room! screen room next-room)
        (loop [rooms rooms]
          (let [new-rooms (connect-rooms! screen rooms next-room)]
            (if (= rooms new-rooms)
              rooms
              (recur new-rooms)))))
      (if (-> (filter :end? rooms) count (> 0))
        rooms
        (map #(if (= % visited-room) (assoc % :end? true) %) rooms)))))

; core

(declare dungeon-crawler-screen dungeon-crawler-overlay-screen)

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y player?]} entities]
    (when player?
      (position! screen x y)))
  entities)

(defn play-sounds!
  [entities]
  (doseq [{:keys [play-sound]} entities]
    (when play-sound
      (sound! play-sound :play)))
  (map #(dissoc % :play-sound) entities))

(defn render-everything!
  [screen entities]
  (->> (find-first #(= (:id %) (:mouse-npc-id screen)) entities)
       (update-health-bar (:npc-health-bar screen))
       (conj entities)
       (render-sorted! screen ["walls"]))
  entities)

(defscreen dungeon-crawler-screen
  :on-show
  (fn [screen entities]
    (let [renderer (isometric-tiled-map "dungeon-crawler/level1.tmx" (/ 1 pixels-per-tile))
          screen (update! screen
                          :camera (orthographic)
                          :npc-health-bar (shape :filled)
                          :renderer renderer)
          start-room {:x (rand-int rows)
                      :y (rand-int cols)}
          start-player-x (+ (* (:x start-room) size)
                            (/ size 2))
          start-player-y (+ (* (:y start-room) size)
                            (/ size 2))
          rooms (for [row (range rows)
                      col (range cols)]
                  {:x row :y col})
          rooms (connect-rooms! screen rooms start-room)
          rooms (map #(assoc % :start? (and (= (:x %) (:x start-room))
                                            (= (:y %) (:y start-room))))
                     rooms)
          me (assoc (create-player)
                    :x start-player-x
                    :y start-player-y)]
      (->> [(isometric->screen screen me)
            (take 5 (repeat (create-ogre)))
            (take 5 (repeat (create-elemental)))
            (take 5 (repeat (create-magician)))
            (take 5 (repeat (create-skeleton)))
            (take 5 (repeat (create-zombie)))
            (create-werewolf)]
           flatten
           (reduce #(randomize-locations screen rooms %1 %2) []))))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (let [me (find-first :player? entities)]
      (screen! dungeon-crawler-overlay-screen :on-update-health-bar :entity me)
      (->> (map (fn [entity]
                  (->> entity
                       (move screen entities)
                       (animate screen)
                       (prevent-move screen entities)
                       (adjust screen)))
                entities)
           (attack screen (find-first #(can-attack? % me) entities) me)
           play-sounds!
           (render-everything! screen)
           (update-screen! screen))))
  
  :on-resize
  (fn [screen entities]
    (height! screen vertical-tiles))
  
  :on-touch-down
  (fn [screen entities]
    (when (= (:button screen) (button-code :right))
      (let [me (find-first :player? entities)
            victim (get-entity-at-cursor screen entities)
            victim (when (can-attack? me victim) victim)]
        (attack screen me victim entities))))
  
  :on-mouse-moved
  (fn [screen entities]
    (let [e (get-entity-at-cursor screen entities)]
      (update! screen :mouse-npc-id (:id e))
      nil)))

(defscreen dungeon-crawler-overlay-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic) :renderer (stage))
    [(assoc (label "0" (color :white))
            :id :fps
            :x 5)
     (assoc (shape :filled)
            :id :bar
            :x 5
            :y 40)])
  
  :on-render
  (fn [screen entities]
    (->> (for [entity entities]
           (case (:id entity)
             :fps (doto entity (label! :set-text (str (game :fps))))
             entity))
         (render! screen)))
  
  :on-resize
  (fn [screen entities]
    (height! screen 300))
  
  ; custom function that is invoked in main-screen
  :on-update-health-bar
  (fn [screen entities]
    (for [entity entities]
      (case (:id entity)
        :bar (let [me (:entity screen)
                   pct (/ (:health me) (+ (:health me) (:wounds me)))]
               (shape entity
                      :set-color (color :red)
                      :rect 0 0 bar-w bar-h
                      :set-color (color :green)
                      :rect 0 0 bar-w (* bar-h pct)))
        entity))))
