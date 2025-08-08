# India vs Pakistan Mod

A comprehensive Minecraft Fabric mod that creates an exciting team-based PvP experience with India and Pakistan teams. This mod is designed for cracked servers and includes automatic team assignment, protected home bases, kill tracking, team chat, and anti-griefing features.

## Features

### 🏴 Team Management
- **Automatic Team Assignment**: Players are automatically assigned to teams based on their IP location (India/Pakistan/Neutral)
- **Manual Team Switching**: Players can switch teams using commands
- **Team Persistence**: Team assignments are saved and persist across server restarts
- **Team Prefixes**: All chat messages display team prefixes with colors

### 🏠 Home Base Protection
- **Protected Regions**: Create protected areas for each team's home base
- **Anti-Griefing**: Prevents opposing teams from breaking/placing blocks in protected areas
- **Anti-Theft**: Protects against inventory theft and unauthorized access
- **Flexible Permissions**: Configure access permissions for teammates and neutral players

### 📊 Kill Tracking & Statistics
- **Individual Stats**: Track kills, deaths, and K/D ratio for each player
- **Team Statistics**: Monitor team performance and kill counts
- **Leaderboards**: View top players and team rankings
- **Kill Announcements**: Broadcast kills with team colors and information

### 💬 Advanced Chat System
- **Team Prefixes**: Automatic team prefixes in all chat messages
- **Team Chat**: Private communication within teams
- **Global Chat**: Public communication with team identification
- **Chat Toggle**: Switch between team and global chat modes

### ⚙️ Configuration & Administration
- **Configurable Settings**: Extensive configuration options via JSON file
- **Admin Commands**: Comprehensive admin tools for server management
- **Help System**: Built-in help commands for all features
- **Real-time Configuration**: Reload settings without server restart

## Installation

### Requirements
- Minecraft 1.21.8
- Fabric Loader
- Fabric API

### Installation Steps
1. Download the latest release of `indiavspakistanmod-1.0.0.jar`
2. Place the JAR file in your server's `mods` folder
3. Ensure Fabric API is also installed in the `mods` folder
4. Start your server
5. The mod will create a configuration file on first run

## Commands

### Player Commands

#### Team Management
- `/team` - Show team command help
- `/team info` - Display your current team
- `/team list` - List all available teams
- `/team switch <team>` - Switch to a different team (india/pakistan/neutral)

#### Chat Commands
- `/tc` or `/teamchat` - Toggle team chat mode
- `/tc <message>` - Send a team chat message
- `/gc <message>` or `/globalchat <message>` - Send a global message (when in team chat mode)

#### Statistics
- `/stats` - Show your statistics
- `/stats player <player>` - Show another player's statistics
- `/stats team` - Show team statistics
- `/stats leaderboard` - Show kill leaderboard

#### Help
- `/indvspakhelp` - Show general help
- `/indvspakhelp team` - Team management help
- `/indvspakhelp region` - Region protection help
- `/indvspakhelp stats` - Statistics help
- `/indvspakhelp chat` - Chat commands help
- `/indvspakhelp admin` - Admin commands help

### Admin Commands (Requires OP Level 2)

#### Team Administration
- `/teamadmin assign <player> <team>` - Assign a player to a team
- `/teamadmin list` - List all players and their teams
- `/teamadmin stats` - Show detailed team statistics

#### Region Management
- `/region create <name> <x1> <y1> <z1> <x2> <y2> <z2> <team>` - Create a protected region
- `/region delete <name>` - Delete a protected region
- `/region list` - List all protected regions
- `/region info <name>` - Show detailed region information
- `/region here` - Show regions at your current location

#### Statistics Management
- `/stats reset teams` - Reset all team statistics
- `/stats reset player <player>` - Reset a player's statistics

#### Configuration
- `/indvspakconfig show` - Display current configuration
- `/indvspakconfig reload` - Reload configuration from file
- `/indvspakconfig save` - Save current configuration
- `/indvspakconfig set <setting> <value>` - Change a configuration setting

## Configuration

The mod creates a configuration file at `config/indvspak_config.json` with the following options:

```json
{
  "enableIPDetection": true,
  "enableTeamChat": true,
  "enableKillTracking": true,
  "enableProtection": true,
  "broadcastTeamSwitches": true,
  "broadcastKills": true,
  "allowNeutralTeam": true,
  "persistPlayerData": true,
  "indiaTeamName": "India",
  "pakistanTeamName": "Pakistan",
  "neutralTeamName": "Neutral",
  "teamChatPrefix": "[TEAM]",
  "globalChatPrefix": "",
  "showTeamPrefixInChat": true,
  "allowTeammateAccess": true,
  "allowNeutralAccess": false,
  "protectFromTeammates": true,
  "trackTeamKills": false,
  "announceFirstBlood": true,
  "announceKillStreaks": true,
  "killStreakThreshold": 5,
  "geoipDatabasePath": "/assets/indiavspakistanmod/GeoLite2-Country.mmdb",
  "fallbackToNeutral": true
}
```

### Configuration Options

#### Core Features
- `enableIPDetection`: Enable automatic team assignment based on IP location
- `enableTeamChat`: Enable team chat functionality
- `enableKillTracking`: Enable kill/death tracking and statistics
- `enableProtection`: Enable region protection system

#### Team Settings
- `indiaTeamName`: Display name for India team
- `pakistanTeamName`: Display name for Pakistan team
- `neutralTeamName`: Display name for Neutral team
- `allowNeutralTeam`: Allow players to join neutral team

#### Chat Settings
- `teamChatPrefix`: Prefix for team chat messages
- `showTeamPrefixInChat`: Show team prefixes in all chat messages
- `broadcastTeamSwitches`: Announce when players switch teams
- `broadcastKills`: Announce kills in chat

#### Protection Settings
- `allowTeammateAccess`: Allow teammates to access protected regions
- `allowNeutralAccess`: Allow neutral players to access protected regions
- `protectFromTeammates`: Protect against theft from same-team players

#### Statistics Settings
- `trackTeamKills`: Count team kills in statistics (usually disabled)
- `announceFirstBlood`: Announce the first kill of the match
- `announceKillStreaks`: Announce kill streaks
- `killStreakThreshold`: Number of kills required for kill streak announcement

## Data Storage

The mod stores data in the following files:
- `config/indvspak_config.json` - Configuration settings
- `config/indvspak_players.json` - Player data (teams, kills, deaths)
- `config/indvspak_regions.json` - Protected regions data

All data files are automatically created and managed by the mod.

## Team Colors

- **India Team**: Green text and formatting
- **Pakistan Team**: Dark Green text and formatting  
- **Neutral Team**: Gray text and formatting

## Troubleshooting

### Common Issues

#### Mod Not Loading
- Ensure you have the correct Minecraft version (1.21.8)
- Verify Fabric Loader and Fabric API are installed
- Check server logs for error messages

#### IP Detection Not Working
- IP detection is currently simplified and assigns all players to Neutral team
- This can be enhanced with proper GeoIP database integration
- Players can manually switch teams using `/team switch <team>`

#### Commands Not Working
- Ensure you have the required permission level for admin commands
- Check that the mod is properly loaded in the server
- Verify command syntax using `/indvspakhelp`

#### Configuration Changes Not Applied
- Use `/indvspakconfig reload` to reload configuration
- Restart the server if configuration reload doesn't work
- Check configuration file syntax for JSON errors

### Performance Considerations

- The mod is designed to be lightweight and efficient
- Region protection checks are optimized for minimal performance impact
- Statistics are stored in memory and periodically saved to disk
- Chat modifications use efficient event handling

### Compatibility

- Designed for Fabric mod loader
- Compatible with most other Fabric mods
- May conflict with other team-based or protection mods
- Tested on cracked servers

## Support

For issues, suggestions, or contributions:
1. Check the troubleshooting section above
2. Review the configuration options
3. Use the built-in help commands (`/indvspakhelp`)
4. Check server logs for error messages

## License

This mod is provided as-is for educational and entertainment purposes. Please respect server rules and player preferences when using team-based gameplay features.

## Version Information

- **Mod Version**: 1.0.0
- **Minecraft Version**: 1.21.8
- **Fabric API**: Compatible with latest versions
- **Build Date**: Generated automatically during build process

---

*Enjoy your India vs Pakistan team battles! 🇮🇳 vs 🇵🇰*

