---@param player LuaPlayer
---@param message string
return function(player, message)
    if not message:find("^:") then return end

    local args = {}
    for word in message:gmatch("%S+") do
        table.insert(args, word)
    end

    local command = args[1]:sub(2):lower()
    local world = player:getWorld()
    local senderName = player:getName():lower()

    local function findPlayerByName(name)
        name = name:lower()
        for _, p in ipairs(world:getPlayers()) do
            if p:getName():lower() == name then
                return p
            end
        end
        return nil
    end

    local function resolveTargets(who)
        local result = {}
        local seen = {}
        local allPlayers = world:getPlayers()

        who = who and who:lower() or "me" -- default to "me" if nil or empty

        if who == "me" then
            return { player }
        elseif who == "all" then
            return allPlayers
        elseif who == "others" then
            for _, p in ipairs(allPlayers) do
                if p:getName():lower() ~= senderName then
                    table.insert(result, p)
                end
            end
            return result
        else
            for name in who:gmatch("[^,]+") do
                name = name:lower()
                if not seen[name] then
                    local p = findPlayerByName(name)
                    if p then
                        table.insert(result, p)
                        seen[name] = true
                    else
                        player:sendMessage("couldn't find player: " .. name)
                    end
                end
            end
            return result
        end
    end

    local abusiveCommands = {
        kill = function(player, args)
            local targets = resolveTargets(args[2])
            for _, p in ipairs(targets) do
                p:kill()
                p:sendMessage("you have been slain by a command.")
            end
            player:sendMessage("killed " .. #targets .. " player(s)")
        end,

        teleport = function(player, args)
            local sources = resolveTargets(args[2])
            local targetPlayer = findPlayerByName(args[3] or "")
            if not targetPlayer then
                player:sendMessage("couldn't find target player: " .. (args[3] or "nil"))
                return
            end
            local targetPos = targetPlayer:getLocation()
            for _, p in ipairs(sources) do
                p:teleport(targetPos)
                p:sendMessage("you were teleported to " .. targetPlayer:getName())
            end
            player:sendMessage("teleported " .. #sources .. " player(s) to " .. targetPlayer:getName())
        end,

        explode = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            for _, p in ipairs(targets) do
                world:createExplosion(p:getLocation(), 4.0)
                p:sendMessage("boom.")
            end
            player:sendMessage("exploded " .. #targets .. " player(s)")
        end,

        smite = function(player, args)
            local targets = resolveTargets(args[2])
            for _, p in ipairs(targets) do
                world:strikeLightning(p:getLocation())
                p:sendMessage("you were smitten.")
            end
            player:sendMessage("smote " .. #targets .. " player(s)")
        end,

        give = function(player, args)
            local targetArg, itemArg, amountArg

            if args[4] then
                targetArg = args[2]
                itemArg = args[3]
                amountArg = args[4]
            elseif args[3] then
                targetArg = "me"
                itemArg = args[2]
                amountArg = args[3]
            else
                player:sendMessage("usage: :give [target] <item> <amount>")
                return
            end

            local targets = resolveTargets(targetArg)
            local material = itemArg
            local amount = tonumber(amountArg) or 1

            for _, p in ipairs(targets) do
                p:giveItem(material, amount)
                p:sendMessage("you received " .. amount .. " of " .. material)
            end

            player:sendMessage("gave " .. amount .. "x " .. material .. " to " .. #targets .. " player(s)")
        end,

        time = function(player, args)
            local timeArg = args[2] and args[2]:lower() or nil
            local world = player:getWorld()
        
            if not timeArg then
                player:sendMessage("usage: :time [day|night|<number>]")
                return
            end
        
            if timeArg == "day" then
                world:setTime(0)
                player:sendMessage("time set to day (0)")
            elseif timeArg == "night" then
                world:setTime(13000)
                player:sendMessage("time set to night (13000)")
            else
                local customTime = tonumber(timeArg)
                if customTime and customTime >= 0 and customTime <= 24000 then
                    world:setTime(customTime)
                    player:sendMessage("time set to " .. customTime)
                else
                    player:sendMessage("invalid time value: " .. tostring(timeArg))
                end
            end
        end,
    }

    local nonAbusiveCommands = {
        help = function(player)
            player:sendMessage("command help is limited in legacy chat.")
            player:sendMessage("read the source file or ask an op/dev for help.")
        end,

        ping = function(player)
            player:sendMessage("pong")
        end,
    }

    -- dispatch
    if nonAbusiveCommands[command] then
        return nonAbusiveCommands[command](player, args)
    end

    if abusiveCommands[command] then
        if not player:isOp() then
            player:sendMessage("you do not have permission to use the :" .. command .. " command.")
            return
        end
        return abusiveCommands[command](player, args)
    end

    player:sendMessage("unknown command: " .. command)
end
