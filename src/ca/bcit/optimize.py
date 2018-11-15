import os.path, subprocess
from subprocess import STDOUT, PIPE

def compile_java(java_file):
    subprocess.check_call(['javac', java_file])

def execute_java(java_file, stdin):
    java_class, ext = os.path.splitext(java_file)
    cmd = ['java', java_class]
    proc = subprocess.Popen(cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    stdout, stderr = proc.communicate(stdin)
    print('This was "' + stdout + '""')

os.chdir(os.getcwd())
compile_java('./ca/bcit/Main.java')
execute_java('./ca/bcit/Main.java', 'run')
