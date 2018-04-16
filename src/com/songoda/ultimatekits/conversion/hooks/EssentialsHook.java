package com.songoda.ultimatekits.conversion.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;
import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.conversion.Hook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EssentialsHook implements Hook {

    private Essentials essentials;

    public EssentialsHook() {
        essentials = (Essentials) UltimateKits.getInstance().getServer().getPluginManager().getPlugin("Essentials");
    }

    public Set<ItemStack> getItems(String kitName) {
        Set<ItemStack> stacks = new HashSet<>();
        try {
            Kit kit = new Kit(kitName, essentials);

            for (String nonParse : kit.getItems()) {
                String[] parts = nonParse.split(" +");
                ItemStack item = essentials.getItemDb().get(parts[0], parts.length > 1 ? Integer.parseInt(parts[1]) : 1);
                MetaItemStack metaStack = new MetaItemStack(item);
                if (parts.length > 2 != nonParse.startsWith("/")) {
                    try {
                        metaStack.parseStringMeta(null, true, parts, 2, essentials);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                stacks.add(metaStack.getItemStack());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return stacks;
    }

    public Set<String> getKits() {
        ConfigurationSection cs = essentials.getSettings().getKits();
        Set<String> kits = new HashSet<>();
        if (cs.getKeys(false).size() == 0) return kits;
        for (String kitItem : cs.getKeys(false)) {
            kits.add(kitItem);
        }
        return kits;
    }

    public long getDelay(String kitName) {
        return Integer.toUnsignedLong((int)essentials.getSettings().getKit(kitName).getOrDefault("delay", 0));
    }

}
