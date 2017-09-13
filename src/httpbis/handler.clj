(ns httpbis.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [httpbis.routes.home :refer [home-routes]]))

(defn init []
  (println "httpbis is starting"))

(defn destroy []
  (println "httpbis is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-jsonify [handler]
  (fn [request]
     (let [response (handler request)]
       (if (:httpbis/jsonify response)
         (-> response
             (assoc-in [:headers "Content-Type"] "application/json")
             (update :body json/write-str)
         )
         response))))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)
      (wrap-jsonify)))
