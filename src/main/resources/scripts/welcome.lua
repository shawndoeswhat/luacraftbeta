--- @param player LuaPlayer
return function(player)
    local key = player:getName() .. "_join"
    local data = storage.getSavedData(key)

    if not data or not data.joinDate then
        local now = os.date("%Y-%m-%d %H:%M:%S")
        storage.applySaveData(key, {
            joinDate = now
        })
        player:sendMessage("Welcome! Your join date has been saved: " .. now)
    else
        player:sendMessage("Welcome back! Your join date is: " .. data.joinDate)
    end
end
