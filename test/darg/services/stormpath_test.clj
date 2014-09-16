(ns darg.services.stormpath-test
  (:require [clojure.test :refer :all]
            [darg.services.stormpath :as stormpath]
            [slingshot.slingshot :refer [try+ throw+]]))

;; for creation and deletion
(def user-1 {:email "test-user@darg.io"
             :password "ohmyglob"
             :givenName "LSP"
             :surname "lumpy space?"})

;; for authentication -- already seeded in Stormpath, do not delete
(def user-2 {:email "test-user2@darg.io"
             :password "samurai"
             :givenName "Finn"
             :surname "the Human"})

;; Malformed/incomplete user -- to test failures
(def quasi-user {:email "quasi-user@darg.io"
             :givenName "Cinnamon Bun"})

(deftest we-can-convert-a-darg-user-to-a-stormpath-user
  (is (= (stormpath/user->account {:email "test-user@darg.io"
                                   :password "ohmyglob"
                                   :first_name "LSP"
                                   :last_name "lumpy space?"})
         user-1)))

(deftest we-can-convert-a-stormpath-user-to-a-darg-user
  (is (= (stormpath/account->user user-1)
         {:email "test-user@darg.io"
          :password "ohmyglob"
          :first_name "LSP"
          :last_name "lumpy space?"})))

(deftest we-can-create-and-delete-a-user-in-stormpath
  (is (stormpath/create-account user-1))
  (is (= 204 (:status (stormpath/delete-account-by-email
                        (:email user-1))))))

(deftest we-can-find-a-user-in-stormpath
  (is (stormpath/search-for-account-by-email "david@ursacorp.io"))
  (is (stormpath/get-search-results
        (stormpath/search-for-account-by-email "david@ursacorp.io")))
  ;; Stormpath searches return maps with status 200
  (is (stormpath/search-for-account-by-email "test-user@darg.io"))
  (is (not (stormpath/get-search-results
             (stormpath/search-for-account-by-email "test-user@darg.io")))))

(deftest we-can-update-a-user-in-stormpath
  (stormpath/create-account user-1)
  (stormpath/update-account-by-email "test-user@darg.io" {:surname "blobs"})
  (is (= "blobs" (:surname (stormpath/get-search-results
    (stormpath/search-for-account-by-email "test-user@darg.io")))))
  (stormpath/delete-account-by-email (:email user-1)))

(deftest we-can-authenticate-a-user-in-stormpath
  (is (stormpath/authenticate "test-user2@darg.io" "samurai"))
  (try+ (stormpath/authenticate "test-user2@darg.io" "ronin")
        (is (= 1 0)) ;; shouldn't get to here
       (catch [:status 400] e ;; should throw a 400 -- cannot authenticate
         (is (= 1 1)))))
