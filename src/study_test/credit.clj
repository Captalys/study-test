(ns study-test.credit
  (:require [study-test.database :as database]
            [datomic.api :as d]))


(defn valid? [credit]
  (let [blacklist #{"Luis" "Bruno"}]
    (and
     (> (:credit/value credit) 0)
     (not (contains? blacklist (:credit/owner credit)))
     (= (:credit/buyer credit) "Captalys"))))


(defn save! [credit]
  (when (valid? credit)
    (-> (d/transact (:connection database/server) [credit])
        deref
        :tempids
        first
        second)))

(defn update-payment [credit amount-paid]
  (let [adding (fnil + 0)]
    (update credit :credit/paid-value adding amount-paid)))


(defn payment! [credit-owner amount-paid]
  (let [db (d/db (:connection database/server))
        credit (d/q '[:find (pull ?e [*])
                      :in $ ?owner
                      :where
                      [?e :credit/owner ?owner]]
                    db credit-owner)]
    (-> credit
        ffirst
        (update-payment amount-paid)
        vector
        (as-> upd-credit (d/transact (:connection database/server) upd-credit))
        deref)))

(comment
  (def db (d/db (:connection database/server)))

  (def credit {:credit/value 1000.0
               :credit/owner "Wand"
               :credit/buyer "Captalys"
               :credit/future-value 1500.00})

  (save! credit)

  ;; verify if credit is inside the database: query for owner
  (d/q '[:find (pull ?e [*])
         :where
         [?e :credit/owner "Wand"]]
       db)

  (payment! "Wand" 200.00))
