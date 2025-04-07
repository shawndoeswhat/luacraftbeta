---@param player LuaPlayer
return function(player)
    local pos = player:getLocation()
    pos.y = pos.y + 500

    player:sendMessage("See you on the ground!")
    player:teleport(pos)
end
