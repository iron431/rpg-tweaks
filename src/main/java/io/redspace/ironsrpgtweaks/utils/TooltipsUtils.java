package io.redspace.ironsrpgtweaks.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class TooltipsUtils {

    public static final Style UNIQUE_STYLE = Style.EMPTY.withColor(0xe04324);

    public static int indexOfComponent(List<Component> lines, String key) {
        return indexOfInternal(lines, key::equals);
    }

    public static int indexOfComponentRegex(List<Component> lines, String regex) {
        //IronsSpellbooks.LOGGER.debug("TooltipsUtils.indexOfComponentRegex: {}", regex);
        return indexOfInternal(lines, (string -> string.matches(regex)));
    }

    public static int indexOfAdvancedText(List<Component> lines, ItemStack itemStack) {
        return indexOfComponentRegex(lines, "item.durability|item.components|" + BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
    }

    private static int indexOfInternal(List<Component> lines, Predicate<String> comparator) {
        int size = lines.size();
        for (int i = 0; i < size; i++) {
            var component = lines.get(i);
            if (component.getContents() instanceof TranslatableContents translatableContents) {
                //IronsSpellbooks.LOGGER.debug("TooltipsUtils.indexOfInternal {}: {}: {}", i, translatableContents.getKey(), comparator.test(translatableContents.getKey()));
                if (comparator.test(translatableContents.getKey())) {
                    return i;
                }
            } else if (component.getContents() instanceof PlainTextContents.LiteralContents literalContents) {
                //IronsSpellbooks.LOGGER.debug("TooltipsUtils.indexOfInternal {}: {}: {}", i, literalContents.text(), comparator.test(literalContents.text()));
                if (comparator.test(literalContents.text())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
