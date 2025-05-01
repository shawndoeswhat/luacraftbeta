# LuaCraftBeta

## Requirements

- JDK 8
- Maven

## Installation

1. Clone the repository.

2. Install the dependencies.

    ```bash
    python3 deps.py
    ```

3. Build the project.

    ```bash
    python3 build.py
    ```

4. Place the output `target/LuaCraftBeta-{version}.jar` into your Poseidon server's `plugins` directory, or run the `install.py` script if you're on Debian or similar to install it to any Bukkit server found in `/opt`.

    ```bash
    # installs to first bukkit server in /opt
    python3 install.py
    ```

## Usage

Events are managed through scripts, which are executed when associated events occur. This can be useful for creating custom systems in which you do not want to compile a plugin for or do not have the knowledge or time to use Java.