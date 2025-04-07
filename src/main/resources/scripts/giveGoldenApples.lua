return function()
    local players = mc.getOnlinePlayers()

    for _, player in ipairs(players) do
        player:giveItem("golden_apple", 1)
        player:sendMessage("You have been blessed with a golden apple!")
    end
end
