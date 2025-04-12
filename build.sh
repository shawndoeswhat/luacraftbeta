#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'

mkdir -p ./jars

echo -e "${BOLD}${GREEN}[✔]${NC} Downloading project-poseidon-1.1.10.jar..."
curl -L -o ./jars/project-poseidon-1.1.10.jar https://github.com/retromcorg/Project-Poseidon/releases/download/1.1.10-250328-1731-f67a8e3/project-poseidon-1.1.10.jar

echo -e "${BOLD}${GREEN}[✔]${NC} Downloading luaj-jse-3.0.2.jar..."
curl -L -o ./jars/luaj-jse-3.0.2.jar https://github.com/luaj/luaj/releases/download/v3.0.2/luaj-jse-3.0.2.jar

echo -e "${BOLD}${GREEN}[✔]${NC} Installing project-poseidon-1.1.10.jar to local Maven repository..."
mvn install:install-file -Dfile=./jars/project-poseidon-1.1.10.jar -DgroupId=com.legacyminecraft.poseidon -DartifactId=poseidon-craftbukkit -Dversion=1.1.10 -Dpackaging=jar

echo -e "${BOLD}${GREEN}[✔]${NC} Installing luaj-jse-3.0.2.jar to local Maven repository..."
mvn install:install-file -Dfile=./jars/luaj-jse-3.0.2.jar -DgroupId=org.luaj -DartifactId=luaj-jse -Dversion=3.0.2 -Dpackaging=jar

echo -e "${BOLD}${GREEN}[✔]${NC} Running Maven clean and eclipse:eclipse..."
mvn clean eclipse:eclipse

echo -e "${BOLD}${GREEN}[✔]${NC} Build process complete!"
