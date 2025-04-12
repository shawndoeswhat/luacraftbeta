

--- @param player LuaPlayer
--- @param action_name string
--- @param block LuaBlock
return function(player, action_name, block)
    local messages = {
        'ouch, that\'s got to hurt.',
        'mommy said not to play with fire.',
        'you love being cooked, don\'t you?',
        'it appears that you are on fire...'
    }

    if action_name == MC_ACTION.RIGHT_CLICK_BLOCK and block:getTypeId() == 51 then
        local randomMessage = messages[math.random(1, #messages)]
        player:setFireTicks(20)
        player:sendMessage('§e' .. randomMessage)
    end
end
