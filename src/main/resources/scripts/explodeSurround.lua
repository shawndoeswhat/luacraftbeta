--- @param player LuaPlayer
return function(player)
    local world = player:getWorld()
    --- @type Vector3
    local pos = player:getLocation()

    local radius = 5       -- distance from player
    local power = 10       -- explosion strength
    local height = 0       -- explosions at same Y-level

    local offsets = {
        { x = radius, z = 0 },
        { x = -radius, z = 0 },
        { x = 0, z = radius },
        { x = 0, z = -radius },
        { x = radius, z = radius },
        { x = -radius, z = radius },
        { x = radius, z = -radius },
        { x = -radius, z = -radius },
    }

    for _, offset in ipairs(offsets) do
        local explosionPos = Vector3.new(
            pos.x + offset.x,
            pos.y + height,
            pos.z + offset.z
        )
        world:createExplosion(explosionPos, power)
    end

    player:sendMessage("Boom! Surrounded by explosions.")
end
