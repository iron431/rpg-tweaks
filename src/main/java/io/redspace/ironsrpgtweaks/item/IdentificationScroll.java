package io.redspace.ironsrpgtweaks.item;

import io.redspace.ironsrpgtweaks.config.CommonConfigs;
import io.redspace.ironsrpgtweaks.enchantment_module.EnchantHelper;
import io.redspace.ironsrpgtweaks.enchantment_module.EnchantmentClientEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IdentificationScroll extends Item {
    public static final Component description = Component.translatable("item.irons_rpg_tweaks.identification_scroll.description").withStyle(ChatFormatting.GRAY);
    public static final Component shift = Component.translatable("ui.irons_rpg_tweaks.shift_tooltip").withStyle(ChatFormatting.GRAY);

    public IdentificationScroll() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stackMe, Slot pSlot, ClickAction pAction, Player pPlayer) {
        var other = pSlot.getItem();
        if (pAction == ClickAction.SECONDARY) {
            if (attemptIdentifyItem(other, pPlayer)) {
                stackMe.shrink(1);
                return true;
            }
        }
        return super.overrideStackedOnOther(stackMe, pSlot, pAction, pPlayer);
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand useHand) {
//        var otherhand = player.getItemInHand(useHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
//        if (!level.isClientSide && attemptIdentifyItem(otherhand)) {
//            //TODO: shrink not working?
//            player.getItemInHand(useHand).shrink(1);
//            return InteractionResultHolder.success(player.getItemInHand(useHand));
//        }
//        return super.use(level, player, useHand);
//    }

    private boolean attemptIdentifyItem(ItemStack itemStack, Player player) {
        if (EnchantHelper.shouldHideEnchantments(itemStack)) {
            EnchantHelper.unhideEnchantments(itemStack, player);
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
                pTooltipComponents.add(description);
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
