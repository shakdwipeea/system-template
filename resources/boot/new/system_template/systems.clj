(ns {{name}}.systems
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [{{name}}.routes :refer [hello-routes]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            (system.components
             [jetty :refer [new-jetty]]
             [endpoint :refer [new-endpoint]]
             [middleware :refer [new-middleware]]
             [repl-server :refer [new-repl-server]]
             [postgres :refer [new-postgres-database]]
             [handler :refer [new-handler]])))

(def rest-middleware
  (fn [handler]
    (wrap-restful-format handler
                         :formats [:json-kw]
                         :response-options {:json-kw {:pretty true}})))

(defn dev-system []
  (component/system-map
   :hello (new-endpoint hello-routes)
   :middleware (new-middleware
                {:middleware  [rest-middleware
                               [wrap-defaults api-defaults]]})
   :handler (component/using
             (new-handler)
             [:hello :middleware])
   :web (component/using (new-jetty :port (Integer. (env :http-port)))
                         [:handler])))

(defn prod-system
  "Assembles and returns components for a production deployment"
  []
  (merge (dev-system)
         (component/system-map
          :repl-server (new-repl-server (read-string (env :repl-port))))))
