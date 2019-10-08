(ns user
  (:require [clojure.tools.namespace.repl :as tn]
            [study-test.database :as database]
            [mount.core :as mount]))

(defn start []
  (mount/start #'database/server))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go []
  (start)
  :ready)

(defn reset []
  (stop)
  (tn/refresh :after 'user/go))

(mount/in-clj-mode)
