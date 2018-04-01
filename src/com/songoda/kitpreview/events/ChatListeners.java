package com.songoda.kitpreview.events;

import com.songoda.arconix.Arconix;
import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.kitpreview.KitPreview;
import com.songoda.kitpreview.kits.Editor;
import com.songoda.kitpreview.kits.Kit;
import com.songoda.kitpreview.Lang;
import com.songoda.kitpreview.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by songoda on 2/24/2017.
 */
public class ChatListeners implements Listener {

    private final KitPreview instance;

    public ChatListeners(KitPreview instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        try {
            final Player p = e.getPlayer();
            if (!instance.inEditor.containsKey(p)) return;

            Editor edit = new Editor(instance.editingKit.get(p).getShowableName(), p);
            String msg = e.getMessage();
            Kit kit = instance.editingKit.get(p);
            e.setCancelled(true);

            switch (instance.inEditor.get(p)) {
                case "price":
                    if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
                        p.sendMessage(instance.references.getPrefix() + TextComponent.formatText("&8You must have &aVault &8installed to utilize economy.."));
                    } else if (!Arconix.pl().doMath().isNumeric(msg)) {
                        p.sendMessage(TextComponent.formatText("&a" + msg + " &8is not a number. Please do not include a &a$&8."));
                    } else {
                        if (instance.getConfig().getString("data.kit." + kit.getName() + ".link") != null) {
                            instance.getConfig().set("data.kit." + kit.getName() + ".link", null);
                            p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&8LINK has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                        }
                        Double eco = Double.parseDouble(msg);
                        instance.getConfig().set("data.kit." + kit.getName() + ".eco", eco);
                        instance.holo.updateHolograms();
                        instance.saveConfig();
                    }
                    instance.inEditor.remove(p);
                    edit.selling();
                    break;
                case "link":
                    if (instance.getConfig().getString("data.kit." + kit.getName() + ".eco") != null) {
                        instance.getConfig().set("data.kit." + kit.getName() + ".eco", null);
                        p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&8ECO has been removed from this kit. Note you cannot have ECO & LINK set at the same time.."));
                    }
                    String link = msg;
                    instance.getConfig().set("data.kit." + kit.getName() + ".link", link);
                    instance.saveConfig();
                    instance.holo.updateHolograms();
                    instance.inEditor.remove(p);
                    edit.selling();
                    break;
                case "title":
                    instance.getConfig().set("data.kit." + kit.getName() + ".title", msg);
                    instance.saveConfig();
                    instance.holo.updateHolograms();
                    p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&8Title &5" + msg + "&8 added to Kit &a" + kit.getShowableName() + "&8."));
                    instance.inEditor.remove(p);
                    edit.gui();
                    break;
                case "command":
                    ItemStack parseStack = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = parseStack.getItemMeta();

                    ArrayList<String> lore = new ArrayList<>();

                    int index = 0;
                    while (index < msg.length()) {
                        lore.add("§a/" + msg.substring(index, Math.min(index + 30, msg.length())));
                        index += 30;
                    }
                    meta.setLore(lore);
                    meta.setDisplayName(Lang.COMMAND.getConfigValue());
                    parseStack.setItemMeta(meta);

                    p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + "&8Command &5" + msg + "&8 saved to your inventory."));
                    instance.inEditor.remove(p);
                    edit.open(false, parseStack);
                    break;
                default:
                    e.setCancelled(false);
                    break;
            }
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerChatEvent event) {
        try {
            if (event.getMessage().equalsIgnoreCase("/kits") || event.getMessage().equalsIgnoreCase("/kit")) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            Debugger.runReport(e);
        }
    }

}
