(def project {{name}})
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.danielsz/system "{{system}}"]
                            [org.clojure/java.jdbc "{{java-jdbc}}"]
                            [org.clojure/tools.cli "{{tools-cli}}"]
                            [org.clojure/tools.logging "{{tools-logging}}"]
                            [metosin/ring-http-response "{{ring-http-response}}"]
                            [compojure "{{compojure}}"]
                            [ring "{{ring}}"]
                            [org.clojure/tools.nrepl "{{tools-nrepl}}"]
                            [ring/ring-defaults "{{ring-defaults}}"]
                            [ring-middleware-format "{{ring-middleware-format}}"]
                            [adzerk/boot-reload "{{boot-reload}}" :scope "test"]
                            [adzerk/boot-test "{{boot-test}}" :scope "test"]])

(require '[system.boot :refer [system run]]
         '[kongauth.systems :refer [dev-system]]
         '[adzerk.boot-reload :refer :all]
         '[clojure.edn :as edn]
         '[environ.core :refer [env]]
         '[environ.boot :refer [environ]])


(require '[nightlight.boot :refer [nightlight]])
(require '[ragtime.jdbc :as jdbc]
         '[ragtime.repl :as repl])
(require '[kongauth.db.util :as dbutil])

(task-options!
 aot {:namespace   #{'{{name}} .core}}
 jar {:main        '{{name}} .core
      :file        (str "{{name}}-" version "-standalone.jar")})

(deftask dev
  "run a restartable system"
  []
  (comp
   (environ :env (profile))
   (watch :verbose true)
   (system :sys #'dev-system
           :auto true
           :files ["routes.clj" "systems.clj"])
   (nightlight :port 4000)
   (repl :server true
         :host "127.0.0.1"
         :port 8989)))

(deftask build
  "Build the project locally as a JAR."
  [d dir PATH #{str} "the set of directories to write to (target)."]
  (let [dir (if (seq dir) dir #{"target"})]
    (comp (aot) (pom) (uber) (jar) (target :dir dir))))

(deftask run-project
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (require '[kongauth.core :as app])
  (apply (resolve 'app/-main) args))

(require '[adzerk.boot-test :refer [test]])
