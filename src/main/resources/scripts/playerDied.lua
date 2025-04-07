local randomDeathMessages = {
    'outplayed.',
    'that\'s on you.',
    'was it worth it?',
    'maybe try blocking next time.',
    'unexpected, but not surprising.',
    'you blinked.',
    'sent to the shadow realm.',
    'deleted.',
    'you stood still.',
    'missed the clutch.',
    'fatal miscalculation.',
    'that one\'s going on your record.',
    'should\'ve zigged.',
    'saw that coming.'
}

--- @param player LuaPlayer
--- @param deathMessage string
return function(player, deathMessage)
    local name = player:getName()

    -- choose a random dry/sarcastic message
    local randomIndex = math.random(1, #randomDeathMessages)
    local randomMessage = randomDeathMessages[randomIndex]
    player:sendMessage('§e' .. randomMessage)

    local key = name .. "_deathLog"
    local existing = storage.getSavedData(key)
    if type(existing) ~= "table" then
        existing = {}
    end

    table.insert(existing, os.date("%Y-%m-%d %H:%M:%S") .. " - " .. deathMessage)
    storage.applySaveData(key, existing)
end
