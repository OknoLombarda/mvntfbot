(ns movie-notifier.core (:require [telegrambot-lib.core :as tbot]
                                  [overtone.at-at :as atat]
                                  [clj-http.client :as client]
                                  [clojure.edn :as edn]))

(def config (edn/read-string (slurp "config.edn")))

(defn req-reservation []
  (client/get "https://afisha.api.kinopark.kz/api/seance/0b447a1f-917e-4b9c-aabb-f4e90513b86c/availability"
              {:headers {:content-type "application/json"
                         :Authorization (:Authorization config)}
               :throw-exceptions false}))

(def bot (tbot/create (:bot-token config)))
(defn check-availability []
  (if (not= 406 (:status (req-reservation)))
    (tbot/send-message bot (:chat_id config) "Reservation is available")))

(def pool (atat/mk-pool :cpu-count 1))
(atat/every 60000 check-availability pool)
