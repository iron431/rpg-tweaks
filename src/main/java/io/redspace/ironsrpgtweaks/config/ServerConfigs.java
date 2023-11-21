package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.damage_module.DamageServerEvents;
import io.redspace.ironsrpgtweaks.damage_module.PlayerDamageMode;
import io.redspace.ironsrpgtweaks.durability_module.DeathDurabilityMode;
import io.redspace.ironsrpgtweaks.durability_module.VanillaDurabilityMode;
import io.redspace.ironsrpgtweaks.hunger_module.CommonHungerEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ServerConfigs {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DAMAGE_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Integer> IFRAME_COUNT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_ENTITY_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<PlayerDamageMode> PLAYER_DAMAGE_MODE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_NON_FULL_STRENGTH_ATTACKS;
    public static final ForgeConfigSpec.ConfigValue<Double> MINIMUM_ATTACK_STRENGTH;
    public static final ForgeConfigSpec.ConfigValue<Double> KNOCKBACK_MODIFIER;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DURABILITY_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<VanillaDurabilityMode> DURABILITY_VANILLA_MODE;
    public static final ForgeConfigSpec.ConfigValue<DeathDurabilityMode> DURABILITY_DEATH_MODE;
    public static final ForgeConfigSpec.ConfigValue<Double> DURABILITY_LOST_ON_DEATH;
    public static final ForgeConfigSpec.ConfigValue<Integer> ADDITIONAL_DURABILITY_LOST_ON_DEATH;

    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_IGNORE_KEEPINVENTORY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_ONLY_ALLOW_OWNER;
    public static final ForgeConfigSpec.ConfigValue<Double> ENTITY_XP_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<Double> BLOCK_XP_MODIFIER;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ENCHANT_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IDENTIFY_ON_EQUIP;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_ENCHANTING_TABLE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IDENTIFY_ON_ENCHANTING_TABLE;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HUNGER_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HUNGER_DISABLED;
    public static final ForgeConfigSpec.ConfigValue<Double> FOOD_TO_HEALTH_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<Integer> NATURAL_REGENERATION_TICK_RATE;
    public static final ForgeConfigSpec.ConfigValue<Integer> POTION_STACK_SIZE_OVERRIDE;
    public static final ForgeConfigSpec.ConfigValue<Integer> FOOD_STACK_SIZE_OVERRIDE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOOD_STACK_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<Double> SPLASH_POTION_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> LINGERING_POTION_COOLDOWN;


//    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_DROP_REWARD_XP;

    static {

        BUILDER.push("Damage-Module");
        BUILDER.comment("The purpose of the damage module is to remove the invulnerability ticks after an entity is damaged to better suit gameplay where entities are going to be ignoring too much damage if left unchecked. Disabling will nullify every feature listed under this module.");
        DAMAGE_MODULE_ENABLED = BUILDER.define("damageModuleEnabled", true);
        BUILDER.comment("Some entities or damage sources rely on damage ticks to time their attacks. In these cases, we want to let them initiate i-frames.");
        BUILDER.comment("entityBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_ENTITY_TYPES));
        DAMAGE_MODULE_ENTITY_BLACKLIST = BUILDER.defineList("entityBlacklist", DamageServerEvents.BLACKLIST_ENTITY_TYPES, ServerConfigs::validateEntityName);
        BUILDER.comment("damagesourceBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_DAMAGE_SOURCES));
        DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST = BUILDER.defineList("damagesourceBlacklist", DamageServerEvents.BLACKLIST_DAMAGE_SOURCES, (x) -> true);
        BUILDER.comment("Invulnerability Tick (I-Frame) count. Default: 0 (Vanilla's is 20, one second)");
        IFRAME_COUNT = BUILDER.define("invulnerabilityTickCount", 0);
        BUILDER.comment("Specialized handling for player damage ticks. \"ALL\" means there is no special handling, \"ONLY_LIVING\" means only living attacks ignore player i-frames (may help with unforeseen damage like potions), and \"NONE\" means player's damage ticks are unaffected by the damage module.");
        PLAYER_DAMAGE_MODE = BUILDER.defineEnum("playerDamageMode", PlayerDamageMode.ALL);
        BUILDER.comment("In order to prevent spam attacks, a minimum threshold of attack strength can be set before an attack can deal damage. Default: 0.75");
        MINIMUM_ATTACK_STRENGTH = BUILDER.define("minimumAttackStrength", 0.75);
        BUILDER.comment("Whether or not a player is allowed to even swing if the threshold is not met. Default: true");
        ALLOW_NON_FULL_STRENGTH_ATTACKS = BUILDER.worldRestart().define("allowNonFullStrengthAttacks", true);
        BUILDER.comment("Global multiplier to all knockback. Default: 1.0");
        KNOCKBACK_MODIFIER = BUILDER.worldRestart().define("globalKnockbackMultiplier", 1.0);
        BUILDER.pop();

        BUILDER.push("Durability-Module");
        BUILDER.comment("The purpose of the durability module is to rework how durability damage is applied to better emulate an rpg setting.  Disabling will nullify every feature listed under this module.");
        DURABILITY_MODULE_ENABLED = BUILDER.define("durabilityModuleEnabled", true);
        BUILDER.comment("What type of gear should take vanilla durability damage. Default: NONE");
        DURABILITY_VANILLA_MODE = BUILDER.defineEnum("vanillDurabilityGearType", VanillaDurabilityMode.NONE);
        BUILDER.comment("What type of gear is damaged upon death. Default: ALL");
        DURABILITY_DEATH_MODE = BUILDER.defineEnum("deathGearType", DeathDurabilityMode.ALL);
        BUILDER.comment("The percent of durability damage equipment should take on player dying. Set to 0 to disable. Default: 0.15 (15%)");
        DURABILITY_LOST_ON_DEATH = BUILDER.define("durabilityLostOnDeath", 0.15);
        BUILDER.comment("An additional constant amount of damage taken on death. This makes items with a high max durability degrade relatively slower. Set to 0 to disable. Default: 25");
        ADDITIONAL_DURABILITY_LOST_ON_DEATH = BUILDER.define("additionalDurabilityLostOnDeath", 25);

        BUILDER.pop();

        BUILDER.push("XP-Module");
        BUILDER.comment("The purpose of the xp module is to rework how experience is dropped on a player's death by creating a souls-like xp catalyst instead. Disabling will nullify every feature listed under this module.");
        XP_MODULE_ENABLED = BUILDER.define("xpModuleEnabled", true);
        BUILDER.comment("Whether or not players will drop xp despite keepInventory gamerule. Default: true");
        XP_IGNORE_KEEPINVENTORY = BUILDER.define("ignoreKeepInventory", true);
        BUILDER.comment("Whether or not the player who dropped the xp is the only player allow to collect the xp. Default: true");
        XP_ONLY_ALLOW_OWNER = BUILDER.define("onlyAllowOwnerPickup", true);
        BUILDER.comment("Multiplier to experience dropped by slain entities. Default: 1.0");
        ENTITY_XP_MODIFIER = BUILDER.worldRestart().define("mobDropXpMultiplier", 1.0);
        BUILDER.comment("Multiplier to experience dropped by blocks broken. Default: 1.0");
        BLOCK_XP_MODIFIER = BUILDER.worldRestart().define("blockDropXpMultiplier", 1.0);
//        BUILDER.comment("Whether or not \"reward xp\" is dropped, meaning a portion of the killed player's experience points. Useful as a reward for pvp. Default: false");
//        XP_DROP_REWARD_XP = BUILDER.define("dropRewardXp", false);
        BUILDER.pop();

        BUILDER.push("Enchantment-Module");
        BUILDER.comment("The purpose of the enchantment module is to mystify enchantments and add an additional challenge to game by obscuring the description of enchanted and cursed items found through looting. Disabling will nullify every feature listed under this module.");
        ENCHANT_MODULE_ENABLED = BUILDER.define("enchantmentModuleEnabled", true);
        BUILDER.comment("Whether or not armor should be automatically identified when equipped. Default: true");
        IDENTIFY_ON_EQUIP = BUILDER.define("identifyOnEquip", true);
        BUILDER.comment("Whether or not unidentified items can be identified by interacting with an enchanting table. Default: true");
        IDENTIFY_ON_ENCHANTING_TABLE = BUILDER.define("identifyOnEnchantingTable", true);
        BUILDER.comment("Whether or not the enchanting table's functionality should be disabled, making looting or trading the only way to get enchanted items. Default: false");
        DISABLE_ENCHANTING_TABLE = BUILDER.define("disableEnchantingTable", false);
        BUILDER.pop();

        BUILDER.push("Hunger-Module");
        BUILDER.comment("The hunger module removes hunger and makes food to directly heal in order to to remove the tedious task of maintaining hunger, as well as rebalance health management during combat and exploration. Disabling will nullify every feature listed under this module.");
        HUNGER_MODULE_ENABLED = BUILDER.define("hungerModuleEnable", true);
        BUILDER.comment("Disable Hunger. Without this, most of the hunger module features and config are nullified, but if you want to adjust stack sizes or potion mechanics without disabling hunger, you can do so here.");
        HUNGER_DISABLED = BUILDER.define("disableHunger", true);
        BUILDER.comment("The multiplier of a food's hunger value to health regained by eating it. Default: 0.5 (50%)");
        FOOD_TO_HEALTH_MODIFIER = BUILDER.define("foodToHealthModifier", 0.5);
        BUILDER.comment("The amount of time, in ticks, between players naturally regenerating 1 hp. 1 second is 20 ticks. Turn off the naturalRegeneration gamerule to disable. Default: 250.");
        NATURAL_REGENERATION_TICK_RATE = BUILDER.define("naturalRegenerationTickRate", 250);
        BUILDER.comment("Changes the stack size of potions. Set to 0 to disable. Requires game restart. Default: 4");
        POTION_STACK_SIZE_OVERRIDE = BUILDER.define("potionStackSize", 4);
        BUILDER.comment("Limit the stack size of every food item. Set to 0 to disable. Requires game restart. Default: 0");
        FOOD_STACK_SIZE_OVERRIDE = BUILDER.define("foodStackSize", 0);
        BUILDER.comment("A Blacklist for limited food stack size, if enabled. Useful for mob drops or other edible items that are not meant as food. Default: " + getDefaultEntries(CommonHungerEvents.DEFAULT_FOOD_BLACKLIST));
        FOOD_STACK_BLACKLIST = BUILDER.defineList("foodStackSizeBlacklist", CommonHungerEvents.DEFAULT_FOOD_BLACKLIST, ServerConfigs::validateItemName);
        BUILDER.comment("Item Cooldown in seconds when throwing a splash potion. Default: 0.5");
        SPLASH_POTION_COOLDOWN = BUILDER.define("splashPotionCooldown", 0.5);
        BUILDER.comment("Item Cooldown in seconds when throwing a lingering potion. Default: 1.5");
        LINGERING_POTION_COOLDOWN = BUILDER.define("lingeringPotionCooldown", 1.5);
        BUILDER.pop();


        SPEC = BUILDER.build();
    }

    private static boolean validateEntityName(final Object obj) {
        return obj instanceof final String itemName && ResourceLocation.isValidResourceLocation(itemName) && ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(itemName));
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ResourceLocation.isValidResourceLocation(itemName) && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static String getDefaultEntries(List<? extends String> list) {
        String str = "[";
        for (String entry : list)
            str += "\"" + entry + "\",";
        str = str.substring(0, str.length() - 1);
        return str + "]";
    }
}
