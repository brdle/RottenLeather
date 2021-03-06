package net.brdle.rottenleather.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.brdle.rottenleather.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class AddItemLootModifier extends LootModifier {
    public static final Codec<AddItemLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
        .and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(g -> g.item))
        .and(Codec.INT.fieldOf("minAmount").forGetter(g -> g.minAmount))
        .and(Codec.INT.fieldOf("minAmount").forGetter(g -> g.maxAmount))
        .and(Codec.BOOL.fieldOf("unique").forGetter(g -> g.unique))
        .apply(inst, AddItemLootModifier::new));
    private final Item item;
    private final int minAmount;
    private final int maxAmount;
    private final boolean unique;

    public AddItemLootModifier(LootItemCondition[] conditions, Item item, int minAmount, int maxAmount, boolean unique) {
        super(conditions);
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.unique = unique;
    }

    /**
     * Applies the modifier to the generated loot (all loot conditions have already been checked
     * and have returned true).
     *
     * @param generatedLoot the list of ItemStacks that will be dropped, generated by loot tables
     * @param context       the LootContext, identical to what is passed to loot tables
     * @return modified loot drops
     */
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if ((this.unique && generatedLoot.stream().anyMatch(stack -> stack.getItem().equals(this.item))) || (this.maxAmount < 1)) {
            return generatedLoot;
        }
        int amount = context.getRandom().nextInt(this.maxAmount + 1 - this.minAmount) + this.minAmount;
        return (amount >= 1) ? Util.with(generatedLoot, new ItemStack(this.item, amount)) : generatedLoot;
    }

    /**
     * Returns the registered codec for this modifier
     */
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return RottenLeatherLootModifiers.ADD_ITEM.get();
    }

    /*public static class Serializer extends GlobalLootModifierSerializer<AddItemLootModifier> {

        @Override
        public AddItemLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditions) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation((GsonHelper.getAsString(object, "item"))));
            int minAmount = GsonHelper.getAsInt(object, "minAmount");
            int maxAmount = GsonHelper.getAsInt(object, "maxAmount");
            boolean unique = GsonHelper.getAsBoolean(object, "unique");
            return new AddItemLootModifier(conditions, item, minAmount, maxAmount, unique);
        }

        @Override
        public JsonObject write(AddItemLootModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(instance.item).toString());
            json.addProperty("minAmount", instance.minAmount);
            json.addProperty("maxAmount", instance.maxAmount);
            json.addProperty("unique", instance.unique);
            return json;
        }
    }*/
}