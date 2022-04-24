package space.faintlocket.learningfabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.UUID;

public class LearningFabric implements ModInitializer {
    //check for server/client here maybe
    public static final String modid = "learningfabric";
    public static final Identifier VAPORIZE_ENTITY_PACKET_ID = new Identifier(modid, "vaporize_entity");
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(modid, "general"),
            () -> new ItemStack(Blocks.COBBLESTONE)
    );

    private static final UnobtainiumMaterial UNOBTAINIUM_MATERIAL = new UnobtainiumMaterial();
    //public static final Item FABRIC_ITEM = new Item(new FabricItemSettings().group(ItemGroup.MISC));
    private static final EntityVaporizer ENTITY_VAPORIZER = new EntityVaporizer(UNOBTAINIUM_MATERIAL, 7, -1.0f, new FabricItemSettings().group(ITEM_GROUP));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(modid, "fabric_item"), ENTITY_VAPORIZER);

        ServerPlayNetworking.registerGlobalReceiver(LearningFabric.VAPORIZE_ENTITY_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            UUID entityUUID = buf.readUuid();

            server.execute(() -> {
                //execute on the server thread
                Entity entity = player.getWorld().getEntity(entityUUID);
                assert entity != null;
                entity.remove(Entity.RemovalReason.DISCARDED);
            });
        });
    }
}
