package com.shawnjb.luacraftbeta.lua.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;

import com.shawnjb.luacraftbeta.docs.LuaDocRegistry;

public class LuaItemStack {
    private final ItemStack itemStack;

    public LuaItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public LuaTable toLuaTable() {
        LuaTable t = new LuaTable();

        t.set("getType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(itemStack.getType().toString().toLowerCase());
            }
        });

        t.set("setType", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue typeName) {
                if (typeName.isstring()) {
                    Material mat = Material.getMaterial(typeName.tojstring().toUpperCase());
                    if (mat != null) {
                        itemStack.setType(mat);
                        return LuaValue.NIL;
                    }
                    return LuaValue.valueOf("Invalid material name: " + typeName.tojstring());
                }
                return LuaValue.valueOf("Usage: setType(materialName)");
            }
        });

        t.set("getAmount", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(itemStack.getAmount());
            }
        });

        t.set("setAmount", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue amount) {
                if (amount.isnumber()) {
                    itemStack.setAmount(amount.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Usage: setAmount(amount)");
            }
        });

        t.set("getDurability", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(itemStack.getDurability());
            }
        });

        t.set("setDurability", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue durability) {
                if (durability.isnumber()) {
                    itemStack.setDurability((short) durability.toint());
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Usage: setDurability(durability)");
            }
        });

        t.set("getData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                MaterialData data = itemStack.getData();
                if (data instanceof Wool) {
                    Wool wool = (Wool) data;
                    LuaTable table = new LuaTable();
                    table.set("color", wool.getColor().toString().toLowerCase());
                    return table;
                }
                return LuaValue.valueOf("No data available for this item.");
            }
        });

        t.set("setData", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue data) {
                if (data.istable()) {
                    LuaTable table = data.checktable();
                    String color = table.get("color").tojstring();
                    Material mat = itemStack.getType();

                    if (mat == Material.WOOL) {
                        DyeColor dyeColor = DyeColor.valueOf(color.toUpperCase());
                        Wool wool = (Wool) itemStack.getData();
                        wool.setColor(dyeColor);
                        itemStack.setData(wool);
                    } else {
                        return LuaValue.valueOf("This item does not support data modification.");
                    }
                    return LuaValue.NIL;
                }
                return LuaValue.valueOf("Usage: setData(data)");
            }
        });

        return t;
    }

    public static void registerDocs() {
        LuaDocRegistry.addClass("LuaItemStack");

        LuaDocRegistry.addFunction("LuaItemStack", "getType", "Gets the type of the item as a lowercase string.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("string", "The material type")));

        LuaDocRegistry.addFunction("LuaItemStack", "setType", "Sets the item type using a string name.",
                Arrays.asList(new LuaDocRegistry.Param("materialName", "string")),
                null);

        LuaDocRegistry.addFunction("LuaItemStack", "getAmount", "Returns the number of items in the stack.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The stack size")));

        LuaDocRegistry.addFunction("LuaItemStack", "setAmount", "Sets the number of items in the stack.",
                Arrays.asList(new LuaDocRegistry.Param("amount", "number")),
                null);

        LuaDocRegistry.addFunction("LuaItemStack", "getDurability", "Gets the durability value of the item.",
                Arrays.asList(),
                Arrays.asList(new LuaDocRegistry.Return("number", "The durability value")));

        LuaDocRegistry.addFunction("LuaItemStack", "setDurability", "Sets the durability of the item.",
                Arrays.asList(new LuaDocRegistry.Param("durability", "number")),
                null);

        LuaDocRegistry.addFunction("LuaItemStack", "getData",
                "Returns data for the item if available (e.g., wool color).", Arrays.asList(),
                Arrays.asList(
                        new LuaDocRegistry.Return("table|string", "A table with data or a message if unavailable")));

        LuaDocRegistry.addFunction("LuaItemStack", "setData", "Sets data on the item, like color for wool blocks.",
                Arrays.asList(new LuaDocRegistry.Param("data", "table")),
                null);
    }
}