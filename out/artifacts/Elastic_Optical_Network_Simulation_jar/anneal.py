import subprocess
import operator
import random
import math

CURRENT_BEST_LIST = ''
CURRENT_BEST_COST = 100.0

def cost(list):
    process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', list[0], list[1], list[2], list[3], list[4], '10000', '1100', '0'], stdout=subprocess.PIPE)
    out, err = process.communicate()
    output = out.decode("utf-8")
    cost = float(output.split(",")[5])
    global CURRENT_BEST_COST
    global CURRENT_BEST_LIST
    if (cost < CURRENT_BEST_COST):
        CURRENT_BEST_COST = cost
        CURRENT_BEST_LIST = output
    return cost, output

def neighbor(list):
    usage_1 = int(list[0]) + random.randint(-10,10)
    while (usage_1 >= 100):
        usage_1 = usage_1 + random.randint(-5,5)
    usage_2 = int(list[1]) + random.randint(-5,5)
    while (usage_2 >= usage_1):
        usage_2 = usage_2 + random.randint(-5,5)
    usage_3 = int(list[2]) + random.randint(-5,5)
    while (usage_3 >= usage_2):
        usage_3 = usage_3 + random.randint(-5,5)
    usage_4 = int(list[3]) + random.randint(-5,5)
    while (usage_4 >= usage_3):
        usage_4 = usage_4 + random.randint(-5,5)
    usage_5 = int(list[4]) + random.randint(-5,5)
    while ((usage_5 >= usage_4) or (usage_5 <= 0)):
        usage_5 = usage_5 + random.randint(-10,10)
    random_neighbor = [str(usage_1), str(usage_2), str(usage_3), str(usage_4), str(usage_5)]
    return(random_neighbor)

def acceptance_probability(old_cost, new_cost, temp):
    ap = math.exp((new_cost - old_cost) / temp)
    return ap

def anneal(solution):
    global CURRENT_BEST_COST
    global CURRENT_BEST_LIST
    f = open("results.txt", "w+")

    old_cost, x = cost(solution)
    temp = 1.0
    temp_min = 0.00001
    alpha = 0.9
    while temp > temp_min:
        i = 1
        while i <= 20:
            print('Current Best List: {0}'.format(CURRENT_BEST_LIST),end='')
            f.write('Current Best List: {0}'.format(CURRENT_BEST_LIST))
            print('Current Best Cost: '+str(CURRENT_BEST_COST))
            f.write('Current Best Cost: '+str(CURRENT_BEST_COST)+'\n')
            new_solution = neighbor(solution)
            print_list = ','.join(new_solution)
            print('---- New Neighbor: {0}'.format(print_list))
            f.write('---- New Neighbor: {0}\n'.format(print_list))
            new_cost, l = cost(new_solution)
            print('List: {0}'.format(l),end='')
            f.write('List: {0}'.format(l))
            ap = acceptance_probability(old_cost, new_cost, temp)
            print('AP: ' + str(ap))
            f.write('AP: ' + str(ap)+'\n')
            if ap > random.uniform(0.0, 1.0):
                solution = new_solution
                old_cost = new_cost
            i += 1
        temp = temp*alpha

        print('******** Temperature: '+str(temp)+'\n')
        f.write('******** Temperature: '+str(temp)+'\n')

    f.close()
    return solution, cost

initial_solution = ['90', '75', '60', '40', '20']
result, cost = anneal(initial_solution)
print(result)
print(cost)
