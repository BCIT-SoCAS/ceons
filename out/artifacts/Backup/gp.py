import subprocess
import operator
import random
import numpy

from deap import base
from deap import benchmarks
from deap import creator
from deap import tools

import warnings
warnings.filterwarnings("ignore")

def run_eon(list):
    process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', list[0], list[1], list[2], list[3], list[4], '10000', '1000', '120'], stdout=subprocess.PIPE)
    out, err = process.communicate()
    out_formatted = out.decode("utf-8")
    return(out_formatted)

list = ['90', '75', '60', '40', '20']
result = run_eon(list)
print(result)

creator.create("FitnessMin", base.Fitness, weights=(-1.0))
creator.create("Particle", list, fitness=creator.FitnessMin, speed=list, )
