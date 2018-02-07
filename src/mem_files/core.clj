(ns mem-files.core
  (:require [clojure.java.io :as io])
  (:import (java.lang AutoCloseable)
           (clojure.lang IDeref)))


(defn- slurp-if-exists [file]
  (when (.exists (io/as-file file))
    (slurp file)))


(defn- load-one [*cache k file parse-fn]
  ;; If the file is missing or parse-fn throws, will put nil
  (let [new-value (try (some-> (slurp-if-exists file)
                               parse-fn)
                       (catch Exception _))]
    (swap! *cache assoc k new-value)))


(defn- load-all [*cache keys-files parse-fn]
  (doseq [[k f] keys-files]
    (load-one *cache k f parse-fn)))


(defn- worker [f interval-ms]
  (loop []
    (when-not (= ::stop
                 (try
                   (Thread/sleep interval-ms)
                   (f)
                   (catch InterruptedException e
                     ::stop)
                   (catch Exception e)))
      (recur))))


(deftype MemFiles [*cache thread]
  IDeref
  (deref [_] @*cache)
  AutoCloseable
  (close [_] (.interrupt thread)))


(defn start
  ([interval-ms keys-files]
   (start interval-ms keys-files identity))
  ([interval-ms keys-files parse-fn]
   (let [*cache (atom {})
         _      (load-all *cache keys-files parse-fn)
         thread (doto (Thread. (fn [] (worker #(load-all *cache keys-files parse-fn) interval-ms))) .start)]
     (MemFiles. *cache thread))))
