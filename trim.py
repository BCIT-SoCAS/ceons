import random
import csv
import math

class_a = []
class_b = []
class_c = []
class_d = []
class_e = []

with open("results.csv", "r") as infile:
    next(infile)
    for line in infile:
        l = line.split(',')
        if (l[8] == 'A'):
            class_a.append(line)
        elif (l[8] == 'B'):
            class_b.append(line)
        elif (l[8] == 'C'):
            class_c.append(line)
        elif (l[8] == 'D'):
            class_d.append(line)
        elif (l[8] == 'E'):
            class_e.append(line)

lengths = [len(class_a), len(class_b), len(class_c), len(class_d), len(class_e)]
print(lengths)
smallest = math.inf
smallest_index = 5 # Will be out of bounds

for index, length in enumerate(lengths):
    if (length < smallest):
        smallest = length
        smallest_index = index

output = [class_a[:smallest], class_b[:smallest], class_c[:smallest], class_d[:smallest], class_e[:smallest]]

f = open("results-trimmed.csv", "w+")
f.write('seed,demands_count,erlang,usage_1,usage_2,usage_3,usage_4,usage_5,usage_class,blocked_all\n')

for line in output:
    for l in line:
        f.write(l)

f.close()
