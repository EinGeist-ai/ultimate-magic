package com.mina.ultimatemagic.Blocks;

import com.mina.ultimatemagic.Items.ModItems;
import com.mina.ultimatemagic.UltimateMagic;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import static com.mina.ultimatemagic.UltimateMagic.MOD_ID;
import static com.mina.ultimatemagic.UltimateMagic.LOGGER;

public class ModBlocks {
    public static final Block TEST_BLOCK = registerBlock("test_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .luminance((state) -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block MAGIC_BLOCK = registerBlock("magic_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .luminance((state) -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK)),
            new Item.Settings().fireproof());

    public static final Block MAGIC_ORE_BLOCK = registerBlock("magic_ore_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .luminance((state) -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block PEWTER_ORE_BLOCK = registerBlock("pewter_ore_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .luminance((state) -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block PEWTER_BLOCK = registerBlock("pewter_block",
            new Block(AbstractBlock.Settings.create().strength(4f)
                    .luminance((state) -> 15).sounds(BlockSoundGroup.AMETHYST_BLOCK)),
            new Item.Settings().maxCount(16).fireproof());




    private static Block registerBlock(String name, Block block, Item.Settings settings) {
        LOGGER.debug("[ModBlocks] Registering block: {}", name);
        registerBlockItem(name, block, settings);
        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, Item.Settings settings) {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name),
                new BlockItem(block, settings));
    }


    private static Block registerBlock(String name, Block block) {
        return registerBlock(name, block, new Item.Settings());
    }

    public static void registerModBlocks(){
        LOGGER.debug("[ModBlocks] Starting block registration");

        UltimateMagic.LOGGER.info("Regestering Mod Blocks for " + MOD_ID);

        RegistryKey<ItemGroup> ultimateMagicGroupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(MOD_ID, "ultimate_magic_items"));
        ItemGroupEvents.modifyEntriesEvent(ultimateMagicGroupKey).register(entries -> {
            entries.add(ModBlocks.MAGIC_BLOCK);
            entries.add(ModBlocks.TEST_BLOCK);
            entries.add(ModBlocks.MAGIC_ORE_BLOCK);
            entries.add(ModBlocks.PEWTER_ORE_BLOCK);
            entries.add(ModBlocks.PEWTER_BLOCK);

        });

        LOGGER.debug("[ModBlocks] Block registration completed");
    }

}