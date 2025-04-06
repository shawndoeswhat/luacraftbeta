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

        t.set("getX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getX());
            }
        });
        t.set("getY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getY());
            }
        });
        t.set("getZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(vector.getZ());
            }
        });

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

        t.set("add", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue other) {
                if (other.istable()) {
                    Vector v = fromTable(other.checktable());
                    vector.add(v);
                    return toLuaTable(); // return updated self
                }
                return LuaValue.error("Usage: add({x=, y=, z=})");
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
                vector.normalize();
                return toLuaTable();
            }
        });

        t.set("toString", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf("(" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ")");
            }
        });

        return t;
    }

    public static Vector fromTable(LuaTable table) {
        double x = table.get("x").todouble();
        double y = table.get("y").todouble();
        double z = table.get("z").todouble();
        return new Vector(x, y, z);
    }

    public static LuaTable fromCoords(double x, double y, double z) {
        return new LuaVector3(x, y, z).toLuaTable();
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("Vector3");

        LuaDocRegistry.addFunction(
                "Vector3",
                "getX",
                "Returns the X coordinate of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "X coordinate")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "getY",
                "Returns the Y coordinate of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Y coordinate")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "getZ",
                "Returns the Z coordinate of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Z coordinate")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "set",
                "Sets the vector's coordinates.",
                Arrays.asList(
                        new LuaDocRegistry.Param("x", "number"),
                        new LuaDocRegistry.Param("y", "number"),
                        new LuaDocRegistry.Param("z", "number")),
                null);

        LuaDocRegistry.addFunction(
                "Vector3",
                "add",
                "Adds another vector to this one.",
                Arrays.asList(new LuaDocRegistry.Param("other", "Vector3")),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "The updated vector")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "clone",
                "Returns a copy of this vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Cloned vector")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "length",
                "Returns the length (magnitude) of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "Length of the vector")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "normalize",
                "Normalizes this vector to a unit vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("Vector3", "Normalized vector")));

        LuaDocRegistry.addFunction(
                "Vector3",
                "toString",
                "Returns a string representation of the vector.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "Formatted string of coordinates")));
    }
}
