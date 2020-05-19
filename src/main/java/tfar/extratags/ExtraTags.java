package tfar.extratags;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;
import tfar.extratags.api.ExtraTagManager;
import tfar.extratags.api.tagtypes.BiomeTags;
import tfar.extratags.api.tagtypes.BlockEntityTypeTags;
import tfar.extratags.api.tagtypes.DimensionTypeTags;
import tfar.extratags.api.tagtypes.EnchantmentTags;

public class ExtraTags implements ClientModInitializer {

	public static ExtraTags instance;

	public ExtraTagManager extraTagManager;

	public static final String MODID = "extratags";

	public static final Identifier packet_id = new Identifier(MODID,MODID);

	public ExtraTags(){
		instance = this;
	}

	@Override
	public void onInitializeClient() {
		ClientSidePacketRegistry.INSTANCE.register(packet_id,
						(packetContext, attachedData) -> {
			ExtraTagManager extraTagManager = ExtraTagManager.read(attachedData);
							packetContext.getTaskQueue().execute(() -> {
								instance.extraTagManager = extraTagManager;
								EnchantmentTags.setContainer(extraTagManager.getEnchantments());
								BlockEntityTypeTags.setContainer(extraTagManager.getBlockEntityTypes());
								BiomeTags.setContainer(extraTagManager.getBiomes());
								DimensionTypeTags.setContainer(extraTagManager.getDimensionTypes());
							});
						});
		/*
		ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
			ListTag enchantments = EnchantedBookItem.getEnchantmentTag(itemStack);
			for(int i = 0; i < enchantments.size(); ++i) {
				CompoundTag compoundTag = enchantments.getCompound(i);
				Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent((e) -> {
					EnchantmentTags.getCollection().getEntries().forEach(
									(identifier, enchantmentTag) -> {
										if (enchantmentTag.contains(e)){
											list.add(new LiteralText(identifier.toString()));
										}
									}
					);
				});
			}
		});*/
	}
}
