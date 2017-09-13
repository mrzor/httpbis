(ns httpbis.repl
  (:use httpbis.handler
        ring.server.standalone
        [ring.middleware file-info file])
  (:import (org.eclipse.jetty.server Server)))

(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
    (wrap-file "resources")
    ; Content-Type, Content-Length, and Last Modified headers for files in body
    (wrap-file-info)))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(if (and @server (.isStarted ^Server @server))
  (println "httpbis.repl: not restarting existing Jetty server")
  (when (System/getenv "HTTPBIS_MAGIC_LAUNCH")
    (do
      (println "httpbis.repl: starting server because HTTPBIS_MAGIC_LAUNCH is set")
      (start-server)))
  )