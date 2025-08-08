package com.manus.indvspak.protection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.manus.indvspak.TeamManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {
    private static final String REGIONS_FILE = "indvspak_regions.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path REGIONS_PATH = FabricLoader.getInstance().getConfigDir().resolve(REGIONS_FILE);

    public static class ProtectedRegion {
        public String name;
        public TeamManager.Team ownerTeam;
        public BlockPos corner1;
        public BlockPos corner2;
        public String worldId;
        public boolean allowTeammates;
        public boolean allowNeutral;

        public ProtectedRegion() {}

        public ProtectedRegion(String name, TeamManager.Team ownerTeam, BlockPos corner1, BlockPos corner2, String worldId) {
            this.name = name;
            this.ownerTeam = ownerTeam;
            this.corner1 = corner1;
            this.corner2 = corner2;
            this.worldId = worldId;
            this.allowTeammates = true;
            this.allowNeutral = false;
        }

        public boolean contains(BlockPos pos) {
            int minX = Math.min(corner1.getX(), corner2.getX());
            int maxX = Math.max(corner1.getX(), corner2.getX());
            int minY = Math.min(corner1.getY(), corner2.getY());
            int maxY = Math.max(corner1.getY(), corner2.getY());
            int minZ = Math.min(corner1.getZ(), corner2.getZ());
            int maxZ = Math.max(corner1.getZ(), corner2.getZ());

            return pos.getX() >= minX && pos.getX() <= maxX &&
                   pos.getY() >= minY && pos.getY() <= maxY &&
                   pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }

        public boolean canAccess(TeamManager.Team playerTeam) {
            if (playerTeam == ownerTeam) {
                return true;
            }
            if (playerTeam == TeamManager.Team.NEUTRAL && allowNeutral) {
                return true;
            }
            if (playerTeam != TeamManager.Team.NEUTRAL && playerTeam != ownerTeam && allowTeammates) {
                return false; // Different team, not allowed
            }
            return allowTeammates && playerTeam == ownerTeam;
        }

        public int getVolume() {
            int width = Math.abs(corner2.getX() - corner1.getX()) + 1;
            int height = Math.abs(corner2.getY() - corner1.getY()) + 1;
            int depth = Math.abs(corner2.getZ() - corner1.getZ()) + 1;
            return width * height * depth;
        }
    }

    private static Map<String, ProtectedRegion> regions = new HashMap<>();

    public static void loadRegions() {
        try {
            if (Files.exists(REGIONS_PATH)) {
                String json = Files.readString(REGIONS_PATH);
                Type type = new TypeToken<Map<String, ProtectedRegion>>(){}.getType();
                Map<String, ProtectedRegion> loadedRegions = GSON.fromJson(json, type);
                
                if (loadedRegions != null) {
                    regions = loadedRegions;
                    System.out.println("[IndiaVsPakistanMod] Loaded " + regions.size() + " protected regions");
                }
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to load regions: " + e.getMessage());
        }
    }

    public static void saveRegions() {
        try {
            Files.createDirectories(REGIONS_PATH.getParent());
            String json = GSON.toJson(regions);
            Files.writeString(REGIONS_PATH, json);
            System.out.println("[IndiaVsPakistanMod] Saved " + regions.size() + " protected regions");
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to save regions: " + e.getMessage());
        }
    }

    public static boolean createRegion(String name, TeamManager.Team ownerTeam, BlockPos corner1, BlockPos corner2, String worldId) {
        if (regions.containsKey(name)) {
            return false; // Region already exists
        }

        ProtectedRegion region = new ProtectedRegion(name, ownerTeam, corner1, corner2, worldId);
        regions.put(name, region);
        saveRegions();
        return true;
    }

    public static boolean deleteRegion(String name) {
        if (regions.remove(name) != null) {
            saveRegions();
            return true;
        }
        return false;
    }

    public static ProtectedRegion getRegion(String name) {
        return regions.get(name);
    }

    public static List<ProtectedRegion> getRegionsAt(BlockPos pos, String worldId) {
        List<ProtectedRegion> result = new ArrayList<>();
        for (ProtectedRegion region : regions.values()) {
            if (region.worldId.equals(worldId) && region.contains(pos)) {
                result.add(region);
            }
        }
        return result;
    }

    public static List<ProtectedRegion> getTeamRegions(TeamManager.Team team) {
        List<ProtectedRegion> result = new ArrayList<>();
        for (ProtectedRegion region : regions.values()) {
            if (region.ownerTeam == team) {
                result.add(region);
            }
        }
        return result;
    }

    public static boolean canPlayerAccess(BlockPos pos, String worldId, TeamManager.Team playerTeam) {
        List<ProtectedRegion> regionsAtPos = getRegionsAt(pos, worldId);
        
        if (regionsAtPos.isEmpty()) {
            return true; // No protection, allow access
        }

        // Check if player can access any of the regions at this position
        for (ProtectedRegion region : regionsAtPos) {
            if (region.canAccess(playerTeam)) {
                return true;
            }
        }

        return false; // Player cannot access any region at this position
    }

    public static Map<String, ProtectedRegion> getAllRegions() {
        return new HashMap<>(regions);
    }

    public static void setRegionPermissions(String regionName, boolean allowTeammates, boolean allowNeutral) {
        ProtectedRegion region = regions.get(regionName);
        if (region != null) {
            region.allowTeammates = allowTeammates;
            region.allowNeutral = allowNeutral;
            saveRegions();
        }
    }
}

