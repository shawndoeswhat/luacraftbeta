--- @param player LuaPlayer
return function(player)
    local world = player:getWorld()
    --- @type Vector3
    local pos = player:getLocation()

    local count = 5
    local spacing = 2
    local height = 100

    for i = 1, count do
        local offsetX = (math.random() - 0.5) * spacing * 2
        local offsetZ = (math.random() - 0.5) * spacing * 2

        mc.summon("zombie", world, Vector3.new(
            pos.x + offsetX,
            pos.y + height,
            pos.z + offsetZ
        ))
    end

    player:sendMessage("Incoming! Zombies are falling from the sky...")
end
