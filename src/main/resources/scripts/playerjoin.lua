--- @param player LuaPlayer
return function(player)
    local key = player:getName() .. "_join"
    local data = storage.getSavedData(key)

    local now = os.date("%Y-%m-%d %H:%M:%S")

    if not data or not data.joinDate then
        data = {
            joinDate = now,
            lastSeen = now
        }
        storage.applySaveData(key, data)
        player:sendMessage("Welcome! Your join date has been saved: " .. now)
    else
        data.lastSeen = now
        storage.applySaveData(key, data)
        player:sendMessage("Welcome back! Your join date is: " .. data.joinDate)
        player:sendMessage("Your last login was: " .. (data.lastSeen or "unknown"))
    end
end
