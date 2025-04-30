package com.mina.ultimatemagic.Items;

import com.mina.ultimatemagic.Blocks.ModBlocks;
import com.mina.ultimatemagic.UltimateMagic;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup ULTIMATE_MAGIC_ITEMS = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(UltimateMagic.MOD_ID, "ultimate_magic_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.GUIDE_BOOK)) // Set the icon for the item group
                    .displayName(Text.translatable("itemgroup.ultimatemagic.ultimate_magic_items")) // Set the display name
                    .entries((displayContext, entries) -> { // Add the items to the group
                        entries.add(ModItems.GUIDE_BOOK);
                        entries.add(ModItems.SPELL_BOOK);
                        entries.add(ModItems.WAND_GOLD);
                        entries.add(ModItems.WAND);
                        entries.add(ModBlocks.TEST_BLOCK);
                        entries.add(ModBlocks.MAGIC_BLOCK);
                        entries.add(ModBlocks.MAGIC_ORE_BLOCK);
                        entries.add(ModBlocks.DEEPSLATE_MAGIC_ORE_BLOCK);
                        entries.add(ModItems.RAW_MAGIC);
                        entries.add(ModItems.MAGIC_INGOT);

                    })
                    .build()
    );

    public static void registerItemGroups() {
        UltimateMagic.LOGGER.info("Registering Item Group for " + UltimateMagic.MOD_ID);
    }
}