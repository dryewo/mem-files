(ns mem-files.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [mem-files.core :refer :all]))


(def test-file "target/file-on-disk")


(use-fixtures
  :each (fn [f]
          (spit test-file "haha")
          (f)
          (io/delete-file test-file :silently)))


(deftest works
  (with-open [refresher (start 1000 {:test-file test-file})]
    (testing "Returns contents of the file"
      (is (= "haha" (:test-file @refresher))))

    (spit test-file "bebe")
    (Thread/sleep 1100)

    (testing "Returns updated contents of the file"
      (is (= "bebe" (:test-file @refresher))))))
