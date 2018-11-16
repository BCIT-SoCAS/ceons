import random
with open('results-trimmed.csv','r') as source:
    next(source)
    data = [ (random.random(), line) for line in source ]
data.sort()
with open('results-trimmed-randomized.csv','w') as target:
    for _, line in data:
        target.write( line )
