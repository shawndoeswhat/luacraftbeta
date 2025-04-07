package com.shawnjb.luacraftbeta.lua.api;

import java.util.Arrays;

import org.bukkit.util.Vector;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

public class LuaVector3 {
    private final Vector vector;

    public LuaVector3(Vector vector) {
        this.vector = vector;
    }

    public LuaVector3(double x, double y, double z) {
        this.vector = new Vector(x, y, z);
    }

    public Vector getHandle() {
        return vector;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("set", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() >= 3) {
                    vector.setX(args.arg(1).todouble());
                    vector.setY(args.arg(2).todouble());
                    vector.setZ(args.arg(3).todouble());
                    return LuaValue.NIL;
                }
                return LuaValue.error("Usage: set(x, y, z)");
            }
        });

        t.set("clone", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return new LuaVector3(vector.clone()).toLuaTable();
            }
        });

        t.set("length", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.length());
            }
        });

        t.set("normalize", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (vector.lengthSquared() == 0) {
                    return LuaValue.error("Cannot normalize a zero-length vector");
                }
                vector.normalize();
                return toLuaTable();
            }
        });

        LuaTable meta = new LuaTable();

        meta.set("__index", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue table, LuaValue key) {
                String k = key.checkjstring().toLowerCase();
                switch (k) {
                    case "x":
                        return LuaValue.valueOf(vector.getX());
                    case "y":
                        return LuaValue.valueOf(vector.getY());
                    case "z":
                        return LuaValue.valueOf(vector.getZ());
                    default:
                        return table.rawget(key); // fallback to functions like 'length'
                }
            }
        });

        meta.set("__newindex", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue table, LuaValue key, LuaValue value) {
                String k = key.checkjstring().toLowerCase();
                switch (k) {
                    case "x":
                        vector.setX(value.todouble());
                        break;
                    case "y":
                        vector.setY(value.todouble());
                        break;
                    case "z":
                        vector.setZ(value.todouble());
                        break;
                    default:
                        table.rawset(key, value);
                }
                return LuaValue.NIL;
            }
        });

        meta.set("__tostring", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf("(" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ")");
            }
        });

        meta.set("__add", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue a, LuaValue b) {
                if (!b.istable()) {
                    return LuaValue.error("Right operand must be a Vector3");
                }
                Vector v1 = vector.clone();
                Vector v2 = fromTable(b.checktable());
                v1.add(v2);
                return new LuaVector3(v1).toLuaTable();
            }
        });

        t.setmetatable(meta);
        return t;
    }

    public static Vector fromTable(LuaTable table) {
        if (!table.get("x").isnumber() || !table.get("y").isnumber() || !table.get("z").isnumber()) {
            throw new LuaError("Expected Vector3 table with numeric fields 'x', 'y', and 'z'");
        }
        double x = table.get("x").todouble();
        double y = table.get("y").todouble();
        double z = table.get("z").todouble();
        return new Vector(x, y, z);
    }

    public static LuaTable fromCoords(double x, double y, double z) {
        return new LuaVector3(x, y, z).toLuaTable();
    }

    public static void registerDocs() {
        LuaDocRegistry.addGlobalClass("Vector3");

        LuaDocRegistry.addField("Vector3", "x", "number", "The X coordinate");
        LuaDocRegistry.addField("Vector3", "y", "number", "The Y coordinate");
        LuaDocRegistry.addField("Vector3", "z", "number", "The Z coordinate");

        LuaDocRegistry.addFunction(
                "Vector3",
                "set",
                "Sets the vector's coordinates.",
                Arrays.asList(
                        new LuaDocRegistry.Param("self", "Vector3"),
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number")),
                null, true);

        LuaDocRegistry.addFunction(
                "Vector3",
                "clone",
                "Returns a copy of this vector.",
                Arrays.asList(new LuaDocRegistry.Param("self", "Vector3")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Cloned vector")), true);

        LuaDocRegistry.addFunction(
                "Vector3",
                "length",
                "Returns the length (magnitude) of the vector.",
                Arrays.asList(new LuaDocRegistry.Param("self", "Vector3")),
                Arrays.asList(new LuaDocRegistry.Return("number", "Length of the vector")), true);

        LuaDocRegistry.addFunction(
                "Vector3",
                "normalize",
                "Normalizes this vector to a unit vector.",
                Arrays.asList(new LuaDocRegistry.Param("self", "Vector3")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Normalized vector")), true);
    }
}
