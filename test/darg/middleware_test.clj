(ns darg.middleware-test
  (:require [clojure.test :refer :all]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.handler :as handler]
            [clj-http.client :as client]
            [ring.mock.request :as mock-request]
            [darg.core :as darg]
            [darg.middleware :as middleware]))

(defroutes sample-routes-1
  (GET "/area_51" [] "Omg, aliens here!"))

(def sample-app (-> sample-routes-1 handler/site middleware/ignore-trailing-slash))

(deftest test-trailing-slash-middleware-works
  (is (= (sample-app (mock-request/request :get "/area_51"))
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body "Omg, aliens here!"}))
  (is (= (sample-app (mock-request/request :get "/area_51/"))
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body "Omg, aliens here!"})))
