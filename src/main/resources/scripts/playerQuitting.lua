--- @param player LuaPlayer
return function(player)
    local key = player:getName() .. "_quit"
    local now = os.date("%Y-%m-%d %H:%M:%S")
    
    storage.applySaveData(key, {
        lastSeen = now
    })

    player:sendMessage("Goodbye! We'll remember you were last seen at: " .. now)
end
