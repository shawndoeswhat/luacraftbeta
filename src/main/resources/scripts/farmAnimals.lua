---@param player LuaPlayer
return function(player)
    local world = player:getWorld()
    ---@type Vector3
    local pos = player:getLocation()

    local animals = { "pig", "sheep", "cow", "chicken" }
    local spacing = 2

    for i, type in ipairs(animals) do
        local offsetX = (i - (#animals + 1) / 2) * spacing
        mc.summon(type, world, Vector3.new(
            pos.x + offsetX,
            pos.y,
            pos.z
        ))
    end

    player:sendMessage("ur barnyard buddies have arrived")
end
