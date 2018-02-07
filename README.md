# mem-files
[![Build Status](https://travis-ci.org/dryewo/mem-files.svg?branch=master)](https://travis-ci.org/dryewo/mem-files)
[![codecov](https://codecov.io/gh/dryewo/mem-files/branch/master/graph/badge.svg)](https://codecov.io/gh/dryewo/mem-files)
[![Clojars Project](https://img.shields.io/clojars/v/me.dryewo/mem-files.svg)](https://clojars.org/me.dryewo/mem-files)

Library for keeping up-to-date text file contents in memory.

    [me.dryewo/mem-files "0.0.0"]

This is handy when your application reads runtime configuration from local files. The library reads them
at given interval and exposes a map with their content.

## Example

```clj
(ns my.project.configs
  (:refer-clojure :exclude [get])
  (:require [mount.lite :as m]
            [mem-files.core :as mem-files]))


(m/defstate refresher
  :start (let [interval-ms 1000
               keys-files  {:client-id        "/meta/credentials/client-id"
                            :client-secret    "/meta/credentials/client-id"
                            :service-username "/meta/credentials/service-username"
                            :service-password "/meta/credentials/service-password"}]
           (mem-files/start interval-ms keys-files))
  :stop (.close @refresher))


(defn get [k]
  (let [files @@refresher]
    (assert (contains? files k) (str "File " k " not registered."))
    (clojure.core/get files k)))


(comment
  (get :service-password))
```

Initial loading is done synchronously in the caller thread, the following loadings are done in a background thread.

If you also want to parse those files, you can optionally pass the third parameter to `mem-files/start`:

```clj
(mem-files/start interval-ms keys-files yaml/parse-string)
```

The library will catch all exceptions and put `nil` if the parsing function throws.

## FAQ

**Q.** Why read them every time and not use file watchers?  
**A.** I found only one library that can watch individual files (instead of directories), and it has a bug:
 https://github.com/wkf/hawk/issues/20. Anyway, reading a bunch of small files every few seconds is not a big deal :)

**Q.** Why not pass those tokens and passwords via environment variables, as [Twelve-Factor App] says?  
**A.** In some enterprises service credentials are rotated regularly, for security reasons.
 In order to avoid restarting the app when it happens, we just read them from files. 

[Twelve-Factor App]: https://12factor.net/

## License

Copyright © 2018 Dmitrii Balakhonskii

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
