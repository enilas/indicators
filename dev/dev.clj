(ns dev
  (:require [com.stuartsierra.component.repl :as component-repl]
            [indicators.core :as core]))

(component-repl/set-init
 (fn [_old-system]
   (core/indicators-system {:server {:port 3001}})))

; (component-repl/reset) to reload components