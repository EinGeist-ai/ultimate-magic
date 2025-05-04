package com.mina.ultimatemagic.CauldronRecipeSytem;

import com.mina.ultimatemagic.Items.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import java.util.List;
import com.mina.ultimatemagic.CauldronTracker;

public class BloodstoneRecipe {
    /*public static void checkAndCraftBloodstone(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) return;
        
        CauldronTracker.CauldronContent content = CauldronTracker.getCauldronAt((ServerWorld) world, pos);
        
        if (content == null) return;
        
        // Überprüfe die benötigten Bedingungen
        boolean hasMagicIngots = content.getItemCount("Magic Ingot") == 2;
        boolean hasPewterIngots = content.getItemCount("Pewter Ingot") == 2;
        boolean hasCorrectWaterLevel = content.fluidLevel >= 1 && "Wasser".equals(content.fluidType);

        if (hasMagicIngots && hasPewterIngots && hasCorrectWaterLevel) {
            Box searchBox = new Box(pos).expand(0.125);
            List<ItemEntity> itemsInCauldron = world.getEntitiesByClass(ItemEntity.class, searchBox, entity -> true);
            
            int magicIngotsRemoved = 0;
            int pewterIngotsRemoved = 0;
            
            // Entferne nur die benötigten Items
            for (ItemEntity entity : itemsInCauldron) {
                String itemName = entity.getStack().getItem().getName().getString();
                int count = entity.getStack().getCount();
                
                if (itemName.equals("Magic Ingot") && magicIngotsRemoved < 2) {
                    if (count <= 2 - magicIngotsRemoved) {
                        magicIngotsRemoved += count;
                        entity.discard();
                    } else {
                        ItemStack newStack = entity.getStack().copy();
                        newStack.setCount(count - (2 - magicIngotsRemoved));
                        entity.setStack(newStack);
                        magicIngotsRemoved = 2;
                    }
                }
                else if (itemName.equals("Pewter Ingot") && pewterIngotsRemoved < 2) {
                    if (count <= 2 - pewterIngotsRemoved) {
                        pewterIngotsRemoved += count;
                        entity.discard();
                    } else {
                        ItemStack newStack = entity.getStack().copy();
                        newStack.setCount(count - (2 - pewterIngotsRemoved));
                        entity.setStack(newStack);
                        pewterIngotsRemoved = 2;
                    }
                }
            }

            // Reduziere den Wasserstand um 1
            BlockState currentState = world.getBlockState(pos);
            if (currentState.getBlock() instanceof LeveledCauldronBlock) {
                int newLevel = content.fluidLevel - 1;
                if (newLevel > 0) {
                    world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState()
                            .with(LeveledCauldronBlock.LEVEL, newLevel));
                } else {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                }
            }

            // Erstelle den Bloodstone
            ItemEntity bloodstone = new ItemEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                ModItems.ARTIFACT_BLOODSTONE.getDefaultStack()
            );
            
            world.spawnEntity(bloodstone);
        }
    }*/
}