(ns file-kit.core-test
  (:use [file-kit.core] :reload)
  (:use [clojure.test])
  (:require [clojure.java.io :as io])
  (:import java.io.File))

(def test-file (let [tf (create-temp-file "core_test" ".txt")]
                 (spit tf "12345678901")
                 tf))

(def tmp-dir (create-temp-dir))

(defn tmp-dir-fixture [f]
  (do
    (create-temp-dir)
    (f)))

(deftest can-spit-out-canonical-path
  (is (not (nil? (canonical-path tmp-dir))))
  (is (not (nil? (canonical-path test-file)))))

(deftest can-recognise-a-file
  (is (file? test-file))
  (is (file? (canonical-path test-file))))

(deftest can-recognise-a-dir
  (is (directory? tmp-dir))
  (is (directory? (canonical-path tmp-dir)))
  (is (directory? (io/file "src")))
  (is (not (directory? nil))))

(deftest can-recognise-existence
  (is (exists? test-file))
  (is (exists? (canonical-path test-file)))
  (is (not (exists? (io/file "hgjds786ghjkfsd"))))
  (is (not (exists? nil))))

(deftest test-size
  (is (= 11 (size test-file)))
  (is (= 11 (size (canonical-path test-file)))))

(deftest user-has-home
  (is (not (nil? HOME))))

(deftest can-ls
  (let [new-tmp-dir (create-temp-dir)]
    (spit (io/file new-tmp-dir "aaaa") "aaa")
    (spit (io/file new-tmp-dir "bbbb") "bbb")
    (is (= 2 (count (ls new-tmp-dir))))
    (is (= 2 (count (ls (canonical-path new-tmp-dir)))))))

(deftest test-touch
  (let [file (io/file tmp-dir "test-touch")]
    (do
      (is (not (exists? file)))
      (touch file)
      (is (exists? file)))))

(deftest can-mkdir
  (let [new-dir (mkdir (io/file tmp-dir "newdir"))]
    (do
      (is (exists? new-dir))
      (is (directory? new-dir)))))

(deftest can-makdir-p
  (let [new-dir2 (mkdir-p (io/file tmp-dir "newdir2" "newdir3"))]
    (do
      (is (exists? new-dir2))
      (is (directory? new-dir2)))))

(deftest test-cp
  (let [to-file (io/file tmp-dir "test-cp")]
    (do
      (cp test-file to-file)
      (is (exists? to-file))
      (is (file? to-file))
      (is (= (.lastModified test-file)
             (.lastModified to-file))))))

(deftest can-rm
  (let [newfile (create-temp-file)]
    (do
      (is (exists? newfile))
      (rm newfile)
      (is (not (exists? newfile))))))

(deftest can-rm-rf
  (let [newdir (create-temp-dir) newfile (io/file newdir "somefile")]
    (do
      (touch newfile)
      (rm-rf newdir)
      (is (not (exists? newfile))))))

(deftest test-mv-file-to-file
  (let [from-file (io/file tmp-dir "from-file")
        to-file (io/file tmp-dir "to-file")]
    (do
      (cp test-file from-file)
      (is (exists? from-file))
      (mv from-file to-file)
      (is (not (exists? from-file)))
      (is (exists? to-file))
      (is (file? to-file)))))

(deftest test-mv-file-to-dir
  (let [from-file (io/file tmp-dir "from-file")
        to-dir (io/file tmp-dir "to-dir")]
    (do
      (cp test-file from-file)
      (is (exists? from-file))
      (mkdir to-dir)
      (mv from-file to-dir)
      (is (not (exists? from-file)))
      (is (file? (io/file to-dir (.getName from-file)))))))

(deftest test-mv-dir-to-dir
  (let [from-dir (io/file tmp-dir "from-dir")
        to-dir (io/file tmp-dir "to-dir")]
    (do
      (mkdir from-dir)
      (mv from-dir to-dir)
      (is (not (exists? from-dir)))
      (is (exists? to-dir))
      (is (directory? to-dir)))))

(use-fixtures :each tmp-dir-fixture)

