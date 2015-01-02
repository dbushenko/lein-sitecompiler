(ns leiningen.sc
  (:require [leiningen.sitecompiler]))

(defn ^:no-project-needed sc
  [project & [a1 a2 a3]]
  (leiningen.sitecompiler/sitecompiler project a1 a2 a3))

