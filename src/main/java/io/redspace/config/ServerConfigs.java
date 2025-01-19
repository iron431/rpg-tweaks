package io.redspace.config;

import io.redspace.IronsRpgTweaks;
import io.redspace.damage_module.PlayerDamageMode;
import io.redspace.durability_module.DeathDurabilityMode;
import io.redspace.durability_module.VanillaDurabilityMode;
import io.redspace.hunger_module.RegistryGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerConfigs {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> DAMAGE_MODULE_ENABLED;
    public static final ModConfigSpec.ConfigValue<Integer> IFRAME_COUNT;
    public static final ModConfigSpec.ConfigValue<PlayerDamageMode> PLAYER_DAMAGE_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SAME_TICK_DAMAGE_TYPE_WHITELIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_IFRAME_BLACKLIST; //private so the cache must be used
    public static final ModConfigSpec.ConfigValue<Boolean> ALLOW_NON_MINIMUM_STRENGTH_ATTACKS;
    public static final ModConfigSpec.ConfigValue<Double> MINIMUM_ATTACK_STRENGTH;
    public static final ModConfigSpec.ConfigValue<Double> KNOCKBACK_MODIFIER;
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_COMBAT_SNAPSHOT;

    public static final ModConfigSpec.ConfigValue<Boolean> DURABILITY_MODULE_ENABLED;
    public static final ModConfigSpec.ConfigValue<VanillaDurabilityMode> DURABILITY_VANILLA_MODE;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DURABILITY_VANILLA_MODE_WHITELIST; //private so the cache must be used
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DURABILITY_VANILLA_MODE_BLACKLIST; //private so the cache must be used
    public static final ModConfigSpec.ConfigValue<DeathDurabilityMode> DURABILITY_DEATH_MODE;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DURABILITY_DEATH_MODE_WHITELIST; //private so the cache must be used
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DURABILITY_DEATH_MODE_BLACKLIST; //private so the cache must be used
    public static final ModConfigSpec.ConfigValue<Double> DURABILITY_LOST_ON_DEATH;
    public static final ModConfigSpec.ConfigValue<Integer> ADDITIONAL_DURABILITY_LOST_ON_DEATH;

    public static final ModConfigSpec.ConfigValue<Boolean> XP_MODULE_ENABLED;
    public static final ModConfigSpec.ConfigValue<Boolean> XP_IGNORE_KEEPINVENTORY;
    public static final ModConfigSpec.ConfigValue<Boolean> XP_ONLY_ALLOW_OWNER;
    public static final ModConfigSpec.ConfigValue<Double> ENTITY_XP_MODIFIER;
    public static final ModConfigSpec.ConfigValue<Double> BLOCK_XP_MODIFIER;

    public static final ModConfigSpec.ConfigValue<Boolean> HUNGER_MODULE_ENABLED;
    public static final ModConfigSpec.ConfigValue<Boolean> HUNGER_DISABLED;
    public static final ModConfigSpec.ConfigValue<Boolean> ALLOW_SPRINTING;
    public static final ModConfigSpec.ConfigValue<Boolean> HUNGER_PREVENTS_SPRINTING;
    public static final ModConfigSpec.ConfigValue<Double> HUNGER_NUTRITION_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> FOOD_TO_HEALTH_MODIFIER;
    public static final ModConfigSpec.ConfigValue<Integer> NATURAL_REGENERATION_TICK_RATE;
    public static final ModConfigSpec.ConfigValue<Boolean> NATURAL_REGENERATION_DURING_COMBAT;
    public static final ModConfigSpec.ConfigValue<Double> SPLASH_POTION_COOLDOWN;
    public static final ModConfigSpec.ConfigValue<Double> LINGERING_POTION_COOLDOWN;
    public static final ModConfigSpec.ConfigValue<Double> EAT_TIME_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> POTION_DRINK_TIME_MULTIPLER;


//    public static final ModConfigSpec.ConfigValue<Boolean> XP_DROP_REWARD_XP;

    static {
        /*
        Damage Module
         */
        BUILDER.push("Damage-Module");
        DAMAGE_MODULE_ENABLED = BUILDER
                .comment("The purpose of the damage module is to remove the invulnerability ticks after an entity is damaged to better suit gameplay where entities are going to be ignoring too much damage if left unchecked. Disabling will nullify every feature listed under this module.")
                .define("damageModuleEnabled", true);
        IFRAME_COUNT = BUILDER
                .comment("Invulnerability Tick (I-Frame) count. Default: 0")
                .define("invulnerabilityTickCount", 0);
        SAME_TICK_DAMAGE_TYPE_WHITELIST = BUILDER
                .comment("If specified, these damage types will be able to deal damage on the same tick as a mechanism to bypass auto tick detection")
                .defineList("damageTypeSameTickWhitelist", List.of("minecraft:arrow"), x -> true);
        ENTITY_IFRAME_BLACKLIST = BUILDER
                .comment("If specified, these entity types or type tags will be blacklisted from skipping i-frames upon dealing damage")
                .defineList("entityIframeBlacklist", List.of(), x -> true);
        PLAYER_DAMAGE_MODE = BUILDER
                .comment("Additional handling for player's interactions with reduced i-frames. \"ALL\" means there is no additional handling, \"ONLY_LIVING\" means only living attacks ignore player i-frames (may help with unforeseen damage like potions), and \"NONE\" means player's damage ticks are unaffected by the damage module.")
                .defineEnum("playerDamageMode", PlayerDamageMode.ALL);
        MINIMUM_ATTACK_STRENGTH = BUILDER
                .comment("In order to prevent spam attacks, a minimum threshold of attack strength can be set before an attack can deal damage. Default: 0.75")
                .define("minimumAttackStrength", 0.75);
        ALLOW_NON_MINIMUM_STRENGTH_ATTACKS = BUILDER
                .comment("Whether a player is allowed to swing if the minimumAttackStrength threshold is not met. Default: true")
                .define("allowNonMinStrengthAttacks", true);
        KNOCKBACK_MODIFIER = BUILDER
                .comment("Global multiplier to all knockback. Default: 1.0")
                .define("globalKnockbackMultiplier", 1.0);
        ENABLE_COMBAT_SNAPSHOT = BUILDER
                .comment("Enable Combat Snapshot Inspired Changes (experimental): Attack cooldown no longer affects damage, but instead affects weapon reach")
                .comment("!THIS SETTING IS AFFECTED BY \"minimumAttackStrength\" AND \"allowNonMinStrengthAttacks\" WHICH SHOULD BE CONFIGURED ACCORDINGLY!")
                .define("enableCombatSnapshot", false);
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
                .comment("If specified, the only items or item tags to take vanilla durability damage (Ignores durability mode)")
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
                .comment("If specified, the only items or item tags to take durability damage on death (Ignores durability mode)").
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
                .comment("Whether players will drop xp despite keepInventory gamerule. Default: true")
                .define("ignoreKeepInventory", true);
        XP_ONLY_ALLOW_OWNER = BUILDER
                .comment("Whether the player who dropped the xp is the only player allow to collect the xp. Default: true")
                .define("onlyAllowOwnerPickup", true);
        ENTITY_XP_MODIFIER = BUILDER
                .comment("Multiplier to experience dropped by slain entities. Default: 1.0")
                .define("mobDropXpMultiplier", 1.0);
        BLOCK_XP_MODIFIER = BUILDER
                .comment("Multiplier to experience dropped by blocks broken. Default: 1.0")
                .define("blockDropXpMultiplier", 1.0);
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
        HUNGER_PREVENTS_SPRINTING = BUILDER
                .comment("Whether the Hunger status effect prevents the player from sprinting. Default: true")
                .define("hungerEffectPreventsSprinting", true);
        ALLOW_SPRINTING = BUILDER
                .comment("Whether sprinting is allowed. Default: true")
                .define("allowSprinting", true);
        FOOD_TO_HEALTH_MODIFIER = BUILDER
                .comment("The multiplier of a food's hunger value to health regained by eating it. Default: 0.5 (50%)")
                .define("foodToHealthModifier", 0.5);
        HUNGER_NUTRITION_MULTIPLIER = BUILDER
                .comment("An additional multiplier to the health regained by eating while under the Hunger status effect. Default: 0.5 (50%)")
                .define("hungerEffectHealMultiplier", 0.5);
        NATURAL_REGENERATION_TICK_RATE = BUILDER.
                comment("The amount of time, in ticks, between players naturally regenerating 1 hp. 1 second is 20 ticks. Turn off the naturalRegeneration gamerule to disable. Default: 250.")
                .define("naturalRegenerationTickRate", 250);
        NATURAL_REGENERATION_DURING_COMBAT = BUILDER
                .comment("Whether players should naturally regenerate hp during combat. (Turn off the naturalRegeneration gamerule to disable all natural regen). Default: false.")
                .define("naturalRegenerationDuringCombat", false);
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

    public static class RegistryLists {
        public static final Set<Item> DURABILITY_VANILLA_MODE_WHITELIST_ITEMS = new HashSet<>();
        public static final Set<Item> DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS = new HashSet<>();
        public static final Set<Item> DURABILITY_DEATH_MODE_WHITELIST_ITEMS = new HashSet<>();
        public static final Set<Item> DURABILITY_DEATH_MODE_BLACKLIST_ITEMS = new HashSet<>();
        public static final Set<EntityType<?>> ENTITY_IFRAME_BLACKLIST = new HashSet<>();
        public static final Set<Item> FOOD_STACK_BLACKLIST_ITEMS = new HashSet<>();
    }

    public static void handleOnConfigReload() {
        IronsRpgTweaks.LOGGER.debug("On Config Reload");

        /*
        Cache whitelists/blacklists into objects
         */
        cacheRegistryList(RegistryGetter.getItem(), DURABILITY_VANILLA_MODE_WHITELIST.get(), RegistryLists.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS);
        cacheRegistryList(RegistryGetter.getItem(), DURABILITY_VANILLA_MODE_BLACKLIST.get(), RegistryLists.DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS);
        cacheRegistryList(RegistryGetter.getItem(), DURABILITY_DEATH_MODE_WHITELIST.get(), RegistryLists.DURABILITY_DEATH_MODE_WHITELIST_ITEMS);
        cacheRegistryList(RegistryGetter.getItem(), DURABILITY_DEATH_MODE_BLACKLIST.get(), RegistryLists.DURABILITY_DEATH_MODE_BLACKLIST_ITEMS);
        cacheRegistryList(RegistryGetter.getEntity(), ENTITY_IFRAME_BLACKLIST.get(), RegistryLists.ENTITY_IFRAME_BLACKLIST);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_VANILLA_MODE_WHITELIST: {} {}", DURABILITY_VANILLA_MODE_WHITELIST.get(), RegistryLists.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_VANILLA_MODE_BLACKLIST: {} {}", DURABILITY_VANILLA_MODE_BLACKLIST.get(), RegistryLists.DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_DEATH_MODE_WHITELIST: {} {}", DURABILITY_DEATH_MODE_WHITELIST.get(), RegistryLists.DURABILITY_DEATH_MODE_WHITELIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("DURABILITY_DEATH_MODE_BLACKLIST: {} {}", DURABILITY_DEATH_MODE_BLACKLIST.get(), RegistryLists.DURABILITY_DEATH_MODE_BLACKLIST_ITEMS);
        IronsRpgTweaks.LOGGER.debug("ENTITY_IFRAME_BLACKLIST: {} {}", ENTITY_IFRAME_BLACKLIST.get(), RegistryLists.ENTITY_IFRAME_BLACKLIST);

    }

    private static <T> void cacheRegistryList(Registry<T> registry, List<? extends String> ids, Set<T> output) {
        output.clear();
        for (String name : ids) {
            try {
                if (name.startsWith("#")) {
                    var tag = new TagKey<T>(registry.key(), ResourceLocation.parse(name.substring(1)));
                    output.addAll(registry.stream().filter(item -> registry.wrapAsHolder(item).is(tag)).toList());
                } else {
                    var item = registry.get(ResourceLocation.parse(name));
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

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ResourceLocation.isValidNamespace(itemName) && ResourceLocation.isValidPath(itemName) && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static String getDefaultEntries(List<? extends String> list) {
        String str = "[";
        for (String entry : list)
            str += "\"" + entry + "\",";
        str = str.substring(0, str.length() - 1);
        return str + "]";
    }
}
