package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.damage_module.DamageServerEvents;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ServerConfigs {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DAMAGE_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_ENTITY_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_NON_FULL_STRENGTH_ATTACKS;
    public static final ForgeConfigSpec.ConfigValue<Double> MINIMUM_ATTACK_STRENGTH;
    public static final ForgeConfigSpec.ConfigValue<Double> KNOCKBACK_MODIFIER;

    public static final ForgeConfigSpec.ConfigValue<Boolean> DURABILITY_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TAKE_DURABILITY_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Double> DURABILITY_LOST_ON_DEATH;

    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_RESPECT_KEEPINVENTORY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_ONLY_ALLOW_OWNER;
    public static final ForgeConfigSpec.ConfigValue<Double> XP_MODIFIER;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ENCHANT_MODULE_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IDENTIFY_ON_EQUIP;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISABLE_ENCHANTING_TABLE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> IDENTIFY_ON_ENCHANTING_TABLE;


//    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_DROP_REWARD_XP;

    static {

        BUILDER.push("Damage-Module");
        BUILDER.comment("The purpose of the damage module is to remove the invulnerability ticks after an entity is damaged to better suit gameplay where entities are going to be ignoring too much damage if left unchecked. Disabling will nullify every feature listed under this module.");
        DAMAGE_MODULE_ENABLED = BUILDER.define("damageModuleEnabled", true);
        BUILDER.comment("Some entities or damage sources rely on damage ticks to time their attacks. In these cases, we want to let them initiate i-frames.");
        BUILDER.comment("entityBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_ENTITY_TYPES));
        DAMAGE_MODULE_ENTITY_BLACKLIST = BUILDER.defineList("entityBlacklist", DamageServerEvents.BLACKLIST_ENTITY_TYPES, (x) -> true);
        BUILDER.comment("damagesourceBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_DAMAGE_SOURCES));
        DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST = BUILDER.defineList("damagesourceBlacklist", DamageServerEvents.BLACKLIST_DAMAGE_SOURCES, (x) -> true);
        BUILDER.comment("In order to prevent spam attacks, a minimum threshold of attack strength can be set before an attack can deal damage. Default: 0.75");
        MINIMUM_ATTACK_STRENGTH = BUILDER.define("minimumAttackStrength", 0.75);
        BUILDER.comment("Whether or not a player is allowed to even swing if the threshold is not met. Default: false");
        ALLOW_NON_FULL_STRENGTH_ATTACKS = BUILDER.worldRestart() .define("allowNonFullStrengthAttacks", false);
        BUILDER.comment("Global multiplier to all knockback. Default: 1.0");
        KNOCKBACK_MODIFIER = BUILDER.worldRestart() .define("globalKnockbackMultiplier", 1.0);
        BUILDER.pop();

        BUILDER.push("Durability-Module");
        BUILDER.comment("The purpose of the durability module is to rework how durability damage is applied to better emulate an rpg setting.  Disabling will nullify every feature listed under this module.");
        DURABILITY_MODULE_ENABLED = BUILDER.define("durabilityModuleEnabled", true);
        BUILDER.comment("Whether or not tools and armor should take regular durability damage while in use. Default: false");
        TAKE_DURABILITY_DAMAGE = BUILDER.define("takeDurabilityDamage", false);
        BUILDER.comment("The percent of durability damage equipment should take on player dying. Set to 0 to disable. Default: 0.15 (15%)");
        DURABILITY_LOST_ON_DEATH = BUILDER.define("durabilityLostOnDeath", 0.15);
        BUILDER.pop();

        BUILDER.push("XP-Module");
        BUILDER.comment("The purpose of the xp module is to rework how experience is dropped on a player's death by creating a souls-like xp catalyst instead. Disabling will nullify every feature listed under this module.");
        XP_MODULE_ENABLED = BUILDER.define("xpModuleEnabled", true);
        BUILDER.comment("Whether or not the keepInventory gamerule will prevent the player from losing xp Default: false, meaning even though you keep your items, your xp is still dropped.");
        XP_RESPECT_KEEPINVENTORY = BUILDER.define("respectKeepInventory", false);
        BUILDER.comment("Whether or not the player who dropped the xp is the only player allow to collect the xp. Default: true");
        XP_ONLY_ALLOW_OWNER = BUILDER.define("onlyAllowOwnerPickup", true);
        BUILDER.comment("Global multiplier to experience amount gained. Default: 1.0");
        XP_MODIFIER = BUILDER.worldRestart() .define("globalXpMultiplier", 1.0);
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

        SPEC = BUILDER.build();
    }

    private static String getDefaultEntries(List<? extends String> list) {
        String str = "[";
        for (String entry : list)
            str += "\"" + entry + "\",";
        str = str.substring(0, str.length() - 1);
        return str + "]";
    }
}
