(ns leiningen.new.cljs-ws
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "cljs-ws"))

(defn cljs-ws
  "Builds a project set up with httpkit websockets and clojurescript."
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' cljs-ws project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render ".gitignore" data)]
             ["README.md" (render "README.md" data)]
             ["LICENSE" (render "LICENSE" data)]
             ["doc/intro.md" (render "doc/intro.md" data)]
             ["resources/public/index.html" (render "resources/public/index.html" data)]
             ["resources/public/css/{{name}}.css" (render "resources/public/css/schmetterling.css" data)]
             ["src/cljx/{{sanitized}}/.gitkeep" (render "src/cljx/schmetterling/.gitkeep" data)]
             ["src/cljs/{{sanitized}}/connect.cljs" (render "src/cljs/schmetterling/connect.cljs" data)]
             ["src/cljs/{{sanitized}}/core.cljs" (render "src/cljs/schmetterling/core.cljs" data)]
             ["src/clj/{{sanitized}}/core.clj" (render "src/clj/schmetterling/core.clj" data)]
             ["src/clj/{{sanitized}}/server.clj" (render "src/clj/schmetterling/server.clj" data)]
             ["test/{{sanitized}}/core_test.clj" (render "test/schmetterling/core_test.clj" data)]
             ["target/generated/cljs/.gitkeep" (render "target/generated/cljs/.gitkeep" data)]
             ["target/generated/clj/.gitkeep" (render "target/generated/clj/.gitkeep" data)])))
