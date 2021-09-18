(ns kount-front.app.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defonce counter (r/atom 0))

(defonce api-url "http://localhost:4000")

(defonce health (r/atom false))

(defn build-url [additional-path]
  (str api-url additional-path))

(defn check-health []
  (go (let [response (<! (http/get (build-url "/health")
                                   {:with-credentials? false}))]
        (reset! health (:success response)))))

(defn healthy? []
  @health)

(defn check-counter []
  (go (let [response (<! (http/get (build-url "/current")
                                   {:with-credentials? false}))]
        (reset! health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn increment []
  (go (let [response (<! (http/post (build-url "/inc")
                                    {:with-credentials? false}))]
        (reset! health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn decrement []
  (go (let [response (<! (http/post (build-url "/dec")
                                    {:with-credentials? false}))]
        (reset! health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn reset []
  (go (let [response (<! (http/post (build-url "/reset")
                                    {:with-credentials? false}))]
        (reset! health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn app []
  [:div.d-flex.justify-content-center
   [:div.card.text-center.w-50.border-dark
    [:div.card-header.border-dark "clj-k8ount"]
    (if (healthy?)
      [:div.card-body
       [:h5.card-title (str "Value of counter is " @counter)]
       [:div.btn-group
        [:button.btn.btn-success {:on-click increment} "Increment"]
        [:button.btn.btn-danger {:on-click decrement} "Decrement"]
        [:button.btn.btn-warning {:on-click reset} "Reset!!!"]]]
      [:div.caard-body
       [:h5.card-title.p-2 "Service temporary unavailable ¯\\_(ツ)_/¯"]
       [:button.btn.btn-primary.w-100 {:on-click check-health} "Ping service"]])]])

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (check-health)
  (when (healthy?) (check-counter))
  (render))

(defn ^:dev/after-load reload! []
  (check-health)
  (when (healthy?) (check-counter))
  (render))
