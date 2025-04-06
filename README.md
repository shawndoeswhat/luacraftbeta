# LuaCraftBeta

**LuaCraftBeta** is a Lua scripting plugin for Minecraft Beta 1.7.3 (CB1060), powered by LuaJ. It allows you to run Lua scripts in your Minecraft world, providing access to powerful scripting features like player interaction, world manipulation, explosions, item management, and more.

Made with care by [shawnjb](https://github.com/shawnjb)

---

## Features

- LuaJ-powered scripting engine.
- Full APIs for interacting with **Player**, **World**, **Entity**, **Block**, **Material**, and more.
- Supports in-game commands and interactions.
- Generates LuaCATS-compatible documentation (`docs.lua`) for easier development.
- Scripting hooks for commands such as **summon**, **teleport**, **createExplosion**, **setBlock**, and more.
- Handles player and entity interactions like giving items, teleportation, and more.

---

## Installation

To build **LuaCraftBeta**, you need to follow these steps:

### 1. Install `luaj-jse-3.0.2.jar` to Your Local Maven Repository

Before building the project, you need to install the `luaj-jse-3.0.2.jar` file into your local Maven repository.

- Download the `luaj-jse-3.0.2.jar` from the [official release page](https://github.com/luaj/luaj/releases/download/v3.0.2/luaj-jse-3.0.2.jar).
- Once downloaded, use the following Maven command to install it into your local repository:

```bash
mvn install:install-file -Dfile=./jars/luaj-jse-3.0.2.jar -DgroupId=org.luaj -DartifactId=luaj-jse -Dversion=3.0.2 -Dpackaging=jar
```

This will install the `luaj-jse-3.0.2.jar` in your local Maven repository.

### 2. Download or Clone the Repository

You can download or clone the repository to your Minecraft server's plugin directory.

```bash
git clone https://github.com/shawnjb/LuaCraftBeta.git
```

### 3. Build the Plugin

Make sure you have **Maven** installed and then run the following command to build the plugin.

```bash
mvn clean package
```

### 4. Deploy the Plugin

After building, you will find the plugin `.jar` file in the `target/` directory. Move this `.jar` file to your Minecraft server's `plugins/` folder.

### 5. Start the Server

Launch your Minecraft Beta 1.7.3 server with the plugin installed.

### 6. Enjoy Lua Scripting

Once the server starts, you can execute Lua scripts using the `/loadscript [scriptName]` command.

---

## Usage

### Running Scripts:

To run a script, use the `/loadscript` command in the server. For example:

```bash
/loadscript myscript.lua
```

This will load and execute the `myscript.lua` file located in the `plugins/LuaCraftBeta/scripts/` directory.

### Documentation:

The plugin automatically generates a `docs.lua` file with LuaCATS-compatible documentation. After building, you can find it in:

```
src/main/resources/docs/docs.lua
```

This file contains detailed documentation on how to use the API, including the available classes and methods such as **Player**, **World**, **Entity**, and **Block**. The documentation will assist you in writing your Lua scripts.

### Debugging:

When running scripts, you can enable debug mode to log extra details about the script execution. This can be done by adding `debug` as a second argument to the `/loadscript` command:

```bash
/loadscript myscript.lua debug
```

---

## Commands

- **/loadscript [scriptName]**: Loads and executes the specified Lua script.
- **/loadscript [scriptName] debug**: Loads and executes the Lua script with debug logging enabled.

---

## License

MIT — use it, modify it, and share it freely.