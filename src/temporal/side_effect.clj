;; Copyright © Manetu, Inc.  All rights reserved

(ns temporal.side-effect
  "Methods for managing side-effects from within workflows"
  (:require [taoensso.nippy :as nippy]
            [temporal.internal.utils :refer [->Func] :as u])
  (:import [io.temporal.workflow Workflow]
           [java.time Instant]))

(defn _encode_in [arg]
  (clojure.edn/read (java.io.PushbackReader. (clojure.java.io/reader arg))))

(defn _encode_out [arg]
  (.getBytes (pr-str arg)))

(defn gen-uuid
  "A side-effect friendly random UUID generator"
  []
  (str (Workflow/randomUUID)))

(defn invoke
  "Invokes 'f' via a Temporal [SideEffect](https://docs.temporal.io/concepts/what-is-a-side-effect/)"
  [f]
  (_encode_in
   (Workflow/sideEffect u/bytes-type
                        (->Func (fn [] (_encode_out (f)))))))

(defn now
  "Returns the java.time.Instant as a SideEffect"
  []
  (invoke #(Instant/now)))
