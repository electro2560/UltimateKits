package com.songoda.kitpreview.handlers;

import com.songoda.arconix.Arconix;
import com.songoda.kitpreview.KitPreview;
import com.songoda.kitpreview.utils.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by songoda on 2/24/2017.
 */
public class ParticleHandler {

    private final KitPreview instance;

    public ParticleHandler(KitPreview instance) {
        this.instance = instance;
        checkDefaults();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(KitPreview.getInstance(), () -> applyParticles(), 0, 10L);
    }

    private void applyParticles() {
        try {
            if (instance.getConfig().getString("data.particles") == null) {
                return;
            }
            int amt = instance.getConfig().getInt("data.particlesettings.ammount");
            String type = instance.getConfig().getString("data.particlesettings.type");

            ConfigurationSection section = instance.getConfig().getConfigurationSection("data.particles");
            for (String loc : section.getKeys(false)) {
                String str[] = loc.split(":");
                String worldName = str[1].substring(0, str[1].length() - 1);
                if (Bukkit.getServer().getWorld(worldName) == null) {
                    continue;
                }
                Location location = Arconix.pl().serialize().unserializeLocation(loc);
                location.add(.5, 0, .5);

                if (instance.v1_8 || instance.v1_7) {
                    location.getWorld().spigot().playEffect(location, org.bukkit.Effect.valueOf(type), 1, 0, (float) 0.25, (float) 0.25, (float) 0.25, 1, amt, 100);
                } else {
                    location.getWorld().spawnParticle(org.bukkit.Particle.valueOf(type), location, amt, 0.25, 0.25, 0.25);
                }
            }

        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    private void checkDefaults() {
        try {
            if (instance.getConfig().getInt("data.particlesettings.ammount") == 0) {
                instance.getConfig().set("data.particlesettings.ammount", 25);
                instance.saveConfig();
            }
            if (instance.getConfig().getString("data.particlesettings.type") != null) return;
            if (instance.v1_7 || instance.v1_8) {
                instance.getConfig().set("data.particlesettings.type", "WITCH_MAGIC");
            } else {
                instance.getConfig().set("data.particlesettings.type", "SPELL_WITCH");
            }
            instance.saveConfig();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

}
