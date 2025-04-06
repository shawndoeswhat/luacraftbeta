package com.shawnjb.luacraftbeta.docs;

import java.util.*;

/**
 * Stores Lua function and class documentation metadata for LDoc / LuaCATS
 * output.
 */
public class LuaDocRegistry {

    public static class Param {
        public final String name;
        public final String type;

        public Param(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class Return {
        public final String type;
        public final String description;

        public Return(String type, String description) {
            this.type = type;
            this.description = description;
        }
    }

    public static class FunctionDoc {
        public final String name;
        public final String description;
        public final List<Param> params;
        public final List<Return> returns;

        public FunctionDoc(String name, String description, List<Param> params, List<Return> returns) {
            this.name = name;
            this.description = description;
            this.params = params != null ? params : new ArrayList<>();
            this.returns = returns != null ? returns : new ArrayList<>();
        }
    }

    private static final Map<String, List<FunctionDoc>> functions = new LinkedHashMap<>();

    private static final Set<String> classes = new LinkedHashSet<>();

    public static void addFunction(String category, String name, String description, List<Param> params,
            List<Return> returns) {
        functions.computeIfAbsent(category, k -> new ArrayList<>())
                .add(new FunctionDoc(name, description, params, returns));
    }

    public static void addClass(String className) {
        classes.add(className);
    }

    public static Map<String, List<FunctionDoc>> getAllFunctions() {
        return functions;
    }

    public static Set<String> getAllClasses() {
        return classes;
    }

    public static void debugPrint() {
        System.out.println("[LuaDocRegistry] Classes: " + classes);
        for (Map.Entry<String, List<FunctionDoc>> entry : functions.entrySet()) {
            System.out.println("[LuaDocRegistry] Category: " + entry.getKey());
            for (FunctionDoc doc : entry.getValue()) {
                System.out.println("  Function: " + doc.name);
            }
        }
    }
}
