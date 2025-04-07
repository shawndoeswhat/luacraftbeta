--- broadcasts information about LuaCraftBeta and LuaJ
--- @param player LuaPlayer
return function(player)
    local version = mc.getVersion()
    local scriptCount = mc.getLoadedScriptCount and mc.getLoadedScriptCount() or "unknown"
    local luajVersion = mc.getLuaJVersion and mc.getLuaJVersion() or "unknown"
    local players = mc.getOnlinePlayers()

    local lines = {
        '§6[LuaCraftBeta] §fCustom Lua API for Bukkit Beta 1.7.3!',
        '§eEngine: §fLuaJ ' .. luajVersion,
        '§eLoaded Scripts: §f' .. scriptCount,
        '§ePlugin Version: §f' .. version,
        '§7Explore and automate your Minecraft world with Lua power!'
    }

    for i = 1, #players do
        local p = players[i]
        for j = 1, #lines do
            p:sendMessage(lines[j])
        end
    end
end
