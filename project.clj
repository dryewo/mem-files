(defproject me.dryewo/mem-files "0.0.0"
  :description "Library for keeping up-to-date text file contents in memory."
  :url "http://github.com/dryewo/mem-files"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies []
  :plugins [[lein-cloverage "1.0.9"]
            [lein-shell "0.5.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[me\\\\.dryewo\\\\/mem-files \"[0-9.]*\"\\\\]/[me\\\\.dryewo\\\\/mem-files \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])