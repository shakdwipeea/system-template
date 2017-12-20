(def project '{{name}})
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src/clj"}
          :dependencies   '[[org.danielsz/system "{{lib-versions.system}}"]
                            [org.clojure/java.jdbc "{{lib-versions.java-jdbc}}"]
                            [org.clojure/tools.cli "{{lib-versions.tools-cli}}"]
                            [org.clojure/tools.logging "{{lib-versions.tools-logging}}"]
                            [metosin/ring-http-response "{{lib-versions.ring-http-response}}"]
                            [compojure "{{lib-versions.compojure}}"]
                            [environ "{{lib-versions.environ}}"]
                            [boot-environ "{{lib-versions.environ}}"]
                            [ring "{{lib-versions.ring}}"]
                            [org.clojure/tools.nrepl "{{lib-versions.tools-nrepl}}"]
                            [ring/ring-defaults "{{lib-versions.ring-defaults}}"]
                            [ring-middleware-format "{{lib-versions.ring-middleware-format}}"]
                            [adzerk/boot-reload "{{lib-versions.boot-reload}}" :scope "test"]
                            [adzerk/boot-test "{{lib-versions.boot-test}}" :scope "test"]])

(require '[system.boot :refer [system run]]
         '[{{name}}.systems :refer [dev-system]]
         '[adzerk.boot-reload :refer :all]
         '[clojure.edn :as edn]
         '[environ.core :refer [env]]
         '[environ.boot :refer [environ]])
; 
; (require '[ragtime.jdbc :as jdbc]
;          '[ragtime.repl :as repl])

(task-options!
 aot {:namespace   #{'{{name}}.core}}
 jar {:main        '{{name}}.core
      :file        (str "{{name}}-" version "-standalone.jar")})

(deftask dev
  "run a restartable system"
  []
  (comp
   (environ :env {:http-port "8080"})
   (watch :verbose true)
   (system :sys #'dev-system
           :auto true
           :files ["routes.clj" "systems.clj"])
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
