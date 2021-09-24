(ns kount-front.app.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defonce counter (r/atom 0))

(defonce api-url "http://localhost:4000")

(defonce summer-url "http://localhost:5000")

(defonce api-health (r/atom false))

(defonce summer-health (r/atom false))

(defn build-api-url [additional-path]
  (str api-url additional-path))

(defn build-summer-url [additional-path]
  (str summer-url additional-path))

(defn check-api-health []
  (go (let [response (<! (http/get (build-api-url "/health")
                                   {:with-credentials? false}))]
        (reset! api-health (:success response)))))

(defn check-summer-health []
  (go (let [response (<! (http/get (build-summer-url "/health")
                                   {:with-credentials? false}))]
        (reset! summer-health (:success response)))))

(defn api-healthy? []
  @api-health)

(defn summer-healthy? []
  @summer-health)

(defn check-counter []
  (go (let [response (<! (http/get (build-api-url "/current")
                                   {:with-credentials? false}))]
        (reset! api-health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn increment []
  (go (let [response (<! (http/post (build-api-url "/inc")
                                    {:with-credentials? false}))]
        (reset! api-health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn decrement []
  (go (let [response (<! (http/post (build-api-url "/dec")
                                    {:with-credentials? false}))]
        (reset! api-health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn reset []
  (go (let [response (<! (http/post (build-api-url "/reset")
                                    {:with-credentials? false}))]
        (reset! api-health (:success response))
        (when (= (:status response) 200)
          (reset! counter (:counter (:body response)))))))

(defn app []
  [:div.d-flex.justify-content-center
   [:div.card.text-center.w-50.border-dark
    [:div.card-header.border-dark "clj-k8ount"]
    (if (api-healthy?)
      [:div.card-body
       [:h5.card-title (str "Value of counter is " @counter)]
       [:div.btn-group
        [:button.btn.btn-success {:on-click increment} "Increment"]
        [:button.btn.btn-danger {:on-click decrement} "Decrement"]
        [:button.btn.btn-warning {:on-click reset} "Reset!!!"]]]
      [:div.card-body
       [:h5.card-title.p-2 "API service temporary unavailable ¯\\_(ツ)_/¯"]
       [:button.btn.btn-primary.w-100 {:on-click check-api-health} "Ping service"]])
    (if (summer-healthy?)
      [:div "Summer is up!"]
      [:div
       [:h5.p-2 "Summer service temporary unavailable ¯\\_(ツ)_/¯"]
       [:button.btn.btn-primary.w-100 {:on-click check-summer-health} "Ping service"]])]])

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (check-api-health)
  (check-summer-health)
  (when (api-healthy?) (check-counter))
  (render))

(defn ^:dev/after-load reload! []
  (check-api-health)
  (check-summer-health)
  (when (api-healthy?) (check-counter))
  (render))
