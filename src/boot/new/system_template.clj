(ns boot.new.system-template
  (:require [boot.new.templates :refer [renderer name-to-path ->files slurp-to-lf]]))

(def render (renderer "system-template"))

(def lib-versions {:boot-reload "0.5.2"
                   :java-jdbc "0.7.3"
                   :system "0.4.2-SNAPSHOT"
                   :environ "1.1.0"
                   :tools-cli "0.3.5"
                   :ring-http-response "0.9.0"
                   :compojure "1.6.0"
                   :ring "1.6.3"
                   :tools-nrepl "0.2.12"
                   :ring-defaults "0.3.1"
                   :tools-logging "0.4.0"
                   :ring-middleware-format "0.7.2"
                   :boot-test "1.2.0"
                   :clojure "1.9.0"
                   :clojurescript "1.9.946"
                   :immutant "2.1.9"})


;; (def cljs-libs {:bide "0.10.1"
;;                 :boot-reload "0.5.2"
;;                 :boot-test "1.2.0"
;;                 :boot-cljs "2.1.4"
;;                 :boot-cljs-repl "0.3.3"
;;                 :piggieback "0.2.1"
;;                 :devtools "0.9.4"
;;                 :weasel "0.7.0"})

(def cljs-deps "[reagent \"0.8.0-alpha2\"]
                            [reagi \"0.10.1\"]
                            [funcool/bide \"1.6.0\"]
                            [adzerk/boot-reload \"0.5.2\" :scope \"test\"]
                            [adzerk/boot-test \"1.2.0\" :scope \"test\"]
                            [adzerk/boot-cljs \"2.1.4\" :scope \"test\"]
                            [adzerk/boot-cljs-repl \"0.3.3\" :scope \"test\"]
                            [adzerk/boot-test \"1.2.0\" :scope \"test\"]
                            [adzerk/boot-reload \"0.5.2\" :scope \"test\"]
                            [com.cemerick/piggieback \"0.2.1\" :scope \"test\"]
                            [binaryage/devtools \"0.9.4\" :scope \"test\"]
                            [weasel \"0.7.0\" :scope \"test\"]")

(defn cljs? [opts]
  (some #{"+cljs"} opts))

(def cljs-dev
  "(deftask dev
     \"run a restartable system\"
     []
     (comp
      (environ :env {:http-port \"7000\"})
      (watch :verbose true)
      (system :sys #'dev-system
              :auto true
              :files [\"routes.clj\" \"systems.clj\"])
      (repl :server true
            :host \"127.0.0.1\")
      (reload :asset-path \"public\")
      (cljs-repl)
      (cljs :source-map true :optimizations :none)))")

(def clj-dev
  "(deftask dev
     \"run a restartable system\"
     []
     (comp
      (environ :env {:http-port \"7000\"})
      (watch :verbose true)
      (system :sys #'dev-system
              :auto true
              :files [\"routes.clj\" \"systems.clj\"])
      (repl :server true
            :host \"127.0.0.1\")))")

(def cljs-require-libs  "(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-cljs-repl :refer :all]
         '[adzerk.boot-reload :refer :all])")

(def clj-dev-system
  "(defn dev-system []
    (component/system-map
     :hello (new-endpoint hello-routes)
     :middleware (new-middleware
                  {:middleware  [rest-middleware
                                 [wrap-defaults api-defaults]]})
     :handler (component/using
               (new-handler)
               [:hello :middleware])
     :web (component/using (new-immutant-web :port (Integer. (env :http-port)))
                           [:handler])))")

(def cljs-dev-system 
  "(defn dev-system []
    (component/system-map
     :site-endpoint (component/using (new-endpoint site)
                                     [:site-middleware])
     :api-endpoint (component/using (new-endpoint hello-routes)
                                    [:api-middleware])
     :site-middleware (new-middleware {:middleware [[wrap-defaults site-defaults]]})
     :api-middleware (new-middleware
                      {:middleware  [rest-middleware
                                     [wrap-defaults api-defaults]]})
     :handler (component/using (new-handler) [:api-endpoint :site-endpoint])
     :api-server (component/using (new-immutant-web :port (Integer. (env :http-port)))
                                  [:handler])))")


(def site-routes
  "(defn home-page []
    (-> (response/file-response \"index.html\"
                                {:root \"resources\"})
        (response/header \"Content-Type\" \"text/html\")))

  (defn site [_]
    (routes
     (GET \"/\" [] (home-page))
     (ANY \"*\" [] (home-page))))")
  

(defn system-template
  "FIXME: write documentation"
  [name & opts]
  (let [data {:name name
              :lib-versions lib-versions
              :sanitized (name-to-path name)
              :dev (if (cljs? opts) cljs-dev clj-dev)
              :cljs-require (when (cljs? opts) cljs-require-libs)
              :resource-paths (if (cljs? opts)
                                #{"resources" "src/clj" "src/cljs"}
                                #{"src/clj"})
              :dev-system (if (cljs? opts)
                            cljs-dev-system
                            clj-dev-system)
              :site-routes site-routes
              :cljs-deps (when (cljs? opts) cljs-deps)}
        clj-src #(str "src/clj/" (name-to-path name) "/" %)
        cljs-src #(str "src/cljs/" (name-to-path name) "/" %)]
    (println "Generating fresh 'boot new' system-template project.")
    (apply ->files data
           (into [] (concat
                     [["build.boot" (render "build.boot" data)]]
                     (when (cljs? opts)
                       (vector ["resources/public/main.cljs.edn" (render "main.cljs.edn" data)]
                               ["resources/index.html" (render "index.html" data)]))
                     (vector [(clj-src "core.clj") (render "core.clj" data)]
                             [(clj-src "routes.clj") (render "routes.clj" data)]
                             [(clj-src "systems.clj") (render "systems.clj" data)]
                             [(cljs-src "app.cljs") (render "app.cljs" data)]
                             [(cljs-src "ui.cljs") (render "ui.cljs" data)]))))))
