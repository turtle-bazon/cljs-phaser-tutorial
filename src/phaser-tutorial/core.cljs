(ns phaser-tutorial.core
  )

(def *platforms* (atom nil))

(defn preload-fn [game]
  (let [loader (.-load game)]  
    (.image loader "sky" "assets/sky.png")
    (.image loader "ground" "assets/platform.png")
    (.image loader "star" "assets/star.png")
    (.spritesheet loader "dude" "assets/dude.png" 32 48)))

(defn create-fn [game]
  (let [add (.-add game)]
    (.startSystem (.-physics game) js/Phaser.Physics.ARCADE)
    (.sprite add 0 0 "sky")
    (reset! *platforms* (.group add))
    (set! (.-enableBody *platforms*) true)
    (let [world-h (.-height (.-world game))
          ground (.create @*platforms* 0 (- world-h 64) "ground")
          ledge1 (.create @*platforms* 400 400 "ground")
          ledge2 (.create @*platforms* -150 250 "ground")]
      (.setTo (.-scale ground) 2 2)
      (set! (.-immovable (.-body ground)) true)
      ;(set! (.-immovable (.-body ledge1)) true)
      ;(set! (.-immovable (.-body ledge2)) true)
      )))

(defn update-fn [game]
  )

(defn start []
  (js/Phaser.Game. 800 600 js/Phaser.AUTO "game"
                   (js-obj "preload" preload-fn
                           "create" create-fn
                           "update" update-fn)))

(set! (.-onload js/window) start)
