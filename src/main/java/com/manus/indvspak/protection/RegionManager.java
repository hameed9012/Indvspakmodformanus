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

        public ProtectedRegion(String name, TeamManager.Team ownerTeam, BlockPos corner1, BlockPos corner2, String worldId, boolean allowTeammates, boolean allowNeutral) {
            this.name = name;
            this.ownerTeam = ownerTeam;
            // Store corners in a consistent min/max order
            this.corner1 = new BlockPos(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(), corner2.getY()), Math.min(corner1.getZ(), corner2.getZ()));
            this.corner2 = new BlockPos(Math.max(corner1.getX(), corner2.getX()), Math.max(corner1.getY(), corner2.getY()), Math.max(corner1.getZ(), corner2.getZ()));
            this.worldId = worldId;
            this.allowTeammates = allowTeammates;
            this.allowNeutral = allowNeutral;
        }

        // Method to check if a BlockPos is inside this region
        public boolean contains(BlockPos pos) {
            return pos.getX() >= corner1.getX() && pos.getX() <= corner2.getX() &&
                    pos.getY() >= corner1.getY() && pos.getY() <= corner2.getY() &&
                    pos.getZ() >= corner1.getZ() && pos.getZ() <= corner2.getZ();
        }

        // Overloaded method to check BlockPos and World ID
        public boolean contains(BlockPos pos, String worldId) {
            return worldId.equals(this.worldId) && contains(pos);
        }

        public boolean canAccess(TeamManager.Team team) {
            if (team == ownerTeam) {
                return true;
            }
            if (team == TeamManager.Team.NEUTRAL) {
                return allowNeutral;
            }
            return allowTeammates && team == ownerTeam; // This logic might need refinement based on how you define "teammates"
        }

        // Added missing getCenter method
        public BlockPos getCenter() {
            int centerX = (corner1.getX() + corner2.getX()) / 2;
            int centerY = (corner1.getY() + corner2.getY()) / 2;
            int centerZ = (corner1.getZ() + corner2.getZ()) / 2;
            return new BlockPos(centerX, centerY, centerZ);
        }

        // Method to calculate the volume of the region
        public long getVolume() {
            long x = Math.abs(corner1.getX() - corner2.getX()) + 1;
            long y = Math.abs(corner1.getY() - corner2.getY()) + 1;
            long z = Math.abs(corner1.getZ() - corner2.getZ()) + 1;
            return x * y * z;
        }
    }

    private static final Map<String, ProtectedRegion> regions = new HashMap<>();

    public static void loadRegions() {
        if (!Files.exists(REGIONS_PATH)) {
            return;
        }

        try (java.io.Reader reader = Files.newBufferedReader(REGIONS_PATH)) {
            Type type = new TypeToken<Map<String, ProtectedRegion>>() {}.getType();
            Map<String, ProtectedRegion> loadedRegions = GSON.fromJson(reader, type);
            if (loadedRegions != null) {
                regions.clear();
                regions.putAll(loadedRegions);
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to load regions: " + e.getMessage());
        }
    }

    public static void saveRegions() {
        try {
            Files.createDirectories(REGIONS_PATH.getParent());
            try (java.io.Writer writer = Files.newBufferedWriter(REGIONS_PATH)) {
                GSON.toJson(regions, writer);
            }
        } catch (IOException e) {
            System.err.println("[IndiaVsPakistanMod] Failed to save regions: " + e.getMessage());
        }
    }

    // Fixed return type to boolean for RegionCommand compatibility
    public static boolean createRegion(String name, TeamManager.Team ownerTeam, BlockPos corner1, BlockPos corner2, String worldId) {
        if (regions.containsKey(name)) {
            return false; // Region already exists
        }
        regions.put(name, new ProtectedRegion(name, ownerTeam, corner1, corner2, worldId, true, false));
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
            if (region.contains(pos, worldId)) {
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