--- @param player LuaPlayer
return function(player)
    local items = {
        "diamond_helmet",
        "diamond_chestplate",
        "diamond_leggings",
        "diamond_boots",
        "diamond_sword",
        "diamond_pickaxe",
        "diamond_axe",
        "diamond_shovel"
    }

    for _, item in ipairs(items) do
        player:giveItem(item, 1)
    end

    player:sendMessage("You have received a full set of diamond gear!")
end
