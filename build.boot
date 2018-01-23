(def project 'system-template/boot-template)
(def version "0.1.0")

(set-env! :resource-paths #{"resources" "src"}
          ;; uncomment this if you write tests for your template:
          ;; :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0"]
                            [boot/new "0.5.2"]
                            [adzerk/bootlaces "0.1.13"] ;; latest release
                            [adzerk/boot-test "1.2.0" :scope "test"]]
          :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                             :username "akash"
                                             :password "shakdwipeea"}]))

(task-options!
 pom {:project     project
      :version     version
      :description "boot template for full stack dev"
      :url         "https://github.com/shakdwipeea/system-template"
      :scm         {:url "https://github.com/shakdwipeea/system-template"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}}
 push {:repo "clojars"})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]]
         '[boot.new :refer [new]])

(require '[adzerk.bootlaces :refer :all])
