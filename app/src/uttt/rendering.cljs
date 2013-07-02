(ns uttt.rendering
  (:require [domina :as dom]
            [domina.events :as dom-events]
            [io.pedestal.app.protocols :as p]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render.events :as events]
            [io.pedestal.app.render.push :as render]
            [io.pedestal.app.render.push.templates :as templates]
            [io.pedestal.app.render.push.handlers.automatic :as d])
  (:require-macros [uttt.html-templates :as html-templates]))

(def templates (html-templates/uttt-templates))

(defn render-page [renderer [_ path] transmitter]
  (let [parent (render/get-parent-id renderer path)
        id (render/new-id! renderer path)
        html (templates/add-template renderer path (:uttt-board templates))]
    (dom/append! (dom/by-id parent) (html {:id id :board ""}))

    (doseq [i (range 9)
            :let [path (conj path i)
                  outer-square-id (render/new-id! renderer path)
                  outer-square-template (templates/add-template renderer path
                                                                (:uttt-square templates))]]
      (dom/append! (dom/by-id id) (outer-square-template {:id outer-square-id}))
      (doseq [i (range 9)
              :let [path (conj path i)
                    inner-square-id (render/new-id! renderer path)
                    inner-square-template (templates/add-template renderer path
                                                                  (:uttt-inner-square templates))]]
        (dom/append! (dom/by-id outer-square-id)
                     (inner-square-template {:id inner-square-id :move " "}))))))

(defn render-move [renderer [_ path _ new-value] transmitter]
  (templates/update-t renderer path {:move (case new-value
                                             :x "X"
                                             :o "O"
                                             nil " ")}))

(defn enable-moves [renderer [_ path transform-name messages] transmitter]
  (doseq [i (range 9)
          :let [path (conj path i)]]
    (doseq [i (range 9)
            :let [path (conj path i)]]
      (dom/log [transform-name messages])
      (dom-events/listen! (dom/by-id (render/get-id renderer path))
                          :click
                          (fn [e]
                            (p/put-message transmitter {msg/type :play-move msg/topic path :player :x}))))))

(defn disable-moves [renderer [_ path transform-name messages] transmitter]
  (doseq [i (range 9)
          :let [path (conj path i)]]
    (doseq [i (range 9)
            :let [path (conj path i)]] 
      (dom-events/unlisten! (dom/by-id (render/get-id renderer path))))))

(defn render-config []
  [[:node-create       [:ttt]       render-page]
   [:node-destroy      [:ttt]       d/default-exit]
   [:value             [:ttt :* :*] render-move]
   [:transform-enable  [:ttt]       enable-moves]
   [:transform-disable [:ttt]       disable-moves]])
