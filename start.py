#!/usr/bin/env python3
"""
Run the first CraftBukkit / Project Poseidon server we can find under /opt,
making sure we cd into that directory so no stray files pollute the calling
folder.  Mirrors the search logic of install.py.
"""

import os
import subprocess
import sys
from pathlib import Path
from colorama import Fore, Style, init

init(autoreset=True)

SEARCH_ROOT = '/opt'
JAVA_ARGS    = ['-Xms1G', '-Xmx2G'] # tweak heap here if desired
SERVER_JAR   = 'server.jar'

def log(msg, level='info'):
    symbols = {
        'info':  f'{Style.BRIGHT}{Fore.CYAN}[→]{Style.RESET_ALL}',
        'ok':    f'{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL}',
        'warn':  f'{Style.BRIGHT}{Fore.YELLOW}[!]{Style.RESET_ALL}',
        'err':   f'{Style.BRIGHT}{Fore.RED}[✘]{Style.RESET_ALL}',
    }
    print(f'{symbols.get(level, "")} {msg}')

def find_bukkit_server_dir(start: str) -> Path | None:
    for root, dirs, files in os.walk(start):
        if SERVER_JAR in files and 'bukkit.yml' in files:
            return Path(root)
    return None

if os.name == 'nt':
    log("This script is Unix-only.  Use WSL or run the server directly on Windows.", 'err')
    sys.exit(1)

server_dir = find_bukkit_server_dir(SEARCH_ROOT)
if not server_dir:
    log("No valid Bukkit / Poseidon server found under /opt (needs server.jar + bukkit.yml).", 'err')
    sys.exit(1)

log(f"Found server directory: {Fore.YELLOW}{server_dir}", 'ok')

start_script = None
for candidate in ('start.sh', 'start.bat'):
    path = server_dir / candidate
    if path.is_file():
        start_script = str(path)
        break

if start_script:
    cmd = [start_script] + sys.argv[1:]
    log(f"Using start script: {Fore.YELLOW}{start_script}", 'info')
else:
    cmd = ['java', *JAVA_ARGS, '-jar', SERVER_JAR, 'nogui', *sys.argv[1:]]
    log("No start script; falling back to plain java -jar server.jar nogui", 'warn')

original_cwd = Path.cwd()
os.chdir(server_dir)
log(f"Changed working directory to {Fore.YELLOW}{server_dir}", 'info')

try:
    proc = subprocess.run(cmd)
    code = proc.returncode
    level = 'ok' if code == 0 else 'warn'
    log(f"Server process exited with code {code}", level)
finally:
    os.chdir(original_cwd)
    log(f"Restored working directory to {Fore.YELLOW}{original_cwd}", 'info')

sys.exit(code)
