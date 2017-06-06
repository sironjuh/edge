(ns edge.starwars
  (:require [clj-http.client :as client]
            [com.stuartsierra.component :refer :all]
            [clojure.core.async :refer [<!! alts!! chan close! sliding-buffer timeout]]))

(defrecord Starwars []
  Lifecycle
  (start [this]
    (let [control-chan (chan (sliding-buffer 1))
          in-chan (chan)]
      (assoc this
             :control-chan control-chan
             :in-chan in-chan))))
