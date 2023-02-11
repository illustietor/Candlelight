package net.satisfy.candlelight.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.satisfy.candlelight.registry.ObjectRegistry;
import net.satisfy.candlelight.registry.StorageTypes;
import satisfyu.vinery.block.StorageBlock;
import satisfyu.vinery.block.entity.StorageBlockEntity;

import java.util.List;

public class TableSetBlock extends StorageBlock {

    public static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 15, 1, 15);

    public static final EnumProperty<PlateType> PLATE_TYPE = EnumProperty.of("plate", PlateType.class);

    public TableSetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(super.getDefaultState().with(PLATE_TYPE, PlateType.PLATE));
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        PlateType type = state.get(PLATE_TYPE);
        if(stack.isEmpty() && player.isSneaking()) world.breakBlock(pos, true, player);
        else if(type.equals(PlateType.ALL)) return super.onUse(state, world, pos, player, hand, hit);
        else if(type != PlateType.GLASS && item.equals(ObjectRegistry.GLASS)){
            if(!world.isClient()){
                if(type.equals(PlateType.PLATE)) world.setBlockState(pos, state.with(PLATE_TYPE, PlateType.GLASS));
                else world.setBlockState(pos, state.with(PLATE_TYPE, PlateType.ALL));
                if (!player.isCreative()) stack.decrement(1);
            }
            return ActionResult.success(world.isClient());
        }
        else if(type != PlateType.NAPKIN && item.equals(ObjectRegistry.NAPKIN)){
            if(!world.isClient()){
                if(type.equals(PlateType.PLATE)) world.setBlockState(pos, state.with(PLATE_TYPE, PlateType.NAPKIN));
                else world.setBlockState(pos, state.with(PLATE_TYPE, PlateType.ALL));
                if (!player.isCreative()) stack.decrement(1);
            }
            return ActionResult.success(world.isClient());
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void remove(World world, BlockPos blockPos, PlayerEntity player, StorageBlockEntity shelfBlockEntity, int i) {
        if (!world.isClient()) {
            ItemStack itemStack = shelfBlockEntity.removeStack(i);
            SoundEvent soundEvent = SoundEvents.ENTITY_PLAYER_BURP;
            world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (itemStack.isFood()) {
                FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
                player.getHungerManager().add(Math.round(foodComponent.getHunger() * 1.3f), foodComponent.getSaturationModifier() * 1.3f);
                List<Pair<StatusEffectInstance, Float>> list = foodComponent.getStatusEffects();
                for (Pair<StatusEffectInstance, Float> pair : list) {
                    if (pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond()))
                        continue;
                    player.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
                }
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Identifier type() {
        return StorageTypes.TABLE_SET;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[0];
    }

    @Override
    public boolean canInsertStack(ItemStack stack) {
        return stack.isFood();
    }

    @Override
    public int getSection(Float x, Float y) {
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PLATE_TYPE);
    }

    public enum PlateType implements StringIdentifiable {
        PLATE("plate"),
        GLASS("glass"),
        NAPKIN("napkin"),
        ALL("all");
        private final String name;
        PlateType(String name) {
            this.name = name;
        }
        @Override
        public String asString() {
            return this.name;
        }
    }
}
