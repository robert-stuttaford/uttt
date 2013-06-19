(ns uttt.html-templates
  (:use [io.pedestal.app.templates :only [tfn dtfn tnodes]]))

(defmacro uttt-templates
  []
  ;; Extract the 'hello' template from the template file uttt.html.
  ;; The 'dtfn' function will create a dynamic template which can be
  ;; updated after it has been attached to the DOM.
  ;;
  ;; To see how this template is used, refer to
  ;;
  ;; app/src/uttt/rendering.cljs
  ;;
  ;; The last argument to 'dtfn' is a set of fields that should be
  ;; treated as static fields (may only be set once). Dynamic templates
  ;; use ids to set values so you cannot dynamically set an id.
  {:uttt-board (dtfn (tnodes "uttt.html" "board") #{:id}) 
   :uttt-square (dtfn (tnodes "uttt.html" "square") #{:id})
   :uttt-inner-square (dtfn (tnodes "uttt.html" "inner-square") #{:id})})

;; Note: this file will not be reloaded automatically when it is changed.
