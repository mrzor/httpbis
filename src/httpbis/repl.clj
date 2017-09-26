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
  uuid4 [context]
  {:uuid (str (uuid/v4))})

(defn ^:httpbis/endpoint
^{:doc "Returns Origin IP."
  :path "/ip"
  :verb :get}
ip [context]
  {:ip (:remote-addr (:request context))})

(defn ^:httpbis/endpoint
^{:doc "Returns user-agent."
  :path "/user-agent"
  :verb :get}
user-agent [context]
  {:user-agent (get-in context [:request :headers :user-agent])})

; request-reflector is an endpoint like fn but it is not
; fully specified (no documentation, path or verb set)
;
(defn request-reflector [context]
  (let [interesting-keys (->> (keys (:request context))
                              (remove #(= (namespace %) "aleph")))]
    (select-keys (:request context) interesting-keys)))


(defn http-verb-to-metaendpoint
  "Takes a string as its only parameter, this string
  should be an HTTP verb.
  Produces a map suitable to be used as metaendpoint i.e.
  as metadata around a fn."
  [verb]
  {:httpbis/endpoint true
   :doc (str "Returns " (.toUpperCase verb) " data.")
   :path (str "/" verb)
   :verb (keyword verb)
   :sym (symbol (str "metaendpoint-" verb))})

(def http-verbs-metaendpoints
  (map http-verb-to-metaendpoint ["get" "post" "patch"
                                  "put" "delete"]))

(doseq [{s :sym
         :as metaendpoint} http-verbs-metaendpoints]
  (let [sm (with-meta s metaendpoint)]
    (intern *ns* s request-reflector)))


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
      {:produces {:media-type "application/json"}
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

(defn stop-server []
  (when @server ((:close @server)))
  (reset! server nil))

(defn start-server []
  (stop-server)
  (reset! server (make-server)))

(if (some? @server)
  (println "httpbis.repl: not restarting existing Jetty server")
  (when (System/getenv "HTTPBIS_MAGIC_LAUNCH")
    (do
      (println "httpbis.repl: starting server because HTTPBIS_MAGIC_LAUNCH is set")
      (start-server))))

(start-server)
