package com.songoda.kitpreview.events;

import com.songoda.arconix.Arconix;
import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.kitpreview.KitPreview;
import com.songoda.kitpreview.kits.BlockEditor;
import com.songoda.kitpreview.kits.Kit;
import com.songoda.kitpreview.Lang;
import com.songoda.kitpreview.utils.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public class InteractListeners implements Listener {

    private final KitPreview instance;

    public InteractListeners(KitPreview instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        try {
            boolean chand = true; // This needs to be out of my code.
            if (!KitPreview.getInstance().v1_7 && !KitPreview.getInstance().v1_8) {
                if (e.getHand() != EquipmentSlot.HAND) {
                    chand = false;
                }
            }

            Block b = e.getClickedBlock();
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!chand) return;
                String loc = Arconix.pl().serialize().serializeLocation(b);
                if (instance.getConfig().getString("data.block." + loc) == null) return;
                Player p = e.getPlayer();

                String type = instance.getConfig().getString("data.type." + loc);

                if (p.isSneaking()) return;
                e.setCancelled(true);
                Kit kit = new Kit(b.getLocation());

                if (type != null) {
                    if (type.equals("crate")) {
                        if (p.getItemInHand() != null && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                            e.setCancelled(true);
                            kit.give(p, true, false, false);
                        } else {
                            p.sendMessage(TextComponent.formatText(instance.references.getPrefix() + Lang.NOT_KEY.getConfigValue()));
                        }
                    } else if (type.equals("daily")) {
                        if (!p.hasPermission("essentials.kits." + kit.getName().toLowerCase())) {
                            p.sendMessage(instance.references.getPrefix() + Lang.NO_PERM.getConfigValue());
                            return;
                        }
                        if (instance.hooks.isReady(p, kit.getName())) {
                            kit.give(p, false, false, false);
                            instance.hooks.updateDelay(p, kit.getName());
                        } else {
                            long time = instance.hooks.getNextUse(kit.getName(), p);
                            long now = System.currentTimeMillis();

                            long then = time - now;
                            p.sendMessage(instance.references.getPrefix() + Lang.NOT_YET.getConfigValue(Arconix.pl().format().readableTime(then)));
                        }
                    }
                } else if (instance.getConfig().getString("data.kit." + kit.getName() + ".link") != null || instance.getConfig().getString("data.kit." + kit.getName() + ".eco") != null) {
                    kit.buy(p);
                } else {
                    p.sendMessage("data.kit." + kit.getName() + ".link");
                    kit.display(p, false);
                }
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player p = e.getPlayer();
                String loc = Arconix.pl().serialize().serializeLocation(b);

                if (!chand) return;
                if (instance.getConfig().getString("data.block." + loc) == null) return;

                if (b.getState() instanceof InventoryHolder || b.getType() == Material.ENDER_CHEST) {
                    e.setCancelled(true);
                }
                if (p.isSneaking() && p.hasPermission("kitpreview.admin")) {
                    BlockEditor edit = new BlockEditor(b, p);
                    edit.open();
                    return;
                }
                Kit kit = new Kit(b.getLocation());
                if (p.getItemInHand() != null && p.getItemInHand().getType() != null && p.getItemInHand().getType() == Material.TRIPWIRE_HOOK) {
                    e.setCancelled(true);
                    kit.give(p, true, false, false);
                    return;
                }
                kit.display(p, false);

            }
        } catch (Exception x) {
            Debugger.runReport(x);
        }
    }
}

