--- @param player LuaPlayer
--- @param message string
return function (player, message)
    message = message:lower()
    --- @type LuaWorld
    local world = player:getWorld()

    if message:find("rain") and not (message:find("no rain") or message:find("clear")) then
        player:sendMessage("you mentioned rain... making it rain.")
        world:setStorm(true)
    elseif message:find("no rain") or message:find("clear") then
        player:sendMessage("you asked for clear weather. clearing it up!")
        world:setStorm(false)
    end

    if message:find("day") then
        player:sendMessage("you mentioned day... setting time to morning.")
        world:setTime(0)
    elseif message:find("night") then
        player:sendMessage("night time it is. sweet dreams.")
        world:setTime(13000)
    end
end
