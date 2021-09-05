(ns kount-front.app.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defonce counter (r/atom 0))

(defn check-counter []
  (go (let [response (<! (http/get "http://localhost:4000/current"
                                   {:with-credentials? false}))]
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn increment []
  (go (let [response (<! (http/post "http://localhost:4000/inc"
                                   {:with-credentials? false}))]
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn decrement []
  (go (let [response (<! (http/post "http://localhost:4000/dec"
                                   {:with-credentials? false}))]
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn reset []
  (go (let [response (<! (http/post "http://localhost:4000/reset"
                                   {:with-credentials? false}))]
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn app []
  [:div.d-flex.justify-content-center
   [:div.card.text-center.w-50.border-dark
    [:div.card-header.border-dark "clj-k8ount"]
    [:div.card-body
     [:h5.card-title (str "Value of counter is " @counter)]
     [:div.btn-group
      [:button.btn.btn-success {:on-click increment} "Increment"]
      [:button.btn.btn-danger {:on-click decrement} "Decrement"]
      [:button.btn.btn-warning {:on-click reset} "Reset!!!"]]]]])

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (check-counter)
  (render))

(defn ^:dev/after-load reload! []
  (check-counter)
  (render))
