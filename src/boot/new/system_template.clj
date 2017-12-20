(ns boot.new.system-template
  (:require [boot.new.templates :refer [renderer name-to-path ->files]]))

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
                   :boot-test "1.2.0"})

(defn system-template
  "FIXME: write documentation"
  [name]
  (let [data {:name name
              :lib-versions lib-versions
              :sanitized (name-to-path name)}
        clj-src #(str "src/clj/" name "/" %)
        cljs-src #(str "src/cljs/" name "/" %)]
    (println "Generating fresh 'boot new' system-template project.")
    (->files data
             ["build.boot" (render "build.boot" data)]
             ["resources/index.html" (render "index.html" data)]
             [(clj-src "core.clj") (render "core.clj" data)]
             [(clj-src "routes.clj") (render "routes.clj" data)]
             [(clj-src "systems.clj") (render "systems.clj" data)])))
