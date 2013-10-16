(ns file-kit.core
  (:require [clojure.java.io :as io])
  (:import [java.io File])
  (:import [org.apache.commons.io FileUtils]
           [org.clojars.fdiotalevi.guava GuavaFiles])
  (:gen-class))


(defn- delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its contents.
Raise an exception if any deletion fails unless silently is true."
  [f & [silently]]
  (let [f (io/file f)]
    (if (.isDirectory f)
      (doseq [child (.listFiles f)]
        (delete-file-recursively child silently)))
    (io/delete-file f silently)))


(defmacro defun [name docstring args & body]
  `(do
     (defmulti ~name ~docstring class)
     (defmethod ~name File ~args ~@body)
     (defmethod ~name String ~args (~name (io/file ~@args)))
     (defmethod ~name :default ~args false)))

(defn canonical-path
  "Returns the canonical path of the file or directory."
  [path]
  (.getCanonicalPath (io/file path)))

(defun file?
  "Returns true if the path is a file; false otherwise."
  [path]
  (.isFile ^File path))

(defun directory?
  "Returns true if the path is a directory; false otherwise."
  [path]
  (.isDirectory ^File path))

(defun exists?
  "Returns true if path exists; false otherwise."
  [path]
  (.exists ^File path))

(defn size
  "Returns the size in bytes of a file."
  [file]
  (.length (io/file file)))

(defn ls
  "List files in a directory."
  [dir]
  (seq (.listFiles (io/file dir))))

(defn touch
  "Create a file or update the last modified time."
  [path]
  (let [file (io/file path)]
    (do
      (.createNewFile file)
      (.setLastModified file (System/currentTimeMillis)))))

(defn mkdir
  "Create a directory."
  [dir]
  (let [file-dir (io/file dir)]
    (do
      (.mkdir file-dir)
      file-dir)))

(defn mkdir-p
  "Create a directory and all parent directories if they do not exist."
  [dir]
  (let [file-dir (io/file dir)]
    (.mkdirs file-dir)
    file-dir))

(defn rm
  "Remove a file. Will throw an exception if the file cannot be deleted."
  [file]
  (io/delete-file file))

(defn rm-f
  "Remove a file, ignoring any errors."
  [file]
  (io/delete-file file true))

(defn rm-r
  "Remove a directory. The directory must be empty; will throw an exception
    if it is not or if the file cannot be deleted."
  [path]
  (delete-file-recursively path))

(defn rm-rf
  "Remove a directory, ignoring any errors."
  [path]
  (delete-file-recursively path true))

(defn cp
  "Copy a file, preserving last modified time by default."
  [from to & {:keys [preserve] :or {preserve true}}]
  (let [from-file (io/file from)
        to-file (io/file to)]
    (FileUtils/copyFile from-file to-file preserve)))

(defn cp-r
  "Copy a directory, preserving last modified times by default."
  [from to & {:keys [preserve] :or {preserve true}}]
  (let [from-file (io/file from)
        to-file (io/file to)]
    (cond
      (and (file? from-file) (file? to-file))
        (FileUtils/copyFile from-file to-file preserve)
      (and (file? from-file) (directory? to-file))
        (FileUtils/copyFileToDirectory from-file to-file preserve)
      :default
        (FileUtils/copyDirectory from-file to-file (boolean preserve)))))

(defn mv
  "Try to rename a file, or copy and delete if on another filesystem."
  [from to]
  (let [from-file (io/file from)
        to-file (io/file to)]
    (cond
      (and (file? from-file)
           (or (file? to-file) (not (exists? to-file))))
        (FileUtils/moveFile from-file to-file)
      :default
        (FileUtils/moveToDirectory from-file to-file true))))


(defn chmod
  "Change file permissions in a portable way."
  [path & {:keys [r w x]}]
  (let [file (io/file path)]
    (do
      (if-not (nil? r) (.setReadable file r))
      (if-not (nil? w) (.setWritable file w))
      (if-not (nil? x) (.setExecutable file x)))))

(defn create-temp-file
  "Create a temporary file with prefix and suffix (extension, like .txt)"
  ([] (File/createTempFile "temp" ".tmp"))
  ([prefix suffix]  (File/createTempFile prefix suffix)))

(defn create-temp-dir
  "Create a temporary directory"
  []
  (GuavaFiles/createTempDir))

(defn cat
  [& files]
  (reduce str (map slurp files)))

(defn lines
  [& files]
  (seq (.split (apply cat files) "\n")))

(defn wc-l
  [& files]
  (count (apply lines files)))

(def HOME (System/getProperty "user.home"))
