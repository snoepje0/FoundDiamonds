package org.seed419.founddiamonds.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.seed419.founddiamonds.FoundDiamonds;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.util.Prefix;

import java.util.HashSet;
import java.util.Set;

/**
 * Attribute Only (Public) License
 * Version 0.a3, July 11, 2011
 * <p/>
 * Copyright (C) 2012 Blake Bartenbach <seed419@gmail.com> (@seed419)
 * <p/>
 * Anyone is allowed to copy and distribute verbatim or modified
 * copies of this license document and altering is allowed as long
 * as you attribute the author(s) of this license document / files.
 * <p/>
 * ATTRIBUTE ONLY PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * <p/>
 * 1. Attribute anyone attached to the license document.
 * Do not remove pre-existing attributes.
 * <p/>
 * Plausible attribution methods:
 * 1. Through comment blocks.
 * 2. Referencing on a site, wiki, or about page.
 * <p/>
 * 2. Do whatever you want as long as you don't invalidate 1.
 *
 * @license AOL v.a3 <http://aol.nexua.org>
 */
public class TrapHandler {


    private FoundDiamonds fd;
    private final Set<Location> trapBlocks = new HashSet<Location>();


    public TrapHandler(FoundDiamonds fd) {
        this.fd = fd;
    }

    public void handleTrap(Player player, String[] args) {
        Location playerLoc = player.getLocation();
        Material trap;
        String item;
        int depth = 0;
        if (args.length == 1) {
            trap = Material.DIAMOND_ORE;
            item = "Diamond ore";
        } else if (args.length == 2) {	//either trap block specified, old format, or depth specified, assuming diamond blocks
            item = args[1];
            trap = Material.matchMaterial(item);
            if(trap==null) {
            	try {
            		depth = Integer.parseInt(args[1]);
            	}catch(NumberFormatException ex) {
            		player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
            		return;
            	}
            	item = "Diamond ore";
            	trap = Material.DIAMOND_ORE;
            }
        } else if (args.length == 3) {	//either new block format specification, or depth + old block formatting
            item = args[1] + "_" + args[2];
            trap = Material.matchMaterial(item);
            if(trap == null) {
            	try {
            		depth = Integer.parseInt(args[2]);
            	}catch(NumberFormatException ex) {
               		player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
            		return;
            	}
            	item = args[1];
            	trap = Material.matchMaterial(item);
            }
        }else if(args.length == 4) {	//new block format + depth
            item = args[1] + "_" + args[2];
            trap = Material.matchMaterial(item);
            try {
        		depth = Integer.parseInt(args[3]);
        	}catch(NumberFormatException ex) {
           		player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Invalid arguments");
        		return;
        	}
        } else {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Invalid number of arguments");
            player.sendMessage(ChatColor.RED + "Is it a block and a valid item? Try /fd trap gold ore");
            return;
        }
        if (trap != null && trap.isBlock()) {
            if (isSensibleTrapBlock(trap)) {
                getTrapLocations(player, playerLoc, trap, depth);
            } else {
                player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + "Unable to set a trap with " + item);
                player.sendMessage(ChatColor.RED + "Surely you can use a more sensible block for a trap.");
            }
        } else {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " Unable to set a trap with '" + item + "'");
            player.sendMessage(ChatColor.RED + "Is it a valid trap block? Try /fd trap gold ore");
        }
    }

    private void getTrapLocations(Player player, final Location playerLoc, Material trap, int depth) {
        int x = playerLoc.getBlockX();
        int y = playerLoc.getBlockY() - depth;
        int maxHeight = player.getWorld().getMaxHeight();
        if ((y - 2) < 0) {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap down there, sorry.");
            return;
        } else if ((y - 1) > maxHeight) {
            player.sendMessage(Prefix.getChatPrefix() + ChatColor.RED + " I can't place a trap this high, sorry.");
            return;
        }
        int z = playerLoc.getBlockZ();
        World world = player.getWorld();
        if (trap == Material.EMERALD_ORE) {
            Block block = world.getBlockAt(x, (y-1), z);
            setEmeraldTrap(player, block);
            return;
        }
        int randomnumber = (int)(Math.random() * 100);
        if ((randomnumber >= 0) && randomnumber < 50) {
            Block block1 = world.getBlockAt(x, y - 1, z);
            Block block2 = world.getBlockAt(x, y - 2, z + 1);
            Block block3 = world.getBlockAt(x - 1, y - 2, z);
            Block block4 = world.getBlockAt(x, y - 2, z);
            handleTrapBlocks(player, trap, block1, block2, block3, block4);
        } else if (randomnumber >= 50) {
            Block block1 = world.getBlockAt(x, y - 1, z);
            Block block2 = world.getBlockAt(x - 1, y - 2, z);
            Block block3 = world.getBlockAt(x , y - 2, z);
            Block block4 = world.getBlockAt(x -1, y - 1, z);
            handleTrapBlocks(player, trap, block1, block2, block3, block4);
        }
    }

    private boolean isSensibleTrapBlock(Material trap) {
        switch (trap) {
            case TORCH:
            case GRAVEL:
            case SAND:
            case DIRT:
            case GRASS:
            case VINE:
            case LEAVES:
            case DEAD_BUSH:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
            case WATER:
            case LAVA:
                return false;
            default:
                return true;
        }
    }

    private void setEmeraldTrap(Player player, Block block) {
        trapBlocks.add(block.getLocation());
        block.setType(Material.EMERALD_ORE);
        player.sendMessage(Prefix.getChatPrefix() + ChatColor.AQUA + " Trap set using " + Material.EMERALD_ORE.name().toLowerCase().replace("_", " "));
    }

    private void handleTrapBlocks(Player player, Material trap, Block block1, Block block2, Block block3, Block block4) {
        trapBlocks.add(block1.getLocation());
        trapBlocks.add(block2.getLocation());
        trapBlocks.add(block3.getLocation());
        trapBlocks.add(block4.getLocation());
        block1.setType(trap);
        block2.setType(trap);
        block3.setType(trap);
        block4.setType(trap);
        player.sendMessage(Prefix.getChatPrefix() + ChatColor.AQUA + " Trap set using " + trap.name().toLowerCase().replace("_", " "));
    }

    public boolean isTrapBlock(Location loc) {
        return trapBlocks.contains(loc);
    }

    private void removeTrapBlock(Block block) {
        trapBlocks.remove(block.getLocation());
    }

    public void handleTrapBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (fd.getPermissions().hasPerm(player, "fd.trap")) {
            player.sendMessage(ChatColor.AQUA + "Trap block removed");
            block.setType(Material.AIR);
            removeTrapBlock(block);
        } else {
            String trapMessage = ChatColor.YELLOW + player.getName()
                    + ChatColor.RED + " just triggered a trap block";
            for (Player x: fd.getServer().getOnlinePlayers()) {
                if((fd.getPermissions().hasPerm(x, "fd.trap")) || fd.getPermissions().hasPerm(x, "fd.admin")) {
                    x.sendMessage(trapMessage);
                }
            }
            fd.getServer().getConsoleSender().sendMessage(Prefix.getLoggingPrefix() + trapMessage);
            boolean banned = false;
            boolean kicked = false;
            if (fd.getConfig().getBoolean(Config.kickOnTrapBreak)) {
                String kickMessage = fd.getConfig().getString(Config.kickMessage);
                player.kickPlayer(kickMessage);
                kicked = true;
            }
            if (fd.getConfig().getBoolean(Config.banOnTrapBreak)) {
                player.setBanned(true);
                banned = true;
            }
            if(fd.getConfig().getBoolean(Config.logTrapBreaks)) {
                fd.getLoggingHandler().handleLogging(player, block, true, kicked, banned);
            }
        }
    }

    public Set<Location> getTrapBlocks() {
        return trapBlocks;
    }

}
