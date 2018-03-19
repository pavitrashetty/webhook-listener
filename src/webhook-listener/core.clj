(ns webhook-listener.core
  (:gen-class)
  (:require [cheshire.core :as cc]
            [clj-http.client :as client]
            [clj-http.conn-mgr :as conn-mgr]
            [clojure.tools.logging :as ctl]
            [compojure.core :refer [ANY defroutes POST]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.request :refer [body-string]]))

(defonce server nil)


(defn make-api-call
    [payload]
    (println (str "Making API call with: " payload))
    (try
      (let [payload-map (cc/parse-string payload true)
            issue-id (get-in payload-map [:data :id])
            event-type (:event_type payload-map)
            text (case event-type
                   "create_issue"
                   (str "New issue creation: <http://yoda.helpshift.mobi/admin/issue/"
                        issue-id
                        "|#"
                        issue-id
                        "> Created By: "
                        (get-in payload-map [:data :author_email])
                        " For domain: `"
                        (:domain payload-map)
                        "`")
                   (str "```" payload "```"))
            opts {:body
                  (cc/generate-string
                   {:text text
                    :username "webhook-bot"})
                  :content-type :json
                  :throw-exceptions false
                  :connection-manager (conn-mgr/make-reusable-conn-manager {:timeout 5
                                                                            :threads 4
                                                                            :default-per-route 2
                                                                            :insecure? true})}]
        (println
         (client/post
          "https://hooks.slack.com/services/<URL taken from Slack incoming webhooks>"
          opts)))
      (catch Throwable e
        (let [error-msg (.getMessage e)]
          (ctl/error e "Exception while making an API call")
          (println {:status 500
                    :body error-msg
                    :headers {}
                    :request-time 1000})))))


(defroutes handler
  (POST "/" []
        (fn [request]
          (println "=================================================")
          (println request)
          (println "=================================================")
          (let [body (cc/generate-string
                      (cc/parse-string (body-string request)) {:pretty true})]
            (make-api-call body))
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body "Hello World"}))
  (ANY "/" []
       (fn [request]
         (println "=================================================")
         (println request)
         (println "=================================================")
         {:status 405
          :headers {"Content-Type" "text/html"}
          :body "Method not allowed"})))


(defn -main
  [& args]
  (let [server (run-jetty handler {:port 7400
                                   :join? false
                                   :ssl? true
                                   :ssl-port 8443
                                   :keystore "/Users/pavitra/keystore.jks"
                                   :key-password "darthvader"})]
    (alter-var-root #'server (constantly server))))
