package org.seed419.founddiamonds;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.seed419.founddiamonds.file.Config;
import org.seed419.founddiamonds.file.FileHandler;
import org.seed419.founddiamonds.file.FileUtils;
import org.seed419.founddiamonds.handlers.*;
import org.seed419.founddiamonds.listeners.*;
import org.seed419.founddiamonds.metrics.MetricsLite;
import org.seed419.founddiamonds.sql.MySQL;

import java.io.IOException;
import java.util.logging.Logger;

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

public class FoundDiamonds extends JavaPlugin {


    private Logger log;
    private final BlockPlaceListener blockPlaceListener = new BlockPlaceListener(this);
    private final AnnouncementListener blockBreakListener = new AnnouncementListener(this);
    private final PlayerDamageListener playerDamageListener = new PlayerDamageListener(this);
    private final PistonListener pistonListener = new PistonListener(this);
    private final TrapListener trapListener = new TrapListener(this);
    private final BroadcastHandler broadcastHandler = new BroadcastHandler(this);
    private final AdminMessageHandler adminMessageHandler = new AdminMessageHandler(this);
    private final LightLevelListener lightLevelListener = new LightLevelListener(this);
    private final LightLevelHandler lightLevelHandler = new LightLevelHandler(this);
    private final FileUtils fileUtils = new FileUtils(this);
    private final MySQL mysql = new MySQL(this);
    private final MapHandler mapHandler = new MapHandler(this);
    private final Permissions permissions = new Permissions(this);
    private final WorldHandler worldHandler = new WorldHandler(this);
    private final LoggingHandler loggingHandler = new LoggingHandler(this);
    private final TrapHandler trapHandler = new TrapHandler(this);
    private final FileHandler fileHandler = new FileHandler(this);
    private final PotionHandler potionHandler = new PotionHandler(this);
    private final ItemHandler itemHandler = new ItemHandler(this);
    private final MenuHandler menuHandler = new MenuHandler(this);
    private final BlockCounter blockTotal = new BlockCounter(this);

    /*
    Bugs:
     */

   /*
   TODO:
    MenuHandler set area?
    Is cleanlogging in SQL a popular request?
    */

   /*
   Changelog:
    Tons of refactoring, much cleaner code.  Pull requests and maintenance should be much easier in the future
    Added an option for awarding all players items/potions, or just the player who found the diamonds.
    Fixed adding and removing worlds with spaces in their names.
    Removed config option for admin alerts on trap breaks.  Why would admins not want to know about this?
    Pistons can no longer fool trap blocks.
    Improved a few redundant and sloppy areas of code.  Generic memory/performance enhancements.
    Fixed permissions bug with world management.
    Prevented setting traps with really nonsense stuff like lava, sand, torches, etc
    Removed logging commands to console because bukkit apparently does this natively now.
    Removed @prefix@ from the default config.  It seems a bit excessive...(although it will still work if you want it)
    Fixed triggering trap blocks from crashing the client when kicked (although that's pretty funny actually)
    Added silent mode for light level (doesn't let the player know they're being watched or cancel the event!)
    Light level admin messages now have an option and can't be spammed. (Well, not the same block)
    Added a command to forget placed blocks for easier testing and debugging (requires fd.* or OP)
    Removed material data idea IE 54:3 because there's not a high demand for it and there's no easy way to do this.
    */


   /*
   Test:

    */

    @Override
    public void onEnable() {
        log = this.getLogger();
        fileHandler.initFileVariables();
        fileHandler.checkFiles();
        worldHandler.checkWorlds();
        mapHandler.loadAllBlocks();
        getCommand("fd").setExecutor(new CommandHandler(this));
        registerEvents();
        startMetrics();
        mysql.getConnection();
        log.info("Enabled");
    }

    @Override
    public void onDisable() {
        log.info("Saving all data...");
        fileHandler.saveFlatFileData();
        log.info("Disabled");
    }

    public void registerEvents() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerDamageListener, this);
        pm.registerEvents(blockBreakListener, this);
        pm.registerEvents(blockPlaceListener, this);
        pm.registerEvents(pistonListener, this);
        pm.registerEvents(trapListener, this);
        pm.registerEvents(lightLevelListener, this);
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }

    public TrapHandler getTrapHandler() {
        return trapHandler;
    }

    public BlockPlaceListener getBlockPlaceListener() {
        return blockPlaceListener;
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }

    public PotionHandler getPotionHandler() {
        return potionHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public BroadcastHandler getBroadcastHandler() {
        return broadcastHandler;
    }

    public AdminMessageHandler getAdminMessageHandler() {
        return adminMessageHandler;
    }

    public LightLevelHandler getLightLevelHandler() {
        return lightLevelHandler;
    }

    public BlockCounter getBlockCounter() {
        return blockTotal;
    }

    public Logger getLog() {
        return log;
    }

    public PluginDescriptionFile getPdf() {
        return this.getDescription();
    }

    private void startMetrics() {
        if (getConfig().getBoolean(Config.metrics)) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } catch (IOException e) {}
        }
    }
}
