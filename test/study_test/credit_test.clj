(ns study-test.credit-test
  (:require [midje.sweet :refer :all]
            [study-test.credit :as credit]
            [clojure.spec.test.alpha :as test]
            [mount.core :as mount]
            [study-test.database :as database]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]))

(defn check? [spec-check]
  (->> (test/check spec-check)
       test/summarize-results
       :check-passed
       some?))

(facts "Let's talk about the `valid?' function..."
  (fact "It should pass all its spec tests"
    (check? `credit/valid?) => true)

  (fact "But, If I want to use example-based, that's fine too.."
    (let [single {:credit/value 10.0
                  :credit/owner "Wand"
                  :credit/buyer "Captalys"
                  :credit/future-value 12.0}]
      (credit/valid? single) => true)))


(def cfg {:env "TEST" :uri "datomic:mem://credit_TEST"})

(mount/defstate datomic-test
  :start (database/start-datomic! cfg)
  :stop (database/stop-datomic! cfg))

(facts "Let's talk to databases now!"
  (mount/start-with-states {#'database/server #'datomic-test})
  (let [credit (first (gen/sample (s/gen ::credit/credit-spec)))]
    (fact "Saving the credit into datomic TEST databse"
      (credit/save! credit)) => truthy)
  (mount/stop))


(facts "Ok, improving on the above facts.."
  (with-state-changes [(before :facts (mount/start-with-states {#'database/server #'datomic-test}))
                       (after :facts (mount/stop))]
    (let [credit (first (gen/sample (s/gen ::credit/credit-spec)))]
      (fact "Saving the credit into datomic TEST databse"
        (credit/save! credit)) => truthy)))
