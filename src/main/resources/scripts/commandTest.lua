---@param p LuaPlayer
return function(p)
    print('--- LuaCraftBeta runCommand Test Start ---')

    -- Run a command as the server (plugin-level CommandSender)
    local serverCommand = 'say [Lua] This command was run as the server'
    local ok = mc.runCommand(serverCommand)
    if ok then
        print('[✓] Server command ran: ' .. serverCommand)
    else
        print('[x] Server command failed: ' .. serverCommand)
    end

    -- Run a command as this player
    local playerName = p:getName()
    local playerCommand = 'say [Lua] This command was run as ' .. playerName
    local ok2 = mc.runCommand(playerCommand, playerName)
    if ok2 then
        print('[✓] Player command ran: ' .. playerCommand)
    else
        print('[x] Player command failed: ' .. playerCommand)
    end

    -- Try invalid player
    local failCommand = mc.runCommand('say Should fail', 'invalid_player_404')
    if not failCommand then
        print('[✓] Correctly failed to run command as nonexistent player')
    end

    print('--- LuaCraftBeta runCommand Test End ---')
end
