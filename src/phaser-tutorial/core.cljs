(ns phaser-tutorial.core
  (:require
   [phzr.animation-manager :as pam]
   [phzr.core :as p :refer [pset!]]
   [phzr.game :as pg]
   [phzr.game-object-factory :as pgof]
   [phzr.group :as pgr]
   [phzr.loader :as pl]
   [phzr.physics :as pp]
   [phzr.physics.arcade :as ppa]
   [phzr.point :as ppnt]))

(def *platforms* (atom nil))

(def *player* (atom nil))

(defn p-preload [game]
  (let [loader (:load game)]
    (doto loader
      (pl/image "sky" "assets/sky.png")
      (pl/image "ground" "assets/platform.png")
      (pl/image "star" "assets/star.png")
      (pl/spritesheet "dude" "assets/dude.png" 32 48))))

(defn p-create [game]
  (let [pgof (:add game)
        physics (:physics game)
        game-world-h (-> game :world :height)]
    (pp/start-system physics (pp/const :arcade))
    (pgof/sprite pgof 0 0 "sky")
    (reset! *platforms* (-> game :add (pgof/group "platforms" false true)))
    (pset! @*platforms* :enable-body true)
    (reset! *player* (pgof/sprite pgof 32 (- game-world-h 120) "dude"))
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
      (-> player :animations (pam/add "right" [5 6 7 8] 10 true))
      )))

(defn p-update [game]
  (-> game :physics :arcade (ppa/collide @*player* @*platforms*)))


(defn start []
  (pg/->Game 800 600 (p/phaser-constants :auto) "game"
             {"preload" p-preload
              "create" p-create
              "update" p-update}))

(set! (.-onload js/window) start)
