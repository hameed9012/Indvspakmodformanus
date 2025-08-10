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
import com.manus.indvspak.config.ModConfig;

import java.util.List;
import java.util.Random;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;

public class ProtectionEventHandler {

    private static final long TELEPORT_DELAY = 20; // 1 second
    private static final Random random = new Random();

    public static void register() {
        // Prevent block breaking in protected regions
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!ModConfig.getInstance().isProtectionEnabled()) {
                return ActionResult.PASS;
            }
            if (world.isClient || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
            if (!RegionManager.canPlayerAccess(pos, world.getRegistryKey().getValue().toString(), playerTeam)) {
                player.sendMessage(Text.literal("This area is protected!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Prevent interaction with blocks in protected regions
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!ModConfig.getInstance().isProtectionEnabled()) {
                return ActionResult.PASS;
            }
            if (world.isClient || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
            BlockPos pos = hitResult.getBlockPos();
            if (!RegionManager.canPlayerAccess(pos, world.getRegistryKey().getValue().toString(), playerTeam)) {
                player.sendMessage(Text.literal("This area is protected!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Prevent interaction with entities (e.g. villagers, item frames)
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!ModConfig.getInstance().isProtectionEnabled()) {
                return ActionResult.PASS;
            }
            if (world.isClient || !(player instanceof ServerPlayerEntity)) {
                return ActionResult.PASS;
            }

            TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
            BlockPos pos = entity.getBlockPos();
            if (!RegionManager.canPlayerAccess(pos, world.getRegistryKey().getValue().toString(), playerTeam)) {
                player.sendMessage(Text.literal("This area is protected!").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // Periodically check for players inside protected regions they don't have access to
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!ModConfig.getInstance().isProtectionEnabled()) {
                return;
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isCreative() || player.isSpectator()) {
                    continue; // Do not push creative or spectator players
                }

                TeamManager.Team playerTeam = TeamManager.getPlayerTeam(player.getUuid());
                RegionManager.ProtectedRegion region = getProtectedRegionAt(player);
                if (region != null && !region.canAccess(playerTeam)) {
                    // Check if player is a teammate and if protection is enabled for teammates
                    if (region.ownerTeam == playerTeam && ModConfig.getInstance().shouldProtectFromTeammates()) {
                        pushPlayerAway(player, region.getCenter());
                    } else if (region.ownerTeam != playerTeam) {
                        pushPlayerAway(player, region.getCenter());
                    }
                }
            }
        });
    }

    public static RegionManager.ProtectedRegion getProtectedRegionAt(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String worldId = player.getWorld().getRegistryKey().getValue().toString();
        List<RegionManager.ProtectedRegion> regions = RegionManager.getRegionsAt(pos, worldId);
        return regions.isEmpty() ? null : regions.get(0);
    }

    // Fixed damage method call and renamed for clarity
    private static void pushPlayerAway(ServerPlayerEntity player, BlockPos protectedPos) {
        // Apply damage to the player - Fixed method signature to include ServerWorld parameter
        ServerWorld serverWorld = (ServerWorld) player.getWorld();
        DamageSource damageSource = player.getDamageSources().generic();
        player.damage(serverWorld, damageSource, 2.0f);

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

        player.sendMessage(Text.literal("You are being pushed away from the protected area!").formatted(Formatting.RED), false);
    }
}