(ns {{name}}.routes
  (:require [compojure.core :refer [routes GET]]
            [ring.util.http-response :as response]))

(defn ok-response [response]
  (-> (response/ok response)
      (response/header "Content-Type" "application/json; charset=utf-8")))

(defn hello-routes [_]
  (routes
   (GET "/" [] (ok-response {:msg "Hello world!!"}))))
