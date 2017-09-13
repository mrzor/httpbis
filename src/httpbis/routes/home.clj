(ns httpbis.routes.home
  (:require [compojure.core :refer :all]
            [httpbis.views.layout :as layout]
            [clj-uuid :as uuid]))

(defn home []
  (layout/common [:h1 "Hello HTTPBIS(1)"]))

(defn ^:endpoint
      ^{:doc "Returns a new UUID4"
        :mount ["/uuid" "/uuid/4"]
        :verb :GET}
      uuid4 []
  {:status 200
   :httpbis/jsonify true
   :body {:uuid (str (uuid/v4))}})

(defroutes home-routes
  (GET "/" [] (home))
  (GET "/uuid" [] (uuid4)))
