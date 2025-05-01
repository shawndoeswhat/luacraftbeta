import os
import subprocess
from colorama import Fore, Style, init

init(autoreset=True)

def run_command(command, cwd=None):
    print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} Running: {command}")
    try:
        subprocess.run(command, shell=True, check=True, cwd=cwd)
    except subprocess.CalledProcessError as e:
        print(f"{Style.BRIGHT}{Fore.RED}[✘]{Style.RESET_ALL} Command failed with exit code {e.returncode}")
        exit(e.returncode)

def ensure_dir(path):
    if not os.path.exists(path):
        print(f"{Style.BRIGHT}{Fore.YELLOW}[!] Creating missing directory: {path}")
        os.makedirs(path)

ensure_dir('./target')
run_command('mvn package')

artifact_name = 'LuaCraftBeta-0.1.7-SNAPSHOT.jar'
jar_path = os.path.join('target', artifact_name)

if os.path.isfile(jar_path):
    print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} Build succeeded: {Fore.YELLOW}{jar_path}")
else:
    print(f"{Style.BRIGHT}{Fore.RED}[✘]{Style.RESET_ALL} Build failed: {artifact_name} not found in target/")
    exit(1)
