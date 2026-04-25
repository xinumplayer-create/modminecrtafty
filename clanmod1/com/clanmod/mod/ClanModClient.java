/* Decompiler 23ms, total 841ms, lines 31 */
package com.clanmod.mod;

import com.clanmod.mod.config.ModConfig;
import com.clanmod.mod.events.ClanChatHandler;
import com.clanmod.mod.gui.ClanModScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_304.class_11900;

public class ClanModClient implements ClientModInitializer {
   private static class_304 openGuiKey;
   private static final class_11900 CLANMOD_CATEGORY = class_11900.method_74698(class_2960.method_60655("clanmod", "clanmod"));

   public void onInitializeClient() {
      ModConfig.load();
      ClanChatHandler.register();
      openGuiKey = KeyBindingHelper.registerKeyBinding(new class_304("key.clanmod.open_gui", 268, CLANMOD_CATEGORY));
      ClientTickEvents.END_CLIENT_TICK.register((client) -> {
         while(openGuiKey.method_1436()) {
            if (client.field_1755 == null) {
               client.method_1507(new ClanModScreen());
            }
         }

      });
   }
}
