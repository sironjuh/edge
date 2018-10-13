(ns edge.yada.ig
  (:require
    [edge.system.meta :refer [useful-info]]
    [yada.yada :as yada]
    [integrant.core :as ig]))

(defmethod ig/init-key ::listener
  [_ opts]
  (assoc (apply yada/listener opts)
         ::handler (first opts)))

(defmethod ig/halt-key! ::listener
  [_ {:keys [close]}]
  (when close (close)))

;; Use getName to avoid requiring a direct dependency on bidi, etc.
(defmulti ^:private hosts
  (fn [config state]
    (some-> (::handler state) type (.getName))))

(defmethod hosts "bidi.vhosts.VHostsModel"
  [config state]
  (let [vhosts (mapcat first (:vhosts (::handler state)))]
    (map #(str (name (:scheme %)) "://" (:host %)) vhosts)))

(defmethod hosts :default
  [config state]
  ;; Not a terrible assumption
  [(str "http://localhost:" (:port state))])

(defmethod useful-info ::listener
  [_ config state]
  (str "Website listening on: "
       (apply str (interpose " " (hosts config state)))))
