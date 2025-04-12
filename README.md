# LuaCraftBeta

## Requirements

- JDK 8
- Maven

## Installation

1. Clone the repository.

2. Install the dependencies.

    ```bash
    ./build.sh
    ```

3. Build the project.

    ```bash
    mvn clean package
    ```

4. Place the output `target/LuaCraftBeta-{version}.jar` into your Poseidon server's `plugins` directory.

## Usage

Events are managed through scripts, which are executed when associated events occur. This can be useful for creating custom systems in which you do not want to compile a plugin for or do not have the knowledge or time to use Java.