--- @param player LuaPlayer
return function(player)
    local key = player:getName() .. "_quit"
    
    local epochTime = epoch()
    local now = epochToDate(epochTime)
    
    storage.applySaveData(key, {
        lastSeen = now
    })

    player:sendMessage("Goodbye! We'll remember you were last seen at: " .. now)
end
