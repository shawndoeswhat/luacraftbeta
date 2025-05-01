import os
import shutil
import sys
from colorama import Fore, Style, init

init(autoreset=True)

JAR_NAME = 'LuaCraftBeta-0.1.6.jar'
JAR_PREFIX = 'LuaCraftBeta'
SCRIPT_DIR = os.path.abspath(os.path.dirname(__file__))
SOURCE_JAR = os.path.join(SCRIPT_DIR, 'target', JAR_NAME)
SEARCH_ROOT = '/opt'

def log_success(msg):
    print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} {msg}")

def log_warning(msg):
    print(f"{Style.BRIGHT}{Fore.YELLOW}[!] {msg}{Style.RESET_ALL}")

def log_error(msg):
    print(f"{Style.BRIGHT}{Fore.RED}[✘]{Style.RESET_ALL} {msg}")
    sys.exit(1)

def find_bukkit_server_dir(start_dir):
    for root, dirs, files in os.walk(start_dir):
        if 'server.jar' in files and 'bukkit.yml' in files:
            return root
    return None

if os.name == 'nt':
    log_error("This script cannot be run on Windows.")

if not os.path.isfile(SOURCE_JAR):
    log_error(f"Build JAR not found at: {SOURCE_JAR}")

log_success(f"Found JAR file: {Fore.YELLOW}{SOURCE_JAR}")

server_dir = find_bukkit_server_dir(SEARCH_ROOT)
if not server_dir:
    log_error("No valid Bukkit server directory found under /opt. Must contain both 'server.jar' and 'bukkit.yml'.")

log_success(f"Found Bukkit server directory: {Fore.YELLOW}{server_dir}")

plugins_dir = os.path.join(server_dir, 'plugins')
if not os.path.isdir(plugins_dir):
    log_warning(f"Creating plugins directory at: {plugins_dir}")
    os.makedirs(plugins_dir, exist_ok=True)

removed = False
for filename in os.listdir(plugins_dir):
    if filename.startswith(JAR_PREFIX) and filename.endswith('.jar'):
        old_path = os.path.join(plugins_dir, filename)
        try:
            os.remove(old_path)
            log_warning(f"Removed old plugin: {filename}")
            removed = True
        except Exception as e:
            log_error(f"Failed to remove old JAR {filename}: {e}")

if not removed:
    log_success("No previous LuaCraftBeta plugin versions found.")

dest_jar = os.path.join(plugins_dir, JAR_NAME)

try:
    shutil.move(SOURCE_JAR, dest_jar)
    log_success(f"Installed plugin to: {Fore.YELLOW}{dest_jar}")
except Exception as e:
    log_error(f"Failed to move JAR: {e}")
