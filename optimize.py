# import os,subprocess
# from subprocess import STDOUT,PIPE
#
# def compile_java(java_file):
#     subprocess.check_call(['javac', java_file])
#
# def execute_java(java_file, stdin):
#     java_class,ext = os.path.splitext(java_file)
#     cmd = ['java', '-cp', 'src/ca/bcit', java_class]
#     proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
#     stdout,stderr = proc.communicate(stdin)
#     print ('This was "' + stdout + '"')
#
# compile_java(os.path.join('src', 'ca', 'bcit', 'Main.java'))
# execute_java('Main.java', 'run')

import random
f = open("ranges.txt", "w+")

for i in range(1000):
    usage1 = 0
    usage2 = 0
    usage3 = 0
    usage4 = 0
    usage5 = 0
    usage5 = random.randint(1,20)
    usage4 = random.randint((usage5+1), 40)
    usage3 = random.randint((usage4+1), 60)
    usage2 = random.randint((usage3+1), 80)
    usage1 = random.randint((usage2+1), 99)
    f.write('{0},{1},{2},{3},{4}\n'.format(usage1, usage2, usage3, usage4, usage5))

f.close()
