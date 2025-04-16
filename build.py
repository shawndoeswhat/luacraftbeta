import os
import subprocess
import requests
from colorama import Fore, Style

os.makedirs('./jars', exist_ok=True)

def download_file(url, destination):
    print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} Downloading {os.path.basename(destination)}...")
    response = requests.get(url, stream=True)
    with open(destination, 'wb') as file:
        for chunk in response.iter_content(chunk_size=8192):
            file.write(chunk)

download_file('https://github.com/retromcorg/Project-Poseidon/releases/download/1.1.10-250328-1731-f67a8e3/project-poseidon-1.1.10.jar', './jars/project-poseidon-1.1.10.jar')
download_file('https://github.com/luaj/luaj/releases/download/v3.0.2/luaj-jse-3.0.2.jar', './jars/luaj-jse-3.0.2.jar')

def run_maven_command(command):
    print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} Running: {command}")
    subprocess.run(command, shell=True, check=True)

run_maven_command('mvn install:install-file -Dfile=./jars/project-poseidon-1.1.10.jar -DgroupId=com.legacyminecraft.poseidon -DartifactId=poseidon-craftbukkit -Dversion=1.1.10 -Dpackaging=jar')
run_maven_command('mvn install:install-file -Dfile=./jars/luaj-jse-3.0.2.jar -DgroupId=org.luaj -DartifactId=luaj-jse -Dversion=3.0.2 -Dpackaging=jar')
run_maven_command('mvn clean eclipse:eclipse')

print(f"{Style.BRIGHT}{Fore.GREEN}[✔]{Style.RESET_ALL} Build process complete!")
