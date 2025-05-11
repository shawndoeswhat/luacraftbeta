#!/usr/bin/env python3
import os, re, subprocess, sys, requests
from colorama import Fore, Style

def banner(msg): print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} {msg}")
def download(url, dest):
    banner(f"Downloading {os.path.basename(dest)} …")
    with requests.get(url, stream=True) as r:
        r.raise_for_status()
        with open(dest, "wb") as f:
            for chunk in r.iter_content(8192): f.write(chunk)
def mvn(cmd):
    banner(f"Running: {cmd}")
    subprocess.run(cmd, shell=True, check=True)

OWNER, REPO = "retromcorg", "Project-Poseidon"
ASSET_RE    = re.compile(r"project-poseidon-(\d+\.\d+\.\d+)\.jar$")
LUAJ_URL    = "https://github.com/luaj/luaj/releases/download/v3.0.2/luaj-jse-3.0.2.jar"
LUAJ_FILE   = "luaj-jse-3.0.2.jar"
os.makedirs("jars", exist_ok=True)

release = requests.get(
    f"https://api.github.com/repos/{OWNER}/{REPO}/releases/latest",
    timeout=20
).json()

asset = next((a for a in release["assets"] if ASSET_RE.match(a["name"])), None)
if not asset:
    sys.exit("Poseidon JAR not found; update ASSET_RE if the name changed.")

poseidon_file = asset["name"]
poseidon_ver  = ASSET_RE.match(poseidon_file).group(1)

download(asset["browser_download_url"], f"jars/{poseidon_file}")
download(LUAJ_URL,                      f"jars/{LUAJ_FILE}")

mvn(f"mvn install:install-file -Dfile=jars/{poseidon_file} "
    f"-DgroupId=com.legacyminecraft.poseidon "
    f"-DartifactId=poseidon-craftbukkit -Dversion={poseidon_ver} -Dpackaging=jar")

mvn("mvn install:install-file -Dfile=jars/luaj-jse-3.0.2.jar "
    "-DgroupId=org.luaj -DartifactId=luaj-jse -Dversion=3.0.2 -Dpackaging=jar")

mvn("mvn clean eclipse:eclipse")
banner("Dependency installation process complete!")
