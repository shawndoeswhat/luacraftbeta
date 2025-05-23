local nameToLegacy = {
    white_wool = '35:0',
    orange_wool = '35:1',
    magenta_wool = '35:2',
    light_blue_wool = '35:3',
    yellow_wool = '35:4',
    lime_wool = '35:5',
    pink_wool = '35:6',
    gray_wool = '35:7',
    light_gray_wool = '35:8',
    cyan_wool = '35:9',
    purple_wool = '35:10',
    blue_wool = '35:11',
    brown_wool = '35:12',
    green_wool = '35:13',
    red_wool = '35:14',
    black_wool = '35:15',
}

local function resolveMaterial(input)
    if input:find("^%d+:%d+$") then
        return input -- raw id:data
    end

    local lower = input:lower()
    local mapped = nameToLegacy and nameToLegacy[lower]
    return mapped or input
end

---@param player LuaPlayer
---@param message string
return function(player, message)
    if not message:find("^:") then return end

    local args = {}
    for word in message:gmatch("%S+") do
        table.insert(args, word)
    end

    local rawCommand = args[1]:sub(2):lower()
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
            local power = math.min(tonumber(args[3]) or 4.0, 10.0)
            for _, p in ipairs(targets) do
                world:createExplosion(p:getLocation(), power)
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

            -- Determine argument positions based on number of args
            if args[4] then
                targetArg = args[2]
                itemArg = args[3]
                amountArg = args[4]
            elseif args[3] then
                targetArg = "me"
                itemArg = args[2]
                amountArg = args[3]
            else
                player:sendMessage("usage: :give <username> [item] [amount]")
                return
            end

            local amount = tonumber(amountArg)
            if not amount or amount < 1 then
                player:sendMessage("invalid amount: " .. tostring(amountArg))
                return
            end

            local targets = resolveTargets(targetArg)
            local material = resolveMaterial(itemArg)

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

        fire = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local ticks = tonumber(args[3]) or 100
            ticks = math.max(1, math.min(ticks, 1200))
            
            for _, p in ipairs(targets) do
                p:setFireTicks(ticks)
                p:sendMessage("you were set on fire!")
            end

            player:sendMessage("set " .. #targets .. " player(s) on fire for " .. ticks .. " ticks.")
        end,

        summon = function(player, args)
            local entityName = args[2]
            local count = tonumber(args[3]) or 1
            local targetArg = args[4] or "me"

            if not entityName then
                player:sendMessage("usage: :summon <entity> <count> <target>")
                return
            end

            count = math.max(1, math.min(count, 100)) -- clamp between 1 and 100
            local targets = resolveTargets(targetArg)
            local world = player:getWorld()
            local total = 0

            for _, p in ipairs(targets) do
                local pos = p:getLocation()
                for i = 1, count do
                    local result = mc.summon(entityName, world, pos)
                    if result and type(result) == "table" and result.getType then
                        total = total + 1
                    end
                end
                p:sendMessage("summoned " .. count .. " " .. entityName .. "(s) at your location")
            end

            player:sendMessage("summoned a total of " .. total .. " " .. entityName .. "(s)")
        end,

        randomtp = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local radius = tonumber(args[3]) or 1000
            --- @type LuaWorld
            local world = player:getWorld()
            local origin = player:getLocation()

            local function getRandomOffset()
                return math.random(-radius, radius)
            end

            local function getSafeY(x, z)
                for y = 127, 1, -1 do
                    local block = world:getBlockAt(Vector3.new(x, y, z))
                    if block and block:isSolid() then
                        local head = world:getBlockAt(Vector3.new(x, y + 1, z))
                        local above = world:getBlockAt(Vector3.new(x, y + 2, z))
                        if head and not head:isSolid() and above and not above:isSolid() then
                            return y + 1
                        end
                    end
                end
                return nil
            end

            for _, p in ipairs(targets) do
                local dx = getRandomOffset()
                local dz = getRandomOffset()
                local x = origin.x + dx
                local z = origin.z + dz
                local y = getSafeY(x, z)

                if type(y) == 'number' then
                    p:teleport(Vector3.new(x, y, z))
                    p:sendMessage("you have been randomly teleported.")
                else
                    p:sendMessage("Could not find a safe location to teleport.")
                end
            end

            player:sendMessage("randomly teleported " .. #targets .. " player(s) within radius " .. radius)
        end,

        heal = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local count = 0

            for _, p in ipairs(targets) do
                local max = p:getMaxHealth()
                p:setHealth(max)
                p:sendMessage("you have been fully healed.")
                count = count + 1
            end

            player:sendMessage("healed " .. count .. " player(s).")
        end,

        skydive = function(player, args)
            local targets = resolveTargets(args[2] or "me")
            local rawHeight = tonumber(args[3]) or 100

            if rawHeight <= 0 then
                player:sendMessage("height must be a positive number.")
                return
            end

            local height = math.min(rawHeight, 1000)

            for _, p in ipairs(targets) do
                local loc = p:getLocation()
                local newPos = Vector3.new(loc.x, loc.y + height, loc.z)
                p:teleport(newPos)
                p:sendMessage("you were launched " .. height .. " blocks into the sky!")
            end

            player:sendMessage("launched " .. #targets .. " player(s) " .. height .. " blocks up.")
        end,
    }

    local function extinguishPlayers(player, args)
        local targets = resolveTargets(args[2] or "me")

        for _, p in ipairs(targets) do
            p:setFireTicks(0)
            p:sendMessage("you are no longer on fire.")
        end

        player:sendMessage("extinguished " .. #targets .. " player(s)")
    end

    abusiveCommands.unfire = extinguishPlayers
    abusiveCommands.nofire = extinguishPlayers

    local nonAbusiveCommands = {
        help = function(player)
            player:sendMessage("command help is limited in legacy chat.")
            player:sendMessage("read the source file or ask an op/dev for help.")
        end,

        ping = function(player)
            player:sendMessage("pong")
        end,

        coords = function(player)
            local pos = player:getLocation()
            player:sendMessage(string.format("your position is: x=%.2f y=%.2f z=%.2f", pos.x, pos.y, pos.z))
        end,

        dimension = function(player)
            player:sendMessage("you are in: " .. player:getDimension())
        end,

        who = function(player)
            local players = world:getPlayers()
            local names = {}

            for _, p in ipairs(players) do
                table.insert(names, p:getName())
            end

            player:sendMessage("online players: " .. table.concat(names, ", "))
        end,

        health = function(player)
            player:sendMessage("your health: " .. player:getHealth() .. " / " .. player:getMaxHealth())
        end,

        status = function(player)
            local health = player:getHealth()
            local fireTicks = player:getVelocity().y > 0 and player:getHealth() > 0 and player:getFireTicks() or 0
            player:sendMessage("health: " .. health .. " / " .. player:getMaxHealth())
            player:sendMessage("on fire: " .. (fireTicks > 0 and "yes (" .. fireTicks .. " ticks)" or "no"))
        end,

        item = function(player)
            local item = player:getItemInHand()
            if item and item.getType then
                player:sendMessage("you're holding: " .. item:getType())
            else
                player:sendMessage("your hand is empty.")
            end
        end,

        inv = function(player, args)
            local material = args[2]
            if not material then
                player:sendMessage("usage: :inv <item>")
                return
            end

            local count = 0
            local inv = player:getWorld().getBlockAt -- just in case this breaks, fall back later
            for i = 0, 35 do
                local item = player:getInventoryItem(i)
                if item and item.getType then
                    if item:getType():lower() == material:lower() then
                        count = count + item:getAmount()
                    end
                end
            end

            player:sendMessage("you have " .. count .. " of " .. material)
        end,

        compass = function(player)
            local dir = player:getLookDirection()
            local angle = math.atan2(dir.z, dir.x) * (180 / math.pi)
            local facing = "unknown"

            if angle < 0 then angle = angle + 360 end

            if angle >= 45 and angle < 135 then
                facing = "south"
            elseif angle >= 135 and angle < 225 then
                facing = "west"
            elseif angle >= 225 and angle < 315 then
                facing = "north"
            else
                facing = "east"
            end

            player:sendMessage("you are facing " .. facing)
        end,
    }

    local aliases = {
        tp = "teleport",
    }

    local command = aliases[rawCommand] or rawCommand

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
