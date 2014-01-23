(ns pub-app.utils
  (:require [om.core :as om]))

(defn cx "cljs version of React's classSet addon" [& selectors]
  (let [class-names {:site    "col-site"  :status  "col-status"
                     :size    "col-size"  :owner   "col-owner"
                     :edit    "col-edit"
                     
                     :sort    "sorter"    :cell    "table-cell"
                     :click   "clickable" :bold    "emphasis"}]
    (apply str (for [sel selectors] (str (sel class-names) " ")))))

(defn get-state [k state] (om/read state #(k %)))

(defn str-contains [target string] (not (= -1 (.indexOf string target))))

(defn pub-has-keyword [target pub]
  (not-empty (filter (partial str-contains target) (vals pub))))

(defn choose-sort-order "when sorting, find new sort order" [state]
  (case (get-state :sort-order state)
    nil    :asc
    :asc   :desc
    :desc  :asc))

(defn new-sorting [state sorter]
  (let [curr-sorter (get-state :sorter state)
        opposite #(if (not= % :asc) :asc :desc)]
    (if (not= curr-sorter sorter)
      :asc   
      (opposite (get-state :sort-order state)))))

(defn sort-arrow [state kw]
  (let [sorter (get-state :sorter state)
        sort-order (get-state :sort-order state)]
    (cond
      (not (= sorter kw)) ""
      (= sort-order :asc) "^"
      (= sort-order :desc) "v"
      :else "")))

(defn filter-sort [pubs state]
  (let [kw          (get-state :filter state)
        sort-ord    (get-state :sort-order state)
        sorter      (get-state :sorter state)
        has-kw      (partial pub-has-keyword kw)
        filtering   (partial filter has-kw)
        sorting     (if (nil? sorter) identity (partial sort-by sorter))
        descending  (if (= sort-ord :desc) reverse identity)]
    (-> pubs filtering sorting descending vec)))
