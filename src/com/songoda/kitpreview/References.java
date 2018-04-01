package com.songoda.kitpreview;

import com.songoda.kitpreview.utils.Debugger;
import org.bukkit.Sound;

public class References {

    private String prefix = null;
    private boolean playSound = false;
    private Sound sound = null;

    public References() {
        try {
            prefix = Lang.PREFIX.getConfigValue() + " ";
            playSound = KitPreview.getInstance().getConfig().getBoolean("Main.Sounds Enabled");
            sound = Sound.valueOf(KitPreview.getInstance().getConfig().getString("Main.Sound Played While Clicking In Inventories"));
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isPlaySound() {
        return this.playSound;
    }

    public Sound getSound() {
        return this.sound;
    }
}
