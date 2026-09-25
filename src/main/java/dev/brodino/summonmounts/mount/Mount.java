package dev.brodino.summonmounts.mount;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.Utils;
import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.network.NetworkManager;
import dev.brodino.summonmounts.network.Packets;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.UUID;

public class Mount implements PositionHelper {

    private final PlayerEntity summoner;
    private final AbstractHorseEntity entity;
    private final ItemStack stack;
    private final Identifier id;

    private int aliveTicks = 0;
    private int idleTicks = 0;
    private int airborneTicks = 0;
    private int lastCountdown = -1;
    private CountdownType lastCountdownType;

    private double repair = 0;
    private boolean airborneRecall = false;

    private Mount(PlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        this.summoner = summoner;
        this.entity = entity;
        this.stack = stack;
        this.id = Registry.ENTITY_TYPE.getId(this.entity.getType());
    }

    public static void fromEntity(PlayerEntity summoner, AbstractHorseEntity entity, ItemStack stack) {
        Mount mount = new Mount(summoner, entity, stack);
        mount.recall(RecallReason.TAMED);
    }

    public static Optional<Mount> fromStack(PlayerEntity player, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(SummonMounts.MOD_ID)) {
            return Optional.empty();
        }
        NbtCompound nbt = nbtCompound.getCompound(SummonMounts.MOD_ID);

        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("type")));
        Entity entity = entityType.create(player.getWorld());
        if (!(entity instanceof AbstractHorseEntity abstractHorseEntity)) {
            return Optional.empty();
        }

        applyNbtToEntity(nbt, abstractHorseEntity);

        return Optional.of(new Mount(player, abstractHorseEntity, stack));
    }

    public void summon() {
        Utils.notifyPlayer(this.summoner, Text.translatable("feedback.summonmounts.summon.manual"));
        this.positionMount(this.entity, this.summoner);
        this.summoner.getWorld().spawnEntity(this.entity);
        Utils.schedule(1, () -> NetworkManager.sendParticlePacket(Packets.SUMMON, (ServerPlayerEntity) this.summoner, Utils.getPlayerParticleColor(this.summoner, this.stack), this));
    }

    public void recall(RecallReason reason) {
        Utils.notifyPlayer(this.summoner, reason.getReason());
        SummonMounts.LOGGER.info(reason.getLog(), this.summoner.getName().getString());
        OcarinaItem.saveMount(this.stack, this);
        NetworkManager.sendForceLandPacket((ServerPlayerEntity) this.summoner, this.entity.getUuid(), false);
        NetworkManager.sendParticlePacket(Packets.RECALL, (ServerPlayerEntity) this.summoner, Utils.getPlayerParticleColor(this.summoner, this.stack), this);
        this.entity.discard();
    }

    private void dropInventory() {
        NbtCompound mountNbt = new NbtCompound();
        this.entity.writeNbt(mountNbt);

        if (mountNbt.contains("SaddleItem")) {
            ItemStack saddle = ItemStack.fromNbt(mountNbt.getCompound("SaddleItem"));
            if (!saddle.isEmpty()) this.entity.dropStack(saddle);
            mountNbt.remove("SaddleItem");
        }

        if (mountNbt.contains("ArmorItem")) {
            ItemStack armor = ItemStack.fromNbt(mountNbt.getCompound("ArmorItem"));
            if (!armor.isEmpty()) this.entity.dropStack(armor);
            mountNbt.remove("ArmorItem");
        }

        if (mountNbt.contains("DecorItem")) {
            ItemStack decor = ItemStack.fromNbt(mountNbt.getCompound("DecorItem"));
            if (!decor.isEmpty()) this.entity.dropStack(decor);
            mountNbt.remove("DecorItem");
        }
    }

    public NbtCompound getSavableNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtCompound mountNbt = new NbtCompound();
        this.entity.writeNbt(mountNbt);

        if (entity.isDead()) {
            this.dropInventory();
        } else {
            if (mountNbt.contains("ArmorItem")) { nbt.put("ArmorItem", mountNbt.get("ArmorItem")); }
            if (mountNbt.contains("SaddleItem")) { nbt.put("SaddleItem", mountNbt.get("SaddleItem")); }
            if (mountNbt.contains("DecorItem")) { nbt.put("DecorItem", mountNbt.get("DecorItem")); }
            if (mountNbt.contains("ActiveEffects")) { nbt.put("ActiveEffects", mountNbt.get("ActiveEffects")); }
        }

        if (mountNbt.contains("Owner")) { nbt.putUuid("Owner", mountNbt.getUuid("Owner")); }
        if (mountNbt.contains("Variant")) { nbt.put("Variant", mountNbt.get("Variant")); }
        if (mountNbt.contains("Tame")) { nbt.putBoolean("Tame", mountNbt.getBoolean("Tame")); }

        nbt.putString("type", this.id.toString());

        return nbt;
    }

    private static void applyNbtToEntity(NbtCompound stackNbt, AbstractHorseEntity entity) {
        NbtCompound mountNbt = new NbtCompound();
        entity.writeNbt(mountNbt);

        if (stackNbt.contains("ArmorItem")) mountNbt.put("ArmorItem", stackNbt.get("ArmorItem"));
        if (stackNbt.contains("SaddleItem")) mountNbt.put("SaddleItem", stackNbt.get("SaddleItem"));
        if (stackNbt.contains("DecorItem")) mountNbt.put("DecorItem", stackNbt.get("DecorItem"));
        if (stackNbt.contains("ActiveEffects")) { mountNbt.put("ActiveEffects", stackNbt.get("ActiveEffects")); }
        if (stackNbt.contains("Variant")) mountNbt.put("Variant", stackNbt.get("Variant"));
        if (stackNbt.contains("Tame")) mountNbt.putBoolean("Tame", stackNbt.getBoolean("Tame"));
        if (stackNbt.contains("Owner")) mountNbt.putUuid("Owner", stackNbt.getUuid("Owner"));

        entity.readNbt(mountNbt);
    }

    public boolean feedMount(double value) {
        int currentDamage = this.stack.getDamage();
        SummonMounts.LOGGER.info("Current damage is {}", currentDamage);
        if (currentDamage == 0) {
            SummonMounts.LOGGER.info("Interrupting feeding because damage is 0");
            return false;
        }

        this.repair += value;

        int repairAmount = (int) this.repair;

        if (repairAmount > currentDamage) {
            repairAmount = currentDamage;
        }

        this.stack.setDamage(currentDamage - repairAmount);
        this.repair -= repairAmount;
        Criteria.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) this.summoner, stack, this.stack.getDamage());
        return true;
    }

    // Getters
    public Vec3d getPos() { return this.entity.getPos(); }
    public double getHeight() { return this.entity.getHeight(); }
    public double getRadius() { return this.entity.getBoundingBox().getAverageSideLength(); }
    public ItemStack getStack() { return this.stack; }
    public PlayerEntity getSummoner() { return this.summoner; }
    public UUID getUuid() { return this.entity.getUuid(); }
    public OcarinaItem getItem() { return (OcarinaItem) this.stack.getItem(); }
    public boolean isMountable() { return this.stack.getMaxDamage() - this.stack.getDamage() > 1; }
    public boolean shouldBeRecalled() { return this.airborneRecall; }
    public int getId() { return this.entity.getId(); }

    public RecallReason tick() {
        if (this.summoner.hasPermissionLevel(2)) {
            return RecallReason.NONE;
        }

        this.aliveTicks++;

        if (this.entity.hasPassengers()) {
            this.idleTicks = 0;
        } else {
            this.idleTicks++;
        }

        if (!this.entity.isOnGround() && !this.entity.isTouchingWater()) {
            this.airborneTicks++;
        }

        this.sendCountdownIfNeeded(this.getLowestCountdown());

        if (this.aliveTicks >= SummonMounts.CONFIG.getMountAliveTicks()) {
            return RecallReason.ALIVE;
        }

        if (this.idleTicks >= SummonMounts.CONFIG.getMountIdleTicks()) {
            return RecallReason.IDLE;
        }

        if (this.airborneTicks >= SummonMounts.CONFIG.getMountAirborneTicks() && !airborneRecall) {
            this.airborneRecall = true;
            NetworkManager.sendForceLandPacket((ServerPlayerEntity) this.summoner, this.entity.getUuid(), true);
        }

        return RecallReason.NONE;
    }

    private Countdown getLowestCountdown() {
        if (this.airborneRecall) {
            return new Countdown(CountdownType.AIRBORNE, 0);
        }

        Countdown countdown = new Countdown(CountdownType.ALIVE, SummonMounts.CONFIG.getMountAliveTicks() - this.aliveTicks);

        if (!this.entity.hasPassengers()) {
            countdown = this.getLowestCountdown(countdown, new Countdown(CountdownType.IDLE, SummonMounts.CONFIG.getMountIdleTicks() - this.idleTicks));
        }

        boolean airborne = !this.entity.isOnGround() && !this.entity.isTouchingWater();
        if (airborne) {
            countdown = this.getLowestCountdown(countdown, new Countdown(CountdownType.AIRBORNE, SummonMounts.CONFIG.getMountAirborneTicks() - this.airborneTicks));
        }

        return countdown;
    }

    private Countdown getLowestCountdown(Countdown first, Countdown second) {
        return second.remainingTicks() < first.remainingTicks() ? second : first;
    }

    private void sendCountdownIfNeeded(Countdown countdown) {
        int remainingTicks = countdown.remainingTicks();
        if (remainingTicks > 30 * 20 || remainingTicks < 0) {
            this.lastCountdown = -1;
            this.lastCountdownType = null;
            return;
        }

        int seconds = (remainingTicks + 19) / 20;

        if (seconds != this.lastCountdown || countdown.type() != this.lastCountdownType) {
            this.summoner.sendMessage(Text.translatable(countdown.type().messageKey, seconds), true);
        }

        this.lastCountdown = seconds;
        this.lastCountdownType = countdown.type();
    }

    private enum CountdownType {
        ALIVE("feedback.summonmounts.countdown.alive"),
        IDLE("feedback.summonmounts.countdown.idle"),
        AIRBORNE("feedback.summonmounts.countdown.airborne");

        private final String messageKey;

        CountdownType(String messageKey) {
            this.messageKey = messageKey;
        }
    }

    private record Countdown(CountdownType type, int remainingTicks) {}
}
