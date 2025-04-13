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
            entity:kill()
        end
    end
end
