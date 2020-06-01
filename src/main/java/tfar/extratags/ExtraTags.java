package tfar.extratags;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.extratags.api.ExtraTagManager;
import tfar.extratags.api.ExtraTagRegistry;

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
								instance.extraTagManager.setContainers();
							});
						});

		/*ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
			ListTag enchantments = EnchantedBookItem.getEnchantmentTag(itemStack);
			for(int i = 0; i < enchantments.size(); ++i) {
				CompoundTag compoundTag = enchantments.getCompound(i);
				Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(compoundTag.getString("id"))).ifPresent((e) -> {
					ExtraTagRegistry.ENCHANTMENT.getContainer().getEntries().forEach(
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
