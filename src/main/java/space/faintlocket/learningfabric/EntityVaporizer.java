package space.faintlocket.learningfabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.World;

import java.util.Objects;

public class EntityVaporizer extends SwordItem {
    public EntityVaporizer(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (!world.isClient) return super.use(world, user, hand);


        //running client side
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        if (Objects.requireNonNull(hit).getType() == Type.ENTITY) {
            //hit an entity
            user.sendMessage(new LiteralText("Clicked a mob"), false);
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();

            //send a packet to the server saying to remove the entity
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(entity.getUuid());
            ClientPlayNetworking.send(LearningFabric.VAPORIZE_ENTITY_PACKET_ID, buf);
        }


        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
