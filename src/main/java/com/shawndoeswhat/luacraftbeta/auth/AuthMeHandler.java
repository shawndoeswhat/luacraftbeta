package com.shawndoeswhat.luacraftbeta.auth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class AuthMeHandler {
    private static boolean available = false;
    private static Class<?> playerCacheClass;
    private static Method getInstanceMethod;
    private static Method isAuthenticatedMethod;

    public static void init() {
        try {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("AuthMe");
            if (plugin == null || !plugin.isEnabled()) return;

            playerCacheClass = Class.forName("uk.org.whoami.authme.cache.auth.PlayerCache");
            getInstanceMethod = playerCacheClass.getMethod("getInstance");
            isAuthenticatedMethod = playerCacheClass.getMethod("isAuthenticated", String.class);

            available = true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("[LuaCraftBeta] AuthMe not found or incompatible.");
        }
    }

    public static boolean isAvailable() {
        return available;
    }

    public static boolean isPlayerLoggedIn(Player player) {
        if (!available) return true;

        try {
            Object playerCache = getInstanceMethod.invoke(null);
            return (boolean) isAuthenticatedMethod.invoke(playerCache, player.getName().toLowerCase());
        } catch (Exception e) {
            Bukkit.getLogger().warning("[LuaCraftBeta] Failed to check player auth status: " + e.getMessage());
            return false;
        }
    }
}
