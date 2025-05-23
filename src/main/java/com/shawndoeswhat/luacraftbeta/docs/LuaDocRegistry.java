package com.shawndoeswhat.luacraftbeta.docs;

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
        public final boolean isMethod;

        public FunctionDoc(String name, String description, List<Param> params, List<Return> returns,
                boolean isMethod) {
            this.name = name;
            this.description = description;
            this.params = params != null ? params : new ArrayList<>();
            this.returns = returns != null ? returns : new ArrayList<>();
            this.isMethod = isMethod;
        }

        public FunctionDoc(String name, String description, List<Param> params, List<Return> returns) {
            this(name, description, params, returns, false);
        }
    }

    public static class FieldDoc {
        public final String name;
        public final String type;
        public final String description;

        public FieldDoc(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }

    private static final Map<String, List<FunctionDoc>> functions = new LinkedHashMap<>();
    private static final Map<String, List<FieldDoc>> fields = new LinkedHashMap<>();
    private static final Set<String> classes = new LinkedHashSet<>();
    private static final Set<String> globalClasses = new LinkedHashSet<>();

    public static void addFunction(String category, String name, String description, List<Param> params,
            List<Return> returns) {
        functions.computeIfAbsent(category, k -> new ArrayList<>())
                .add(new FunctionDoc(name, description, params, returns));
    }

    public static void addFunction(String category, String name, String description, List<Param> params,
            List<Return> returns, boolean isMethod) {
        functions.computeIfAbsent(category, k -> new ArrayList<>())
                .add(new FunctionDoc(name, description, params, returns, isMethod));
    }

    public static void addField(String className, String fieldName, String fieldType, String description) {
        fields.computeIfAbsent(className, k -> new ArrayList<>())
                .add(new FieldDoc(fieldName, fieldType, description));
    }

    public static void addClass(String className) {
        classes.add(className);
    }

    public static void addGlobalClass(String className) {
        addClass(className);
        globalClasses.add(className);
    }

    public static Map<String, List<FunctionDoc>> getAllFunctions() {
        return functions;
    }

    public static Map<String, List<FieldDoc>> getAllFields() {
        return fields;
    }

    public static Set<String> getAllClasses() {
        return classes;
    }

    public static Set<String> getGlobalClasses() {
        return globalClasses;
    }

    public static void debugPrint() {
        System.out.println("[LuaDocRegistry] Classes: " + classes);
        for (Map.Entry<String, List<FunctionDoc>> entry : functions.entrySet()) {
            System.out.println("[LuaDocRegistry] Category: " + entry.getKey());
            for (FunctionDoc doc : entry.getValue()) {
                System.out.println("  Function: " + doc.name);
            }
        }
        for (Map.Entry<String, List<FieldDoc>> entry : fields.entrySet()) {
            System.out.println("[LuaDocRegistry] Fields for: " + entry.getKey());
            for (FieldDoc doc : entry.getValue()) {
                System.out.println("  Field: " + doc.name + " (" + doc.type + ")");
            }
        }
        System.out.println("[LuaDocRegistry] Global Classes: " + globalClasses);
    }

    public static void registerMC_ACTIONDocs() {
        LuaDocRegistry.addGlobalClass("MC_ACTION");

        LuaDocRegistry.addField("MC_ACTION", "LEFT_CLICK_BLOCK", "string", "Represents the LEFT_CLICK_BLOCK action");
        LuaDocRegistry.addField("MC_ACTION", "RIGHT_CLICK_BLOCK", "string", "Represents the RIGHT_CLICK_BLOCK action");
        LuaDocRegistry.addField("MC_ACTION", "LEFT_CLICK_AIR", "string", "Represents the LEFT_CLICK_AIR action");
        LuaDocRegistry.addField("MC_ACTION", "RIGHT_CLICK_AIR", "string", "Represents the RIGHT_CLICK_AIR action");
        LuaDocRegistry.addField("MC_ACTION", "PHYSICAL", "string", "Represents the PHYSICAL interaction");
    }
}
