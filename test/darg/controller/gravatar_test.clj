(ns darg.controller.gravatar-test
  (:require [clojure.test :refer :all]
            [darg.controller.gravatar :as api]))

(deftest gravar-image-is-what-it-should-be
  (is (= (api/gravatar {:session {:email "venantius@gmail.com"}
                        :params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/6b653616a592b8bdc296b0abf6207a71?s=40"
          :status 200}))
  (is (= (api/gravatar {:params {:size "40"}})
         {:body "http://www.gravatar.com/avatar/?s=40"
          :status 200})))
