# Installation Guide - India vs Pakistan Mod

This guide provides step-by-step instructions for installing and configuring the India vs Pakistan Mod on your Minecraft Fabric server.

## Prerequisites

Before installing the mod, ensure you have:

1. **Minecraft Server 1.21.8**
   - Download from the official Minecraft website
   - Ensure your server is running version 1.21.8 exactly

2. **Fabric Server**
   - Download Fabric Server installer from https://fabricmc.net/use/server/
   - Install Fabric for Minecraft 1.21.8
   - The installer will create the necessary server files

3. **Fabric API**
   - Download the latest Fabric API for Minecraft 1.21.8
   - Available from https://modrinth.com/mod/fabric-api or CurseForge
   - This is required for the mod to function

## Installation Steps

### Step 1: Prepare Your Server

1. **Stop your server** if it's currently running
2. **Backup your world** and server files (recommended)
3. **Create a mods folder** in your server directory if it doesn't exist

### Step 2: Install Dependencies

1. **Download Fabric API**
   - Go to https://modrinth.com/mod/fabric-api
   - Download the version for Minecraft 1.21.8
   - Place the JAR file in your `mods` folder

### Step 3: Install the Mod

1. **Download the mod**
   - Get `indiavspakistanmod-1.0.0.jar` from the releases
   - Place it in your server's `mods` folder

2. **Verify installation**
   - Your `mods` folder should contain:
     - `fabric-api-[version].jar`
     - `indiavspakistanmod-1.0.0.jar`

### Step 4: First Server Start

1. **Start your server**
   - The mod will initialize on first startup
   - Configuration files will be created automatically

2. **Check the logs**
   - Look for messages like:
     ```
     [IndiaVsPakistanMod] Initializing India vs Pakistan Mod...
     [IndiaVsPakistanMod] Configuration loaded successfully
     [IndiaVsPakistanMod] India vs Pakistan Mod initialized successfully!
     ```

3. **Verify file creation**
   - Check that these files were created in the `config` folder:
     - `indvspak_config.json`
     - `indvspak_players.json` (created when first player joins)
     - `indvspak_regions.json` (created when first region is made)

## Initial Configuration

### Step 1: Basic Settings

1. **Stop the server**
2. **Edit `config/indvspak_config.json`**
3. **Adjust basic settings**:
   ```json
   {
     "enableIPDetection": true,
     "enableTeamChat": true,
     "enableKillTracking": true,
     "enableProtection": true,
     "allowNeutralTeam": true
   }
   ```

### Step 2: Team Names (Optional)

Customize team names if desired:
```json
{
  "indiaTeamName": "India",
  "pakistanTeamName": "Pakistan", 
  "neutralTeamName": "Neutral"
}
```

### Step 3: Chat Settings

Configure chat behavior:
```json
{
  "teamChatPrefix": "[TEAM]",
  "showTeamPrefixInChat": true,
  "broadcastTeamSwitches": true,
  "broadcastKills": true
}
```

## Setting Up Protected Regions

### Creating Home Bases

1. **Start the server** and join as an operator
2. **Choose locations** for India and Pakistan home bases
3. **Create protected regions**:

   For India base:
   ```
   /region create india_base -100 60 -100 100 120 100 india
   ```

   For Pakistan base:
   ```
   /region create pakistan_base 200 60 200 400 120 400 pakistan
   ```

4. **Verify regions**:
   ```
   /region list
   /region info india_base
   ```

### Region Guidelines

- **Size**: Make bases large enough for team activities
- **Height**: Include sufficient vertical space (Y levels)
- **Separation**: Keep team bases far apart to prevent conflicts
- **Resources**: Consider placing bases near different resource areas

## Testing the Installation

### Step 1: Basic Functionality

1. **Join the server** with a test account
2. **Check team assignment**:
   ```
   /team info
   ```
3. **Test team switching**:
   ```
   /team switch india
   /team switch pakistan
   /team switch neutral
   ```

### Step 2: Chat System

1. **Test global chat** - Send a normal message
2. **Test team chat**:
   ```
   /tc Hello team!
   /tc
   ```
3. **Verify prefixes** appear in chat messages

### Step 3: Protection System

1. **Go to a protected region**
2. **Try breaking blocks** as opposing team member
3. **Verify protection** prevents unauthorized actions

### Step 4: Statistics

1. **Test kill tracking** (if possible with multiple players)
2. **Check statistics**:
   ```
   /stats
   /stats team
   /stats leaderboard
   ```

## Advanced Configuration

### Performance Tuning

For large servers, consider these settings:

```json
{
  "persistPlayerData": true,
  "trackTeamKills": false,
  "announceKillStreaks": false
}
```

### Cracked Server Considerations

- IP detection may not work reliably on cracked servers
- Players will likely be assigned to Neutral team initially
- Encourage manual team selection using `/team switch`
- Consider disabling IP detection: `"enableIPDetection": false`

### Multiple Worlds

The mod works across multiple worlds:
- Regions are world-specific
- Player teams persist across worlds
- Statistics are global across all worlds

## Maintenance

### Regular Tasks

1. **Backup configuration files** regularly
2. **Monitor server logs** for errors
3. **Update the mod** when new versions are available
4. **Clean up old player data** if needed

### Configuration Updates

To update configuration without restart:
```
/indvspakconfig reload
```

To save current settings:
```
/indvspakconfig save
```

### Data Management

Player data is automatically saved:
- Every time a player's team changes
- When statistics are updated
- Periodically during gameplay
- When the server shuts down

## Troubleshooting Installation

### Mod Not Loading

**Symptoms**: No mod messages in server log

**Solutions**:
1. Verify Minecraft version compatibility (1.21.8)
2. Ensure Fabric API is installed
3. Check mod file integrity
4. Review server startup logs for errors

### Configuration Errors

**Symptoms**: Mod loads but features don't work

**Solutions**:
1. Validate JSON syntax in config files
2. Reset configuration to defaults
3. Check file permissions
4. Use `/indvspakconfig show` to verify settings

### Permission Issues

**Symptoms**: Admin commands don't work

**Solutions**:
1. Ensure you have OP level 2 or higher
2. Check server operator configuration
3. Verify command syntax with `/indvspakhelp admin`

### Performance Issues

**Symptoms**: Server lag or slow response

**Solutions**:
1. Reduce region sizes if very large
2. Disable unnecessary features in config
3. Monitor server resources
4. Consider reducing announcement frequency

## Getting Help

### Built-in Help

Use the comprehensive help system:
```
/indvspakhelp
/indvspakhelp team
/indvspakhelp region
/indvspakhelp stats
/indvspakhelp chat
/indvspakhelp admin
```

### Log Analysis

Check these log messages:
- `[IndiaVsPakistanMod]` - General mod messages
- `ERROR` - Error conditions
- `WARN` - Warning conditions

### Common Solutions

1. **Restart the server** - Fixes many temporary issues
2. **Reload configuration** - `/indvspakconfig reload`
3. **Reset player data** - Delete `indvspak_players.json` (loses statistics)
4. **Reset regions** - Delete `indvspak_regions.json` (loses protected areas)

## Security Considerations

### Operator Permissions

- Only trusted players should have OP level 2+
- Admin commands can significantly impact gameplay
- Monitor region creation and deletion

### Player Data

- Player data includes IP information (if detection enabled)
- Consider privacy implications
- Regularly clean up inactive player data

### Griefing Prevention

- Properly configure protected regions
- Monitor team switching frequency
- Use statistics to identify problematic behavior

---

**Installation Complete!** Your India vs Pakistan Mod should now be fully functional. Players can join teams, chat with teammates, and enjoy protected home bases with full kill tracking and statistics.

For ongoing support, refer to the main README.md file and use the built-in help commands.

