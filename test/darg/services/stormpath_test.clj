(ns darg.services.stormpath-test
  (:require [clojure.test :refer :all]
            [darg.services.stormpath :as stormpath]))

(deftest we-can-create-and-delete-a-user-in-stormpath
  (is (stormpath/create-account {:email "test-user@darg.io"
                                      :password "ohmyglob"
                                      :givenName "LSP"
                                      :surname "lumpy space?"}))
  (is (= 204 (:status (stormpath/delete-account-by-email
                        "test-user@darg.io")))))

(deftest we-can-find-a-user-in-stormpath
  (is (stormpath/search-for-account-by-email "david@ursacorp.io"))
  (is (stormpath/get-search-results
        (stormpath/search-for-account-by-email "david@ursacorp.io")))
  ;; Stormpath searches return maps with status 200
  (is (stormpath/search-for-account-by-email "test-user@darg.gio"))
  (is (not (stormpath/get-search-results
             (stormpath/search-for-account-by-email "test-user@darg.gio")))))

(deftest we-can-update-a-user-in-stormpath)

(deftest we-can-authenticate-a-user-in-stormpath)
