package com.clownvin.soulcraft.updatechecker;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class UpdateChecker {
    private static int isNewerVersion(String v1, String v2) {
        if (v1 == null || v2 == null) {
            System.err.println("Cannot compare mod versions, "+v1+", "+v2);
            return -1;
        }
        String[] v1s = v1.split("\\.");
        String[] v2s = v2.split("\\.");
        if (v2s.length > v1s.length)
            return 0;
        System.out.println(v2s.length+", "+v1s.length);
        for (int i = 0; i < v2s.length; i++) {
            if (v2s[i].length() > v1s[i].length()) {
                return i + 1;
            }
            if (v2s[i].compareTo(v1s[i]) > 0) {
                return i + 1;
            }
        }
        return -1;
    }

    @SubscribeEvent
    public static void onJoinGame(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!SoulCraftConfig.general.showUpdateNotifications)
            return;
        ForgeVersion.CheckResult result = ForgeVersion.getResult(Loader.instance().activeModContainer());
        int versionDiff;
        if (result.target == null || (versionDiff = isNewerVersion(Loader.instance().activeModContainer().getVersion(), result.target.toString())) < 0) {//result.target.compareTo(Loader.instance().activeModContainer().getVersion()) <= 0) {
            return;
        }
        String updateType;
        if (versionDiff == 1) { //Major update
            updateType = "major update";
        } else if (versionDiff == 2) { //Minor update
            updateType = "minor update";
        } else if (versionDiff == 3) { //Patch update
            updateType = "patch";
        } else {
            updateType = "update";
        }
        event.player.sendMessage(new TextComponentTranslation("text.new_update_notification", updateType, SoulCraft.MOD_NAME, result.target.toString()));
    }
}
