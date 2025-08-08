package com.manus.indvspak.protection;

import com.manus.indvspak.TeamManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.TeleportTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;

public class ProtectionEventHandler {
    
    private static final Random random = new Random();

    public static void register() {
        // Prevent block breaking in protected areas
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!canPlayerModifyBlock(serverPlayer, pos, world)) {
                    serverPlayer.sendMessage(
                        Text.literal("You cannot break blocks in this protected area!")
                            .formatted(Formatting.RED), 
                        true
                    );
                    teleportPlayerAway(serverPlayer, pos);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Prevent block placement and interaction in protected areas
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                BlockPos pos = hitResult.getBlockPos();
                if (!canPlayerModifyBlock(serverPlayer, pos, world)) {
                    serverPlayer.sendMessage(
                        Text.literal("You cannot interact with blocks in this protected area!")
                            .formatted(Formatting.RED), 
                        true
                    );
                    teleportPlayerAway(serverPlayer, pos);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Prevent item usage in protected areas (like placing blocks)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                BlockPos pos = serverPlayer.getBlockPos();
                if (!canPlayerModifyBlock(serverPlayer, pos, world)) {
                    // Only block if the item can place blocks or modify the world
                    if (serverPlayer.getStackInHand(hand).getItem().toString().contains("block") ||
                        serverPlayer.getStackInHand(hand).getItem().toString().contains("bucket")) {
                        serverPlayer.sendMessage(
                            Text.literal("You cannot use this item in this protected area!")
                                .formatted(Formatting.RED), 
                            true
                        );
                        teleportPlayerAway(serverPlayer, pos);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Prevent attacking other players in their own team\"s protected areas
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity attacker && entity instanceof ServerPlayerEntity target) {
                TeamManager.Team attackerTeam = TeamManager.getPlayerTeam(attacker.getUuid());
                TeamManager.Team targetTeam = TeamManager.getPlayerTeam(target.getUuid());
                
                // Check if target is in their own team\"s protected area
                BlockPos pos = target.getBlockPos();
                String worldId = world.getRegistryKey().getValue().toString();
                List<RegionManager.ProtectedRegion> regions = RegionManager.getRegionsAt(pos, worldId);
                
                for (RegionManager.ProtectedRegion region : regions) {
                    if (region.ownerTeam == targetTeam && attackerTeam != targetTeam) {
                        attacker.sendMessage(
                            Text.literal("You cannot attack players in their team\"s protected area!")
                                .formatted(Formatting.RED), 
                            true
                        );
                        teleportPlayerAway(attacker, pos);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkPlayerTerritory(player);
            }
        });
    }

    private static void checkPlayerTerritory(ServerPlayerEntity player) {
        if (player.isSpectator() || player.isCreative()) return; // Don't check for spectators or creative players

        BlockPos playerPos = player.getBlockPos();
        String worldId = player.getWorld().getRegistryKey().getValue().toString();
        TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());

        for (RegionManager.ProtectedRegion region : RegionManager.getAllRegions().values()) {
            if (region.worldId.equals(worldId) && region.ownerTeam != playerTeam) {
                // This is an opposing team's territory
                BlockPos min = new BlockPos(Math.min(region.corner1.getX(), region.corner2.getX()), Math.min(region.corner1.getY(), region.corner2.getY()), Math.min(region.corner1.getZ(), region.corner2.getZ()));
                BlockPos max = new BlockPos(Math.max(region.corner1.getX(), region.corner2.getX()), Math.max(region.corner1.getY(), region.corner2.getY()), Math.max(region.corner1.getZ(), region.corner2.getZ()));

                // Check if player is within 10 blocks of the territory
                if (isWithinDistance(playerPos, min, max, 10)) {
                    if (!region.contains(playerPos)) {
                        // Player is within warning distance but not inside
                        player.sendMessage(Text.literal("WARNING: You are approaching " + region.ownerTeam.getDisplayName() + " territory! Turn back now!").formatted(Formatting.YELLOW), true);
                    } else {
                        // Player is inside the territory
                        player.sendMessage(Text.literal("You have entered " + region.ownerTeam.getDisplayName() + " territory! You will be eliminated!").formatted(Formatting.RED), true);
                        player.damage(player.getWorld(), player.getDamageSources().generic(), 1000.0f); // Kill the player
                    }
                }
            }
        }
    }

    private static boolean isWithinDistance(BlockPos playerPos, BlockPos min, BlockPos max, int distance) {
        int px = playerPos.getX();
        int py = playerPos.getY();
        int pz = playerPos.getZ();

        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
        int maxX = max.getX();
        int maxY = max.getY();
        int maxZ = max.getZ();

        // Check if player is within the bounding box plus the distance buffer
        return px >= minX - distance && px <= maxX + distance &&
               py >= minY - distance && py <= maxY + distance &&
               pz >= minZ - distance && pz <= maxZ + distance;
    }

    private static boolean canPlayerModifyBlock(ServerPlayerEntity player, BlockPos pos, World world) {
        TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
        String worldId = world.getRegistryKey().getValue().toString();
        
        return RegionManager.canPlayerAccess(pos, worldId, playerTeam);
    }

    public static boolean isInProtectedArea(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String worldId = player.getWorld().getRegistryKey().getValue().toString();
        List<RegionManager.ProtectedRegion> regions = RegionManager.getRegionsAt(pos, worldId);
        return !regions.isEmpty();
    }

    public static RegionManager.ProtectedRegion getProtectedRegionAt(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String worldId = player.getWorld().getRegistryKey().getValue().toString();
        List<RegionManager.ProtectedRegion> regions = RegionManager.getRegionsAt(pos, worldId);
        return regions.isEmpty() ? null : regions.get(0);
    }

    private static void teleportPlayerAway(ServerPlayerEntity player, BlockPos protectedPos) {
        // Instead of teleporting, apply damage and knockback to discourage entering
        // Apply damage to the player
        player.damage(player.getWorld(), player.getDamageSources().generic(), 2.0f);
        
        // Apply knockback effect away from the protected area
        double knockbackX = player.getX() - protectedPos.getX();
        double knockbackZ = player.getZ() - protectedPos.getZ();
        double distance = Math.sqrt(knockbackX * knockbackX + knockbackZ * knockbackZ);
        
        if (distance > 0) {
            knockbackX = (knockbackX / distance) * 1.5; // Knockback strength
            knockbackZ = (knockbackZ / distance) * 1.5;
            
            // Apply velocity to push player away
            player.setVelocity(knockbackX, 0.3, knockbackZ);
            player.velocityModified = true;
        }
        
        player.sendMessage(Text.literal("You are being pushed away from the protected area!").formatted(Formatting.RED), true);
    }
}
