(ns study-test.credit
  (:require [study-test.database :as database]
            [datomic.api :as d]))


(defn save! [credit])

(defn delete! [credit])

(defn payment [credit-owner pay])

(comment
  (def credit {:credit/value 1000.0
               :credit/owner "Wand"
               :credit/buyer "Captalys"
               :credit/future-value 1500.00})

  (save! credit)

  ;; verify if credit is inside the database: query for owner

  (delete! credit)

  ;; verify that the credit is not in the database anymore


  (save! credit)
  (payment "Wand" 200.00)
  (payment "Wand" 200.00)
  ;; verify that the credit has a field called paid-value that is incrementally adding

  )
