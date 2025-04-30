package com.mina.ultimatemagic.Items;

import com.mina.ultimatemagic.Items.Custom.SpellBookItem;
import com.mina.ultimatemagic.UltimateMagic;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;

public class ModItems {

    // Register items
    public static final Item GUIDE_BOOK = registerItem("guide_book", new Item(new Item.Settings().maxCount(1)));
    public static final Item WAND = registerItem("wand", new Item(new Item.Settings().maxCount(1)));
    public static final Item WAND_GOLD = registerItem("wand_gold", new Item(new Item.Settings().maxCount(1) ));
    public static final Item SPELL_BOOK = registerItem("spell_book", new SpellBookItem(new Item.Settings().maxCount(1)));

    public static final Item RAW_MAGIC = registerItem("raw_magic", new Item(new Item.Settings().maxCount(64) ));
    public static final Item MAGIC_INGOT = registerItem("magic_ingot", new Item(new Item.Settings().maxCount(64) ));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(UltimateMagic.MOD_ID, name), item);
    }
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(UltimateMagic.MOD_ID, name), block);
    }

    public static void registerModItems() {
        UltimateMagic.LOGGER.info("Registering Mod Items for " + UltimateMagic.MOD_ID);

        // Correct creation of the RegistryKey for the custom ItemGroup
        RegistryKey<ItemGroup> ultimateMagicGroupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(UltimateMagic.MOD_ID, "ultimate_magic_items"));

        // Add items to the custom item group
        ItemGroupEvents.modifyEntriesEvent(ultimateMagicGroupKey).register(entries -> {
            entries.add(SPELL_BOOK);
            entries.add(GUIDE_BOOK);
            entries.add(WAND);
            entries.add(WAND_GOLD);
            entries.add(RAW_MAGIC);
            entries.add(MAGIC_INGOT);

        });
    }


}
