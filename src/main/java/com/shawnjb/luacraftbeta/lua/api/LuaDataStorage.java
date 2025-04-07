package com.shawnjb.luacraftbeta.lua.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

public class LuaDataStorage {
    private static final Gson gson = new Gson();
    private static final File baseDir = new File("plugins/LuaCraftBeta/data");

    public static LuaTable createStorageTable() {
        LuaTable t = new LuaTable();

        t.set("applySaveData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                if (!key.isstring())
                    return LuaValue.error("applySaveData expects string key");

                String fileName = key.tojstring();
                File file = new File(baseDir, fileName + ".json");
                String content;

                if (value.istable()) {
                    content = gson.toJson(toMap(value.checktable()));
                } else {
                    content = value.tojstring();
                }

                try {
                    Files.createDirectories(file.getParentFile().toPath());
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                    }
                    return LuaValue.TRUE;
                } catch (IOException e) {
                    return LuaValue.error("Failed to save: " + e.getMessage());
                }
            }
        });

        t.set("getSavedData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                if (!key.isstring())
                    return LuaValue.error("getSavedData expects string key");

                File file = new File(baseDir, key.tojstring() + ".json");
                if (!file.exists())
                    return LuaValue.NIL;

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        sb.append(line);
                    String content = sb.toString();

                    try {
                        Type type = new TypeToken<Map<String, Object>>() {
                        }.getType();
                        Map<String, Object> map = gson.fromJson(content, type);
                        return toLuaTable(map);
                    } catch (Exception e) {
                        return LuaValue.valueOf(content);
                    }

                } catch (IOException e) {
                    return LuaValue.error("Failed to read: " + e.getMessage());
                }
            }
        });

        return t;
    }

    private static Map<String, Object> toMap(LuaTable table) {
        Map<String, Object> map = new LinkedHashMap<>();
        LuaValue k = LuaValue.NIL;
        while (true) {
            Varargs n = table.next(k);
            if ((k = n.arg1()).isnil())
                break;
            LuaValue v = n.arg(2);
            if (v.istable()) {
                map.put(k.tojstring(), toMap(v.checktable()));
            } else if (v.isboolean()) {
                map.put(k.tojstring(), v.toboolean());
            } else if (v.isnumber()) {
                map.put(k.tojstring(), v.todouble());
            } else {
                map.put(k.tojstring(), v.tojstring());
            }
        }
        return map;
    }

    private static LuaTable toLuaTable(Map<?, ?> map) {
        LuaTable table = new LuaTable();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                table.set(LuaValue.valueOf(key.toString()), toLuaTable((Map<?, ?>) value));
            } else if (value instanceof Boolean) {
                table.set(LuaValue.valueOf(key.toString()), LuaValue.valueOf((Boolean) value));
            } else if (value instanceof Number) {
                table.set(LuaValue.valueOf(key.toString()), LuaValue.valueOf(((Number) value).doubleValue()));
            } else {
                table.set(LuaValue.valueOf(key.toString()), LuaValue.valueOf(value.toString()));
            }
        }
        return table;
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("storage");

        LuaDocRegistry.addFunction("storage", "applySaveData",
                "Applies and saves data to disk using a key.",
                Arrays.asList(
                        new LuaDocRegistry.Param("key", "string"),
                        new LuaDocRegistry.Param("value", "string | table")),
                Arrays.asList(new LuaDocRegistry.Return("boolean", "True if successful")));

        LuaDocRegistry.addFunction("storage", "getSavedData",
                "Retrieves previously saved data using the key. May return string or table.",
                Arrays.asList(new LuaDocRegistry.Param("key", "string")),
                Arrays.asList(new LuaDocRegistry.Return("string | table", "The saved value or nil")));
    }
}
