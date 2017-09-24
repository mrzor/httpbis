(ns httpbis.repl
  (:require [yada.yada :as yada]
            [clj-uuid :as uuid]))
; XXX get help back

(defonce server (atom nil))
(defonce debug (atom nil))

(defn ^:httpbis/endpoint
^{:doc "Returns a new UUID4"
  :path "/uuid"
  :verb :get}
  uuid4 []
  {:status 200
   :httpbis/jsonify true
   :body {:uuid (str (uuid/v4))}})

(defn ^:httpbis/endpoint
^{:doc "Returns Origin IP."
  :path "/ip"
  :verb :get}
ip [request]
  (reset! debug request)
  {:status 200
   :httpbis/jsonify true
   :body {:ip (:remote-addr (:request request))}})

(defn ^:httpbis/endpoint
^{:doc "Returns user-agent."
  :path "/user-agent"
  :verb :get}
user-agent [request]
  {:status 200
   :httpbis/jsonify true
   :body {:user-agent
          (get-in request [:request :headers :user-agent])}})

;; Bit of meta magic so that you just have to add a new
;; endpoint to the file and it will be served and indexed
;; on the home page.

(def endpoints
  (vals
    (filter #(:httpbis/endpoint (meta (last %))) (ns-publics *ns*))))

(defn endpoint-to-resource [endpoint-var]
  (let [m (meta endpoint-var)
        endpoint-fn (var-get endpoint-var)]
    (yada/resource
      {:produces {:media-type "text/plain"}
       :methods { (get m :verb :get)
                 {:response endpoint-fn}}})))

(defn endpoint-to-yada-pair [endpoint-var]
  [(:path (meta endpoint-var))
   (endpoint-to-resource endpoint-var)])

; WE HAVE TO WRITE IT :D

(defn make-server []
  (yada/listener
    ["" (map endpoint-to-yada-pair endpoints)]
    {:port 3000}))

;; REPL boilerplate

(defn start-server []
  (stop-server)
  (reset! server (make-server)))

(defn stop-server []
  (when @server ((:close @server)))
  (reset! server nil))

(if (some? @server)
  (println "httpbis.repl: not restarting existing Jetty server")
  (when (System/getenv "HTTPBIS_MAGIC_LAUNCH")
    (do
      (println "httpbis.repl: starting server because HTTPBIS_MAGIC_LAUNCH is set")
      (start-server))))

(start-server)
