package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.damage_module.DamageServerEvents;
import io.redspace.ironsrpgtweaks.damage_module.PlayerDamageMode;
import io.redspace.ironsrpgtweaks.durability_module.DeathDurabilityMode;
import io.redspace.ironsrpgtweaks.durability_module.VanillaDurabilityMode;
import io.redspace.ironsrpgtweaks.hunger_module.CommonHungerEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DURABILITY_VANILLA_MODE_WHITELIST; //private so the cache must be used
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DURABILITY_VANILLA_MODE_BLACKLIST; //private so the cache must be used
    public static final ForgeConfigSpec.ConfigValue<DeathDurabilityMode> DURABILITY_DEATH_MODE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DURABILITY_DEATH_MODE_WHITELIST; //private so the cache must be used
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DURABILITY_DEATH_MODE_BLACKLIST; //private so the cache must be used
    public static final ForgeConfigSpec.ConfigValue<Double> DURABILITY_LOST_ON_DEATH;
    public static final ForgeConfigSpec.ConfigValue<Integer> ADDITIONAL_DURABILITY_LOST_ON_DEATH;
    public static final Set<Item> DURABILITY_VANILLA_MODE_WHITELIST_ITEMS = new HashSet<>();
    public static final Set<Item> DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS = new HashSet<>();
    public static final Set<Item> DURABILITY_DEATH_MODE_WHITELIST_ITEMS = new HashSet<>();
    public static final Set<Item> DURABILITY_DEATH_MODE_BLACKLIST_ITEMS = new HashSet<>();

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
    public static final ForgeConfigSpec.ConfigValue<Boolean> NATURAL_REGENERATION_DURING_COMBAT;
    public static final ForgeConfigSpec.ConfigValue<Integer> POTION_STACK_SIZE_OVERRIDE;
    public static final ForgeConfigSpec.ConfigValue<Integer> FOOD_STACK_SIZE_OVERRIDE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOOD_STACK_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<Double> SPLASH_POTION_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> LINGERING_POTION_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<Double> EAT_TIME_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Double> POTION_DRINK_TIME_MULTIPLER;


//    public static final ForgeConfigSpec.ConfigValue<Boolean> XP_DROP_REWARD_XP;

    static {
        /*
        Damage Module
         */
        BUILDER.push("Damage-Module");
        DAMAGE_MODULE_ENABLED = BUILDER
                .comment("The purpose of the damage module is to remove the invulnerability ticks after an entity is damaged to better suit gameplay where entities are going to be ignoring too much damage if left unchecked. Disabling will nullify every feature listed under this module.")
                .define("damageModuleEnabled", true);
        DAMAGE_MODULE_ENTITY_BLACKLIST = BUILDER
                .comment("Some entities or damage sources rely on damage ticks to time their attacks. In these cases, we want to let them initiate i-frames.")
                .comment("entityBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_ENTITY_TYPES))
                .defineList("entityBlacklist", DamageServerEvents.BLACKLIST_ENTITY_TYPES, ServerConfigs::validateEntityName);
        DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST = BUILDER
                .comment("damagesourceBlacklist default: " + getDefaultEntries(DamageServerEvents.BLACKLIST_DAMAGE_SOURCES))
                .defineList("damagesourceBlacklist", DamageServerEvents.BLACKLIST_DAMAGE_SOURCES, (x) -> true);
        IFRAME_COUNT = BUILDER
                .comment("Invulnerability Tick (I-Frame) count. Default: 0 (Vanilla's is 20, one second)")
                .define("invulnerabilityTickCount", 0);
        PLAYER_DAMAGE_MODE = BUILDER
                .comment("Specialized handling for player damage ticks. \"ALL\" means there is no special handling, \"ONLY_LIVING\" means only living attacks ignore player i-frames (may help with unforeseen damage like potions), and \"NONE\" means player's damage ticks are unaffected by the damage module.")
                .defineEnum("playerDamageMode", PlayerDamageMode.ALL);
        MINIMUM_ATTACK_STRENGTH = BUILDER
                .comment("In order to prevent spam attacks, a minimum threshold of attack strength can be set before an attack can deal damage. Default: 0.75")
                .define("minimumAttackStrength", 0.75);
        ALLOW_NON_FULL_STRENGTH_ATTACKS = BUILDER
                .comment("Whether a player is allowed to even swing if the threshold is not met. Default: true")
                .worldRestart().define("allowNonFullStrengthAttacks", true);
        KNOCKBACK_MODIFIER = BUILDER
                .comment("Global multiplier to all knockback. Default: 1.0")
                .worldRestart().define("globalKnockbackMultiplier", 1.0);
        BUILDER.pop();

        /*
        Durability Module
         */
        BUILDER.push("Durability-Module");
        DURABILITY_MODULE_ENABLED = BUILDER
                .comment("The purpose of the durability module is to rework how durability damage is applied to better emulate an rpg setting.  Disabling will nullify every feature listed under this module.")
                .define("durabilityModuleEnabled", true);
        DURABILITY_VANILLA_MODE = BUILDER
                .comment("What type of gear should take vanilla durability damage. Default: NONE")
                .defineEnum("vanillaDurabilityGearType", VanillaDurabilityMode.NONE);
        BUILDER.push("Vanilla-Mode-Item-Configs");
        DURABILITY_VANILLA_MODE_WHITELIST = BUILDER
                .comment("(VANILLA MODE CANNOT BE NONE) If specified, the only items or item tags to take vanilla durability damage")
                .defineList("vanillaDurabilityWhitelist", List.of(), x -> true);
        DURABILITY_VANILLA_MODE_BLACKLIST = BUILDER
                .comment("If specified, these items or item tags never take vanilla durability damage")
                .defineList("vanillaDurabilityBlacklist", List.of(), x -> true);
        BUILDER.pop();
        DURABILITY_DEATH_MODE = BUILDER
                .comment("What type of gear is damaged upon death. Default: ALL")
                .defineEnum("deathGearType", DeathDurabilityMode.ALL);
        BUILDER.push("Death-Mode-Item-Configs");
        DURABILITY_DEATH_MODE_WHITELIST = BUILDER
                .comment("(DEATH MODE CANNOT BE NONE) If specified, the only items or item tags to take durability damage on death").
                defineList("deathDurabilityWhitelist", List.of(), x -> true);
        DURABILITY_DEATH_MODE_BLACKLIST = BUILDER
                .comment("If specified, these items or item tags never take durability damage on death")
                .defineList("deathDurabilityBlacklist", List.of(), x -> true);
        BUILDER.pop();
        DURABILITY_LOST_ON_DEATH = BUILDER.
                comment("The percent of durability damage equipment should take on player dying. Set to 0 to disable. Default: 0.15 (15%)")
                .define("durabilityLostOnDeath", 0.15);
        ADDITIONAL_DURABILITY_LOST_ON_DEATH = BUILDER
                .comment("An additional constant amount of damage taken on death. This makes items with a high max durability degrade relatively slower. Set to 0 to disable. Default: 25")
                .define("additionalDurabilityLostOnDeath", 25);
        BUILDER.pop();

        /*
        XP Module
         */
        BUILDER.push("XP-Module");
        XP_MODULE_ENABLED = BUILDER.
                comment("The purpose of the xp module is to rework how experience is dropped on a player's death by creating a souls-like xp catalyst instead. Disabling will nullify every feature listed under this module.")
                .define("xpModuleEnabled", true);
        XP_IGNORE_KEEPINVENTORY = BUILDER
                .comment("Whether or not players will drop xp despite keepInventory gamerule. Default: true")
                .define("ignoreKeepInventory", true);
        XP_ONLY_ALLOW_OWNER = BUILDER
                .comment("Whether or not the player who dropped the xp is the only player allow to collect the xp. Default: true")
                .define("onlyAllowOwnerPickup", true);
        ENTITY_XP_MODIFIER = BUILDER
                .comment("Multiplier to experience dropped by slain entities. Default: 1.0")
                .worldRestart().define("mobDropXpMultiplier", 1.0);
        BLOCK_XP_MODIFIER = BUILDER
                .comment("Multiplier to experience dropped by blocks broken. Default: 1.0")
                .worldRestart().define("blockDropXpMultiplier", 1.0);
        BUILDER.pop();

        /*
        Enchantment Module
         */
        BUILDER.push("Enchantment-Module");
        ENCHANT_MODULE_ENABLED = BUILDER
                .comment("The purpose of the enchantment module is to mystify enchantments and add an additional challenge to game by obscuring the description of enchanted and cursed items found through looting. Disabling will nullify every feature listed under this module.")
                .define("enchantmentModuleEnabled", true);
        IDENTIFY_ON_EQUIP = BUILDER
                .comment("Whether or not armor should be automatically identified when equipped. Default: true")
                .define("identifyOnEquip", true);
        IDENTIFY_ON_ENCHANTING_TABLE = BUILDER
                .comment("Whether or not unidentified items can be identified by interacting with an enchanting table. Default: true")
                .define("identifyOnEnchantingTable", true);
        DISABLE_ENCHANTING_TABLE = BUILDER
                .comment("Whether or not the enchanting table's functionality should be disabled, making looting or trading the only way to get enchanted items. Default: false")
                .define("disableEnchantingTable", false);
        BUILDER.pop();

        /*
        Hunger Module
         */
        BUILDER.push("Hunger-Module");
        HUNGER_MODULE_ENABLED = BUILDER.
                comment("The hunger module removes hunger and makes food to directly heal in order to to remove the tedious task of maintaining hunger, as well as rebalance health management during combat and exploration. Disabling will nullify every feature listed under this module.")
                .define("hungerModuleEnable", true);
        HUNGER_DISABLED = BUILDER
                .comment("Disable Hunger. Without this, most of the hunger module features and config are nullified, but if you want to adjust stack sizes or potion mechanics without disabling hunger, you can do so here.")
                .define("disableHunger", true);
        FOOD_TO_HEALTH_MODIFIER = BUILDER
                .comment("The multiplier of a food's hunger value to health regained by eating it. Default: 0.5 (50%)")
                .define("foodToHealthModifier", 0.5);
        NATURAL_REGENERATION_TICK_RATE = BUILDER.
                comment("The amount of time, in ticks, between players naturally regenerating 1 hp. 1 second is 20 ticks. Turn off the naturalRegeneration gamerule to disable. Default: 250.")
                .define("naturalRegenerationTickRate", 250);
        NATURAL_REGENERATION_DURING_COMBAT = BUILDER
                .comment("Whether players should naturally regenerate hp during combat. (Turn off the naturalRegeneration gamerule to disable all natural regen). Default: false.")
                .define("naturalRegenerationDuringCombat", false);
        POTION_STACK_SIZE_OVERRIDE = BUILDER
                .comment("Changes the stack size of potions. Set to 0 to disable. Requires game restart. Default: 4")
                .define("potionStackSize", 4);
        FOOD_STACK_SIZE_OVERRIDE = BUILDER
                .comment("Limit the stack size of every food item. Set to 0 to disable. Requires game restart. Default: 0")
                .define("foodStackSize", 0);
        FOOD_STACK_BLACKLIST = BUILDER
                .comment("A Blacklist for limited food stack size, if enabled. Useful for mob drops or other edible items that are not meant as food. Default: " + getDefaultEntries(CommonHungerEvents.DEFAULT_FOOD_BLACKLIST))
                .defineList("foodStackSizeBlacklist", CommonHungerEvents.DEFAULT_FOOD_BLACKLIST, ServerConfigs::validateItemName);
        SPLASH_POTION_COOLDOWN = BUILDER
                .comment("Item Cooldown in seconds when throwing a splash potion. Default: 0.5")
                .define("splashPotionCooldown", 0.5);
        LINGERING_POTION_COOLDOWN = BUILDER
                .comment("Item Cooldown in seconds when throwing a lingering potion. Default: 1.5")
                .define("lingeringPotionCooldown", 1.5);
        EAT_TIME_MULTIPLIER = BUILDER
                .comment("Multiplier to the time taken to eat food. Default: 1.2")
                .define("eatTimeMultiplier", 1.2);
        POTION_DRINK_TIME_MULTIPLER = BUILDER
                .comment("Multiplier to the time taken to drink potions. Default: 0.8")
                .define("potionDrinkTimeMultiplier", 0.8);
        BUILDER.pop();


        SPEC = BUILDER.build();
    }

    public static void handleOnConfigReload() {
        IronsRpgTweaks.LOGGER.debug("On Config Reload");

        /*
        Cache whitelists/blacklists into items
         */
        cacheItemList(DURABILITY_VANILLA_MODE_WHITELIST.get(), DURABILITY_VANILLA_MODE_WHITELIST_ITEMS);
        cacheItemList(DURABILITY_VANILLA_MODE_BLACKLIST.get(), DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS);
        cacheItemList(DURABILITY_DEATH_MODE_WHITELIST.get(), DURABILITY_DEATH_MODE_WHITELIST_ITEMS);
        cacheItemList(DURABILITY_DEATH_MODE_BLACKLIST.get(), DURABILITY_DEATH_MODE_BLACKLIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_VANILLA_MODE_WHITELIST: {} {}", DURABILITY_VANILLA_MODE_WHITELIST.get(), DURABILITY_VANILLA_MODE_WHITELIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_VANILLA_MODE_BLACKLIST: {} {}", DURABILITY_VANILLA_MODE_BLACKLIST.get(), DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_DEATH_MODE_WHITELIST: {} {}", DURABILITY_DEATH_MODE_WHITELIST.get(), DURABILITY_DEATH_MODE_WHITELIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_DEATH_MODE_BLACKLIST: {} {}", DURABILITY_DEATH_MODE_BLACKLIST.get(), DURABILITY_DEATH_MODE_BLACKLIST_ITEMS);

    }

    private static void cacheItemList(List<? extends String> ids, Set<Item> output) {
        output.clear();
        for (String name : ids) {
            try {
                if (name.startsWith("#")) {
                    var tag = new TagKey<Item>(Registries.ITEM, new ResourceLocation(name.substring(1)));
                    output.addAll(ForgeRegistries.ITEMS.getValues().stream().filter(item -> item.builtInRegistryHolder().is(tag)).toList());
                } else {
                    var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
                    if (item != null) {
                        output.add(item);
                    } else {
                        IronsRpgTweaks.LOGGER.warn("Unable to add item to config, no such item id: {}", name);
                    }
                }
            } catch (Exception e) {
                IronsRpgTweaks.LOGGER.warn("Unable to validate item config: {}", e.getMessage());
            }
        }
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
