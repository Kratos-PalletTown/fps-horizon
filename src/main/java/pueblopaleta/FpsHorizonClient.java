package pueblopaleta;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/**
 * Fabric entrypoint, declared in fabric.mod.json under "client".
 *
 * Replaces the NeoForge @Mod("fpshorizon") KratosOptimizer constructor.
 * NeoForge's ModContainer#registerConfig / IConfigScreenFactory has no
 * Fabric equivalent - the config screen is instead exposed through
 * ModMenuApi (see KratosModMenuIntegration), and persistence goes through
 * KratosConfig's own JSON load/save instead of a ModConfigSpec.
 */
public class FpsHorizonClient implements ClientModInitializer
{
    private static KratosOptimizer optimizer;
    private static KeyMapping openConfigKey;
    private static KeyMapping openProfilesKey;

    @Override
    public void onInitializeClient() {
        KratosConfig.load();
        KratosProfiles.load();

        optimizer = new KratosOptimizer();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.fpshorizon.open_config",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.fpshorizon"
        ));

        openProfilesKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.fpshorizon.open_profiles",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.fpshorizon"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            optimizer.tick();

            final Minecraft mc = Minecraft.getInstance();
            while (openConfigKey.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(KratosModMenuIntegration.buildConfigScreen(null));
                }
            }
            while (openProfilesKey.consumeClick()) {
                if (mc.screen == null) {
                    mc.setScreen(new KratosProfilesScreen(null));
                }
            }
        });
    }

    public static KratosOptimizer getOptimizer() {
        return optimizer;
    }
}
