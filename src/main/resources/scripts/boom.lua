return function(player)
    local radius = 20
    local center = player:getLocation()
    local world = player:getWorld()

    for angle = 0, 360, 10 do
        local radians = math.rad(angle)
        local x = center:getX() + radius * math.cos(radians)
        local z = center:getZ() + radius * math.sin(radians)
        local y = center:getY()

        local vec = Vector3.new(x, y, z)

        print('making big boom @ ' .. tostring(angle) .. ' degrees')
        coroutine.resume(coroutine.create(world.createExplosion), world, vec, 5)
    end
end
