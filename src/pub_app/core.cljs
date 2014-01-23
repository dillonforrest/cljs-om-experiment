(ns pub-app.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [pub-app.utils :refer [cx get-state new-sorting
                                   sort-arrow filter-sort]]
            [pub-app.data :refer [static-data]]))

(enable-console-print!)

;; ==================================================
;; init app state
;; ==================================================

(def publishers (static-data))

(def app-state (atom {:curr-page   0
                      :page-size   25
                      :filter      ""
                      :sorter      nil
                      :sort-order  nil}))

;; ==================================================
;; event handlers
;; ==================================================

(defn change-page-size [e state owner]
  (let [new-page-size (int (.. e -target -value))]
    (om/update! state assoc :page-size new-page-size :curr-page 0)))

(defn previous-page [e state owner]
  (let [curr-page (get-state :curr-page state)
        new-page  (if (zero? curr-page) 0 (- curr-page 1))]
    (om/update! state assoc :curr-page new-page)))

(defn next-page [e state owner displayed]
  (let [curr-page (om/read state #(:curr-page %))
        page-size (get-state :page-size state)
        max-page  (quot (count displayed) page-size)
        new-page  (min max-page (+ curr-page 1))]
    (om/update! state assoc :curr-page new-page)))

(defn update-filter [e state owner]
  (om/update! state assoc :filter (string/trim (.. e -target -value))
                          :curr-page 0))

(defn update-sort [sorter e state owner]
  (let [new-sort-order (new-sorting state sorter)]
    (om/update! state assoc :sorter sorter :sort-order new-sort-order)))

;; ==================================================
;; components
;; ==================================================

(defn filter-and-create [state owner _]
  (om/component
    (dom/div nil
      (dom/input #js {:type "text", :id "search-box",
                      :onChange (om/bind update-filter state owner)
                      :placeholder "type to filter"})
      (dom/button #js {:id "create-new", :className (cx :click)} "Create New"))))

(defn row [state _ _]
  (om/component
    (dom/tr #js {:className (if (even? (:index state)) "even" "odd")}
      (dom/td nil
        (dom/div #js {:className (cx :site :cell)} (:site state)))
      (dom/td nil
        (dom/div #js {:className (cx :status :cell)} (:status state)))
      (dom/td nil
        (dom/div #js {:className (cx :size :cell)} (:size state)))
      (dom/td nil
        (dom/div #js {:className (cx :owner :cell)} (:owner state)))
      (dom/td nil
        (dom/div #js {:className (cx :edit :cell)}
          (dom/a #js {:href "#"} "edit"))))))

(defn table-header [state owner _]
  (let [sort-handler #(om/bind (partial update-sort %) state owner)
        gen-attrs    #(clj->js {:className (cx % :click)
                                :onClick (sort-handler %)})
        gen-sort-arrow  (partial sort-arrow state)]
    (om/component
      (dom/thead nil
        (dom/tr nil
          (dom/th (gen-attrs :site) "site"
            (dom/span #js {:className (cx :sort)} (gen-sort-arrow :site)))
          (dom/th (gen-attrs :status) "status"
            (dom/span #js {:className (cx :sort)} (gen-sort-arrow :status)))
          (dom/th (gen-attrs :size) "size"
            (dom/span #js {:className (cx :sort)} (gen-sort-arrow :size)))
          (dom/th (gen-attrs :owner) "owner"
            (dom/span #js {:className (cx :sort)} (gen-sort-arrow :owner)))
          (dom/th #js {:className (cx :edit)} "edit"
            (dom/span #js {:className (cx :sort)}))))) 
    )
  )

(defn table-rows [state _ _]
  (let [page-size    (:page-size state)
        lower-limit  (* (:curr-page state) page-size)
        displayed    (:displayed state)
        upper-limit  (min displayed (+ lower-limit page-size))
        drop-later   (partial take upper-limit)
        drop-earlier (partial drop lower-limit)
        add-index    (partial map #(assoc %2 :index %1) (range page-size))
        pubs-to-show (-> displayed drop-later drop-earlier add-index vec)]
    (om/component
      (dom/tbody nil
        (om/build-all row pubs-to-show {:key :guid})))))

(defn table [state _ _]
  (om/component
    (dom/table nil
      (om/build table-header state)
      (om/build table-rows state))))

(defn pager [state owner _]
  (let [go-prev (om/bind previous-page state owner)
        go-next (om/bind next-page state owner (:displayed state))]
    (om/component
      (dom/div #js {:id "pager"}
        (dom/div nil
          (dom/span #js {:onClick go-prev, :className (cx :click)} "previous")
          (dom/span nil " | ")
          (dom/span #js {:onClick go-next, :className (cx :click)} "next"))))))

(defn page-size [state owner _]
  (let [page-size   (:page-size state)
        get-attrs   #(clj->js {:value %})
        total-rows  (count (:displayed state))
        first-row   (+ (if (pos? total-rows) 1 0) (* page-size (:curr-page state)))
        last-row    (min total-rows (+ -1 first-row page-size))
        on-change   #(change-page-size % state owner)]
    (om/component
      (dom/div #js {:className "page-size"}
        (dom/span nil "Showing sites ")
        (dom/span #js {:className (cx :bold)} first-row)
        (dom/span nil " through ")
        (dom/span #js {:className (cx :bold)} last-row)
        (dom/span nil " of ")
        (dom/span #js {:className (cx :bold)} total-rows)
        (dom/span nil ", ")
        (dom/select #js {:value page-size :onChange on-change}
          (dom/option (get-attrs 10) 10)
          (dom/option (get-attrs 25) 25)
          (dom/option (get-attrs 100) 100))
        (dom/span nil " sites per page")))))

(defn main-app [state owner] 
  (om/component
    (let [updated-state (assoc state :displayed (filter-sort publishers state))]
      (dom/div nil
        (dom/h3 nil "ClojureScript Publisher Lead List")
        (om/build filter-and-create updated-state)
        (om/build table updated-state)
        (dom/br nil)
        (om/build pager updated-state)
        (dom/br nil)
        (om/build page-size updated-state)))))

;; ==================================================
;; mount app -- LEGGO
;; ==================================================

(om/root app-state main-app (.getElementById js/document "app"))
