# Configuration Reference - India vs Pakistan Mod

This document provides a comprehensive reference for all configuration options available in the India vs Pakistan Mod.

## Configuration File Location

The main configuration file is located at:
```
config/indvspak_config.json
```

This file is automatically created when the mod first runs and can be modified while the server is running (use `/indvspakconfig reload` to apply changes).

## Configuration Structure

The configuration file uses JSON format with the following structure:

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

## Core Feature Settings

### enableIPDetection
- **Type**: Boolean
- **Default**: `true`
- **Description**: Enables automatic team assignment based on player IP geolocation
- **Notes**: Currently simplified - assigns all players to Neutral team
- **Command**: `/indvspakconfig set enableIPDetection <true|false>`

### enableTeamChat
- **Type**: Boolean
- **Default**: `true`
- **Description**: Enables team chat functionality and commands
- **Impact**: Disabling removes `/tc` and `/teamchat` commands
- **Command**: `/indvspakconfig set enableTeamChat <true|false>`

### enableKillTracking
- **Type**: Boolean
- **Default**: `true`
- **Description**: Enables kill/death tracking and statistics system
- **Impact**: Disabling removes all statistics features and commands
- **Command**: `/indvspakconfig set enableKillTracking <true|false>`

### enableProtection
- **Type**: Boolean
- **Default**: `true`
- **Description**: Enables region protection system
- **Impact**: Disabling removes all protection features and region commands
- **Command**: `/indvspakconfig set enableProtection <true|false>`

## Team Management Settings

### allowNeutralTeam
- **Type**: Boolean
- **Default**: `true`
- **Description**: Allows players to join or be assigned to the neutral team
- **Impact**: When disabled, players can only be on India or Pakistan teams
- **Command**: `/indvspakconfig set allowNeutralTeam <true|false>`

### indiaTeamName
- **Type**: String
- **Default**: `"India"`
- **Description**: Display name for the India team
- **Usage**: Appears in chat prefixes, commands, and messages
- **Validation**: Must be non-empty string

### pakistanTeamName
- **Type**: String
- **Default**: `"Pakistan"`
- **Description**: Display name for the Pakistan team
- **Usage**: Appears in chat prefixes, commands, and messages
- **Validation**: Must be non-empty string

### neutralTeamName
- **Type**: String
- **Default**: `"Neutral"`
- **Description**: Display name for the neutral team
- **Usage**: Appears in chat prefixes, commands, and messages
- **Validation**: Must be non-empty string

## Chat System Settings

### teamChatPrefix
- **Type**: String
- **Default**: `"[TEAM]"`
- **Description**: Prefix displayed before team chat messages
- **Example**: `[TEAM] PlayerName: Hello team!`
- **Command**: `/indvspakconfig set teamChatPrefix <value>`

### globalChatPrefix
- **Type**: String
- **Default**: `""` (empty)
- **Description**: Prefix displayed before global chat messages
- **Usage**: Usually left empty; team prefixes are added automatically
- **Example**: If set to `[GLOBAL]`: `[GLOBAL] [India] PlayerName: Hello everyone!`

### showTeamPrefixInChat
- **Type**: Boolean
- **Default**: `true`
- **Description**: Shows team prefixes in all chat messages
- **Impact**: When disabled, removes `[India]`, `[Pakistan]`, `[Neutral]` from chat
- **Command**: `/indvspakconfig set showTeamPrefixInChat <true|false>`

### broadcastTeamSwitches
- **Type**: Boolean
- **Default**: `true`
- **Description**: Announces when players switch teams
- **Example**: `PlayerName switched from India to Pakistan`
- **Command**: `/indvspakconfig set broadcastTeamSwitches <true|false>`

### broadcastKills
- **Type**: Boolean
- **Default**: `true`
- **Description**: Announces kills in chat with team information
- **Example**: `PlayerA (India) killed PlayerB (Pakistan)`
- **Command**: `/indvspakconfig set broadcastKills <true|false>`

## Protection System Settings

### allowTeammateAccess
- **Type**: Boolean
- **Default**: `true`
- **Description**: Allows team members to access their team's protected regions
- **Impact**: When disabled, only region owners can access protected areas
- **Use Case**: Set to `false` for individual player protection

### allowNeutralAccess
- **Type**: Boolean
- **Default**: `false`
- **Description**: Allows neutral team players to access all protected regions
- **Impact**: When enabled, neutral players can enter any protected area
- **Use Case**: Useful for moderators or neutral observers

### protectFromTeammates
- **Type**: Boolean
- **Default**: `true`
- **Description**: Protects against theft and griefing from same-team players
- **Impact**: Prevents teammates from stealing items or griefing each other
- **Use Case**: Disable for full team cooperation without restrictions

## Statistics Settings

### trackTeamKills
- **Type**: Boolean
- **Default**: `false`
- **Description**: Includes team kills (friendly fire) in statistics
- **Impact**: When enabled, killing teammates counts toward kill statistics
- **Recommendation**: Usually kept disabled to discourage team killing

### announceFirstBlood
- **Type**: Boolean
- **Default**: `true`
- **Description**: Announces the first kill of the match/session
- **Example**: `PlayerName drew first blood!`
- **Impact**: Special announcement for the first kill

### announceKillStreaks
- **Type**: Boolean
- **Default**: `true`
- **Description**: Announces when players achieve kill streaks
- **Example**: `PlayerName is on a killing spree! (5 kills)`
- **Threshold**: Controlled by `killStreakThreshold`

### killStreakThreshold
- **Type**: Integer
- **Default**: `5`
- **Range**: 1-50
- **Description**: Number of consecutive kills required for kill streak announcement
- **Command**: `/indvspakconfig set killStreakThreshold <number>`

## Data Persistence Settings

### persistPlayerData
- **Type**: Boolean
- **Default**: `true`
- **Description**: Saves player data (teams, statistics) to disk
- **Impact**: When disabled, all data is lost on server restart
- **Files**: Controls saving of `indvspak_players.json`

## IP Detection Settings

### geoipDatabasePath
- **Type**: String
- **Default**: `"/assets/indiavspakistanmod/GeoLite2-Country.mmdb"`
- **Description**: Path to GeoIP database file for country detection
- **Status**: Currently not implemented - placeholder for future enhancement
- **Notes**: IP detection is simplified in current version

### fallbackToNeutral
- **Type**: Boolean
- **Default**: `true`
- **Description**: Assigns players to neutral team when country detection fails
- **Alternative**: When disabled, players might be assigned randomly
- **Current**: All players are assigned to neutral team regardless

## Configuration Management Commands

### Viewing Configuration
```bash
/indvspakconfig show
```
Displays all current configuration values.

### Reloading Configuration
```bash
/indvspakconfig reload
```
Reloads configuration from the file without restarting the server.

### Saving Configuration
```bash
/indvspakconfig save
```
Saves current in-memory configuration to the file.

### Modifying Settings
```bash
/indvspakconfig set <setting> <value>
```

Available settings for the `set` command:
- `enableIPDetection`
- `enableTeamChat`
- `enableKillTracking`
- `enableProtection`
- `broadcastTeamSwitches`
- `broadcastKills`
- `allowNeutralTeam`
- `killStreakThreshold`
- `teamChatPrefix`

## Configuration Validation

The mod validates configuration values when loading:

### Boolean Values
- Must be exactly `true` or `false`
- Case-sensitive

### String Values
- Must be valid JSON strings
- Cannot be null for required fields
- Team names cannot be empty

### Integer Values
- Must be valid integers
- Range validation applies where specified
- `killStreakThreshold`: 1-50

### File Paths
- Must be valid path strings
- Currently not validated for existence

## Configuration Presets

### Competitive Setup
```json
{
  "enableIPDetection": false,
  "allowNeutralTeam": false,
  "broadcastKills": true,
  "announceKillStreaks": true,
  "killStreakThreshold": 3,
  "protectFromTeammates": false,
  "trackTeamKills": false
}
```

### Casual Setup
```json
{
  "enableIPDetection": true,
  "allowNeutralTeam": true,
  "broadcastKills": false,
  "announceKillStreaks": false,
  "protectFromTeammates": true,
  "trackTeamKills": false
}
```

### Testing Setup
```json
{
  "enableIPDetection": false,
  "broadcastTeamSwitches": true,
  "broadcastKills": true,
  "announceFirstBlood": true,
  "announceKillStreaks": true,
  "killStreakThreshold": 1
}
```

## Troubleshooting Configuration

### Invalid JSON
- Use a JSON validator to check syntax
- Common issues: missing commas, extra commas, unmatched quotes
- Reset to defaults if corrupted

### Settings Not Applied
- Use `/indvspakconfig reload` after manual file edits
- Check server logs for configuration errors
- Verify file permissions

### Performance Impact
- Disable unnecessary features for better performance
- `announceKillStreaks` and `broadcastKills` can generate chat spam
- Large `killStreakThreshold` values reduce announcement frequency

### Backup and Recovery
- Always backup configuration before major changes
- Default configuration is recreated if file is deleted
- Use `/indvspakconfig save` to preserve in-game changes

---

This configuration reference covers all available options in the India vs Pakistan Mod. For additional help, use the in-game help system with `/indvspakhelp` or refer to the main README.md file.

