(ns pub-app.data
  (:import [goog.ui IdGenerator]))

(def alphabet "abcdefghijklmnopqrstuvwxyz")

(defn guid []
  (.getNextUniqueId (.getInstance IdGenerator)))

(defn gen-word []
  (let [len (+ 5 (rand-int 6))]
    (apply str (repeatedly len #(rand-nth alphabet)))))

(defn gen-status [] (rand-nth ["inactive" "live" "none" "prospecting"]))

(defn gen-size [] (rand-nth ["large" "medium" "small"]))

(defn static-data []
  (vec (let [rows (range 69)]
    (for [row rows]
      {:site (str (gen-word) ".com") :status (gen-status)
       :size (gen-size) :owner (gen-word) :guid (guid)}))))
