import sys
import subprocess
import operator
# --------------- Open File ---------------
f = open("output.csv", "w+")

# --------------- Run Simulations ---------------

# ********************* euro28.eon *********************
# ^^^^^^^^^^^^^^^^^^^^^ OLD ^^^^^^^^^^^^^^^^^^^^^
# @@@@@@@@@@@@@@@@@@@@@ 1000 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 900 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 800 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 700 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 600 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 500 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 400 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 300 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,554,'+output+'\n')

# ^^^^^^^^^^^^^^^^^^^^^ NEW ^^^^^^^^^^^^^^^^^^^^^
# @@@@@@@@@@@@@@@@@@@@@ 1000 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 900 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 800 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 700 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 600 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 500 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 400 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 300 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '0', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '25', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '3386', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '554', 'euro28.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,554,'+output+'\n')


# ********************* us26.eon *********************
# ^^^^^^^^^^^^^^^^^^^^^ OLD ^^^^^^^^^^^^^^^^^^^^^
# @@@@@@@@@@@@@@@@@@@@@ 1000 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '1000', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 900 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '900', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 800 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '800', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 700 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '700', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 600 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '600', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 500 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '500', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 400 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '400', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 300 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '250000', '300', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,554,'+output+'\n')

# ^^^^^^^^^^^^^^^^^^^^^ NEW ^^^^^^^^^^^^^^^^^^^^^
# @@@@@@@@@@@@@@@@@@@@@ 1000 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '1000', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,1000,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 900 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '900', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,900,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 800 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '800', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,800,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 700 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '700', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,700,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 600 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '600', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,600,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 500 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '500', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,500,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 400 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '400', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,400,554,'+output+'\n')

# @@@@@@@@@@@@@@@@@@@@@ 300 @@@@@@@@@@@@@@@@@@@@@
process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '0', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,0,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '25', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,25,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '3386', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,3386,'+output+'\n')

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '70', '60', '45', '25', '6', '250000', '300', '554', 'us26.eon'], stdout=subprocess.PIPE)
out, err = process.communicate()
output = out.decode("utf-8").rstrip()
f.write('250000,300,554,'+output+'\n')

# --------------- Close File & Exit ---------------
f.close()
sys.exit()
