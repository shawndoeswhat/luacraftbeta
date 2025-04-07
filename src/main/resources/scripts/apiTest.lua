--- @param p LuaPlayer
return function(p)
    -- run this via something like: /loadscript apitest.lua
    print('--- LuaCraftBeta API Test Start ---')

    local players = mc.getOnlinePlayers()
    if #players == 0 then
        print('No players online to test with')
        return
    end

    print('Testing with player: ' .. p:getName())

    -- LuaPlayer tests
    print('Player isOp: ' .. tostring(p:isOp()))
    print('Health: ' .. tostring(p:getHealth()) .. '/' .. tostring(p:getMaxHealth()))
    print('IsAlive: ' .. tostring(p:isAlive()))
    p:heal()
    print('Healed. New health: ' .. tostring(p:getHealth()))
    p:damage(2)
    print('Damaged for 2. New health: ' .. tostring(p:getHealth()))
    p:setHealth(15)
    print('Set health to 15. Current: ' .. tostring(p:getHealth()))
    p:setVelocity(Vector3.new(0, 1, 0))
    print('Set upward velocity')

    -- LuaWorld tests
    local world = p:getWorld()
    print('World dimension: ' .. world:getDimension())
    print('Time: ' .. world:getTime())
    world:setTime(world:getTime() + 1000)
    print('Advanced time by 1000 ticks')
    print('Is raining: ' .. tostring(world:isRaining()))
    world:setStorm(true)
    print('Forced storm')

    -- LuaVector3 tests
    local loc = p:getLocation()
    print('Player position: x=' .. loc.x .. ' y=' .. loc.y .. ' z=' .. loc.z)

    -- LuaEntity tests
    local entities = world:getEntities()
    print('Found ' .. #entities .. ' entities in world')

    for i = 1, math.min(#entities, 3) do
        local e = entities[i]
        print(i .. ') Type=' .. e:getType() .. ', ID=' .. e:getId() .. ', Name=' .. e:getName())
        if e.isAlive and e:isAlive() then
            print(' -> Health: ' .. e:getHealth())
        end
    end

    -- Block test
    local block = world:getBlockAt(loc)
    if block then
        print('Block at player: ' .. tostring(block:getType()))
    end

    print('--- LuaCraftBeta API Test End ---')
end
