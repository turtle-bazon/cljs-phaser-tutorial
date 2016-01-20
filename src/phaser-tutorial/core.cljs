(ns phaser-tutorial.core
  (:require
   [phzr.animation-manager :as pam]
   [phzr.core :as p :refer [pset!]]
   [phzr.game :as pg]
   [phzr.game-object-factory :as pgof]
   [phzr.group :as pgr]
   [phzr.keyboard :as pk]
   [phzr.loader :as pl]
   [phzr.physics :as pp]
   [phzr.physics.arcade :as ppa]
   [phzr.point :as ppnt]
   [phzr.sprite :as sprite]))

(defonce *game* (atom nil))

(defonce *score* (atom 0))

(defonce *score-text* (atom nil))

(defonce *platforms* (atom nil))

(defonce *player* (atom nil))

(defonce *cursors* (atom nil))

(defonce *stars* (atom nil))

(defn preload-game [game]
  (let [loader (:load game)]
    (doto loader
      (pl/image "sky" "assets/sky.png")
      (pl/image "ground" "assets/platform.png")
      (pl/image "star" "assets/star.png")
      (pl/spritesheet "dude" "assets/dude.png" 32 48))))

(defn add-stars [game]
  (dotimes [i 12]
    (let [star (pgr/create @*stars* (* i 70) 0 "star")
          star-body (:body star)]
      (pset! (:gravity star-body) :y 6)
      (pset! (:bounce star-body) :y (+ 0.7 (rand 0.2))))))

(defn create-game [game]
  (let [pgof (:add game)
        physics (:physics game)
        game-world-h (-> game :world :height)]
    (pp/start-system physics (pp/const :arcade))
    (pgof/sprite pgof 0 0 "sky")
    (reset! *score-text* (pgof/text pgof 16 16 "Score: 0" {"fontSize" "32px"
                                                           "fill" "#000"}))
    (reset! *platforms* (pgof/group pgof "platforms" false true))
    (pset! @*platforms* :enable-body true)
    (reset! *player* (pgof/sprite pgof 32 (- game-world-h 120) "dude"))
    (reset! *stars* (pgof/group pgof))
    (pset! @*stars* :enable-body true)
    (let [ground (pgr/create @*platforms* 0 (- game-world-h 64) "ground")
          ledge1 (pgr/create @*platforms* 400 400 "ground")
          ledge2 (pgr/create @*platforms* -150 250 "ground")
          player @*player*]
      (-> ground :scale (ppnt/set-to 2 2))
      (pset! (-> ground :body) :immovable true)
      (pset! (-> ledge1 :body) :immovable true)
      (pset! (-> ledge2 :body) :immovable true)
      (-> physics :arcade (pp/enable player))
      (pset! (-> player :body :bounce) :y 0.2)
      (pset! (-> player :body :gravity) :y 300)
      (pset! (-> player :body) :collide-world-bounds true)
      (-> player :animations (pam/add "left" [0 1 2 3] 10 true))
      (-> player :animations (pam/add "right" [5 6 7 8] 10 true)))
    (add-stars game)
    (reset! *cursors* (pk/create-cursor-keys (-> game :input :keyboard)))))

(defn collect-stars [player star]
  (sprite/destroy star)
  (swap! *score* + 10)
  (pset! @*score-text* :text (str "Score: " @*score*))
  (when (= 0 (pgr/count-living @*stars*))
    (add-stars @*game*)))

(defn update-game [game]
  (-> game :physics :arcade (ppa/collide @*player* @*platforms*))
  (-> game :physics :arcade (ppa/collide @*stars* @*platforms*))
  (-> game :physics :arcade (ppa/overlap @*player* @*stars* collect-stars))
  (pset! (-> @*player* :body :velocity) :x 0)
  (cond
    (-> @*cursors* :left :is-down) (do (pset! (-> @*player* :body :velocity) :x -150)
                                       (pam/play (-> @*player* :animations) "left"))
    (-> @*cursors* :right :is-down) (do (pset! (-> @*player* :body :velocity) :x 150)
                                        (pam/play (-> @*player* :animations) "right"))
    :else (do (pam/stop (-> @*player* :animations))
              (pset! @*player* :frame 4)))
  (when (and (-> @*cursors* :up :is-down)
             (aget (-> @*player* :body :touching) "down"))
    (pset! (-> @*player* :body :velocity) :y -350)))


(defn start []
  (when @*game*
    (pg/destroy @*game*)
    (reset! *game* nil))
  (reset! *game* (pg/->Game 800 600 (p/phaser-constants :auto) "game"
                            {"preload" preload-game
                             "create" create-game
                             "update" (fn [game] (update-game game))})))

(set! (.-onload js/window) start)
