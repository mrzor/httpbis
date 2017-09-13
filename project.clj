(defproject httpbis "0.1.0-SNAPSHOT"
  :description "A friendly accolade to httpbin.org "
  :url "http://httpbis.org"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.2"]
                 [hiccup "1.0.5"]
                 [ring-server "0.4.0"]
                 [danlentz/clj-uuid "0.1.7"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler httpbis.handler/app
         :init httpbis.handler/init
         :destroy httpbis.handler/destroy}
  :repl-options {:init-ns httpbis.repl}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.5.1"]]}})
