(ns httpbis.routes.home
  (:require [compojure.core :refer :all]
            [httpbis.views.layout :as layout]
            [clj-uuid :as uuid]))

(defn home []
  (layout/common [:h1 "Hello HTTPBIS(1)"]))

(defn ^:endpoint
      ^{:doc "Returns a new UUID4"
        :route "/uuid"
        :verb :GET}
      uuid4 []
  {:status 200
   :httpbis/jsonify true
   :body {:uuid (str (uuid/v4))}})

(defn ^:endpoint
      ^{:doc "Returns Origin IP."
        :route "/ip"
        :verb :GET}
      ip [request]
  {:status 200
   :httpbis/jsonify true
   :body {:origin (:remote-addr request)}})

(defn ^:endpoint
^{:doc "Returns user-agent."
  :route "/user-agent"
  :verb :GET}
user-agent [request]
  {:status 200
   :httpbis/jsonify true
   :body {:user-agent (get-in request [:headers "user-agent"])}})

(def home-routes
  (routes
    (GET "/" [] (home))
    (GET "/uuid" [] (uuid4))
    (GET "/ip" request (ip request))
    (GET "/user-agent" request (user-agent request))
    ))