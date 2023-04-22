import csv
import matplotlib.pyplot as plt
import numpy as np

def key_value_dictionary(file):
    key = []
    value = []
    with open(file, "r") as f:
        for line in f:
            l = line.split(" ")
            sub_index = l[0].index("(")-1
            key_str = l[0][1:sub_index]
            key.append(key_str)
            value_split = l[7].strip()
            value_s = float(value_split)
            value.append(value_s)
    list_zip = zip(key, value)
    key_value = list(list_zip)
    d = {}
    for key, value in key_value:
        d[key] = d.get(key, []) + [value]
    for key in d:
        if len(d[key]) > 1:
            sum_value = sum(d[key])
            value_average = round(sum_value/len(d[key]), 3)
            d[key] = value_average
    for key in d:
        if isinstance(d[key], list):
            value_converted = float(d[key][0])
            d[key] = value_converted
    return d
            
recall = key_value_dictionary("recall.txt")
precision = key_value_dictionary("precision.txt")

shared_recall = {k: recall[k] for k in recall if k in precision}
shared_precision = {p: precision[p] for p in precision if p in recall}

sorted_recall = dict(sorted(shared_recall.items(), key = lambda kv: kv[1], reverse = True))
firstNpairs_recall = {k: sorted_recall[k] for k in list(sorted_recall)[:6]}
#print(firstNpairs_recall)
pairs_precision = {j: shared_precision[j] for j in shared_precision if j in firstNpairs_recall}
print(pairs_precision)

labels = []
recall_values = []
precision_values = []
for key in firstNpairs_recall:
    labels.append(key)
print(labels)

for key in firstNpairs_recall:
    recall_values.append(firstNpairs_recall[key])
#print(recall_values)
        
for key in firstNpairs_recall:
    precision_values.append(pairs_precision[key])
print(precision_values)

x = np.arange(len(labels))
width = 0.35

fig, ax = plt.subplots()
rects1 = ax.bar(x - width/2, recall_values, width, label = "recall")
rects2 = ax.bar(x + width/2, precision_values, width, label = "precision")

ax.set_ylabel("scores")
ax.set_title("hypernym as new relation")
ax.set_xticks(x, labels)
ax.legend()

ax.bar_label(rects1, padding=3)
ax.bar_label(rects2, padding=3)

fig.tight_layout()

plt.setp(ax.get_xticklabels(), rotation=12, horizontalalignment="right")
plt.show()

