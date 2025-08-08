# GeoLite2 Database Setup Instructions

## Overview
This mod uses MaxMind's GeoLite2 Country database for IP-based country detection. The database file is required for the mod to function properly.

## Steps to Set Up GeoLite2 Database

### 1. Create MaxMind Account
1. Go to https://www.maxmind.com/en/geolite2/signup
2. Create a free account
3. Verify your email address

### 2. Generate License Key
1. Log in to your MaxMind account
2. Go to "My License Key" section
3. Generate a new license key
4. Save the license key securely

### 3. Download GeoLite2-Country Database
1. Go to https://www.maxmind.com/en/accounts/current/geoip/downloads
2. Download "GeoLite2 Country" in Binary format (.mmdb)
3. Extract the .tar.gz file
4. Locate the `GeoLite2-Country.mmdb` file

### 4. Install Database in Mod
1. Copy `GeoLite2-Country.mmdb` to `src/main/resources/assets/indiavspakistanmod/`
2. The mod will automatically load the database on startup

### Alternative: Automatic Download (Advanced)
You can modify the mod to automatically download the database on first run:
```java
// Add this to TeamManager.initialize() method
String licenseKey = "YOUR_LICENSE_KEY_HERE";
String downloadUrl = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key=" + licenseKey + "&suffix=tar.gz";
// Implement download and extraction logic
```

## Important Notes
- The GeoLite2 database is updated monthly by MaxMind
- For production use, consider implementing automatic updates
- The database file is approximately 6MB in size
- IP geolocation is inherently imprecise and should not be used for critical decisions

## Fallback Behavior
If the GeoLite2 database is not available:
- All players will be assigned to the "Neutral" team
- Players can manually switch teams using in-game commands
- The mod will log an error message but continue to function

