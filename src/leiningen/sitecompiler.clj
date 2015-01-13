(ns leiningen.sitecompiler
  (:require [leiningen.core.main :as l]
            [clojure.java.io :as io]
            [cemerick.pomegranate :as p])
  (:import [org.apache.commons.compress.archivers.zip ZipFile]
           [java.io File]))

(def SITECOMPILER-VERSION "0.2.6")

;; Из-за того, что я не смог получить список файлов-ресурсов, пришлось сделать для них
;; дескриптор metainfo.clj. Там, помимо названия файла-шаблона, храним еще и
;; небольшое описание к нему
(defn map-templates []
  (eval (read-string (slurp (io/resource "templates/metainfo.clj")))))

;; Распаковываем архив, используем библиотеку apache.commons.compress
(defn decompress-file [dir infile encoding]
  (with-open [zip (ZipFile. (io/file infile) encoding)]
    (doseq [entry (enumeration-seq (.getEntries zip))]
      (let [local-file (io/file (str dir File/separator (.getName entry)))]
        (io/make-parents local-file)
        (if-not (.isDirectory entry)
          (io/copy (.getInputStream zip entry)
                   local-file))))))

(defn create-project [site-name & [template-name]]
  (let [templ (str (or template-name "blank") ".zip")
        r (.openStream (io/resource (str "templates/" templ))) ;; Открываем архив с шаблоном
        zip-name (str site-name File/separator "templ.zip")]   ;; Куда будем копировать архив шаблона
    (.mkdir (java.io.File. site-name))                         ;; Создаем дирректорию проекта
    (io/copy r (io/file zip-name))                             ;; Копируем туда архив шаблона
    (let [z (ZipFile. zip-name)]
      (decompress-file site-name zip-name "UTF-8"))            ;; Распаковываем архив шаблона
    (io/delete-file zip-name)))                                ;; Удаляем ненужный более templ.zip

(defn compile-project []
  ;; Здесь мы можем только через eval, т.к. при компиляции проекта
  ;; компилятор не находит неймспейс sitecompiler.core
  (eval '(do (require 'sitecompiler.core)
             (sitecompiler.core/-main "config.clj"))))

;; Показать доступные шаблоны
(defn show-templates []
  (let [m (map-templates)]
    (dorun (map #(l/info " *" % "--" (get m %)) (keys m)))))

(defn show-help []
  (l/info "Static website compiler version " SITECOMPILER-VERSION)
  (l/info "Instead of 'sitecompiler' you may use the command 'sc'.")
  (l/info "lein sitecompiler new <site-name> <template-name> -- create site stub")
  (l/info "lein sitecompiler templates -- list available templates")
  (l/info "lein sitecompiler compile -- compile the site")
  (l/info "lein sitecompiler help -- show this help"))

(defn ^:no-project-needed sitecompiler
  "Static website compiler has following commands: templates, new, compile, help. Short version of command is 'sc'."
  [project & args]

  ;; При помощи pomegranate загружаем нужную версию sitecompiler-а
  (p/add-dependencies :coordinates [['sitecompiler SITECOMPILER-VERSION]]
                      :repositories (merge cemerick.pomegranate.aether/maven-central
                                           {"clojars" "http://clojars.org/repo"}))

  (let [nm (if (> (count args) 1) (nth args 1) "default")
        templ (if (> (count args) 2) (nth args 2) "blank")]
    (case (first args)
      "new" (create-project nm templ)
      "compile" (compile-project)
      "templates" (show-templates)
      "help" (show-help)
      (show-help))))
