(ns {{name}}.core
  (:require 
   [clojure.string :as string]
   [cljs.core.async :refer [chan <! >! put!]]
   [cljs.reader :as reader]
   [domina :as dom]
   [domina.css :as css]
   [domina.events :as events]
   [singult.core :as sing]
   [{{name}}.connect :as connect])
  (:require-macros 
   [cljs.core.async.macros :refer [go]]))

(def send (chan))
(def receive (chan))

(def ws-url "ws://localhost:19991/async")
(def ws (new js/WebSocket ws-url))

(defn log
  [e]
  (.log js/console e))

(defn event-chan
  [c id el type data]
  (let [writer #(put! c [id % data])]
    (events/listen! el type writer)
    {:chan c
     :unsubscribe #(.removeEventListener el type writer)}))

(defn key-code
  [event]
  (.-keyCode (events/raw-event event)))

(defn input-value
  [input]
  (-> input css/sel dom/single-node dom/value))

(defn tag
  [type id class]
  (keyword (str (name type) "#" id "." class)))

(defn element-width
  [el]
  (.-width (js/goog.style.getSize (dom/single-node el))))

(defn element-height
  [el]
  (.-height (js/goog.style.getSize (dom/single-node el))))

(defn viewport-to-bottom
  [el]
  (let [inner (dom/single-node el) 
        viewport (.-innerHeight js/window)
        page-top (js/goog.style.getPageOffsetTop inner)
        height (.-height (js/goog.style.getSize inner))]
    (- (+ page-top height) viewport)))

(defn scroll-to-bottom
  [el]
  (let [viewport-top (viewport-to-bottom el)]
    (js/window.scrollTo 0 viewport-top)))

(defn init
  [data])

(defn dispatch-message
  []
  (go
   (while true
     (let [msg (<! receive)
           raw (.-data msg)
           data (reader/read-string raw)]
       (condp = (:op data)
         :init (init data)
         (log (str "op not supported! " data)))))))

(defn make-sender
  []
  (log "HELLO")
  (event-chan send :click js/document.body :click {})
  (go
   (while true
     (let [[id event data] (<! send)]
       (condp = id
         :click (log "click!"))))))

(defn make-receiver []
  (set! 
   (.-onmessage ws)
   (fn [msg]
     (put! receive msg)))
  (set!
   (.-onopen ws)
   (fn [msg] 
     (.send ws {:op :init})))
  (dispatch-message))

(defn init!
  []
  (make-sender)
  (make-receiver))

(def on-load
  (set! (.-onload js/window) init!))

(connect/connect)

