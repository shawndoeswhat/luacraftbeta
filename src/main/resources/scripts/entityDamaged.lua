--- @param attacker LuaWorld|LuaEntity|LuaPlayer
--- @param entity LuaEntity
--- @param damage number
--- @param cause string
return function(attacker, entity, damage, cause)
    if cause ~= "ENTITY_ATTACK" then
        return
    end

    if attacker.getType and attacker:getType() == "player" then
        if attacker.isOp and attacker:isOp() then
            local world = entity:getWorld()
            local loc = entity:getLocation()
            world:strikeLightning(loc)
            wait(0.1)
            attacker:setHealth(20)
        end
    end
end
