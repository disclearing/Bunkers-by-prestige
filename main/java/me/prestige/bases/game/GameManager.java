package me.prestige.bases.game;

import com.customhcf.util.ItemBuilder;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import me.prestige.bases.Bases;
import me.prestige.bases.KothStartTimer;
import me.prestige.bases.economy.EconomyListener;
import me.prestige.bases.faction.FactionMember;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.listener.WorldListener;
import me.prestige.bases.nms.Villager;
import me.prestige.bases.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.Village;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GameManager {

    private Bases plugin;
    private GameState gameState;
    private long gameStartTimeStamp;
    private Set<ColorFaction> deadFactions = new HashSet<>();

    public GameManager(Bases plugin) {
        this.plugin = plugin;
        gameState = GameState.WAITING;
        if (!Bases.getPlugin().getConfig().getBoolean("setup")) {
            loadArena();
        }
    }

    public void end(ColorFaction colorFaction) {
        if (!gameState.equals(GameState.STARTING)) {
            return;
        }

        gameStartTimeStamp = 0;
        gameState = GameState.ENDING;
        String winner;
        if (colorFaction == null) {
            winner = "nobody " + ChatColor.RED + "[Forced End]";
        } else {
            winner = colorFaction.getDisplayName(Bukkit.getConsoleSender());
        }
        for (Player on : Bukkit.getOnlinePlayers()) {
            on.kickPlayer(ChatColor.YELLOW + "The winner is " + winner);
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == EntityType.PLAYER) continue;
                entity.remove();
            }
        }

    }


    public void startGame() {
        this.gameStartTimeStamp = System.currentTimeMillis();
        gameState = GameState.STARTING;
        plugin.deleteRedis();
        new KothStartTimer().runTaskLater(plugin, 3 * 60 * 20);
        for(FactionUser user : plugin.getUserManager().getUsers().values()){
            user.setKills(0);
            user.setDeaths(0);
        }
        for (Faction colorFaction : plugin.getFactionManager().getFactions()) {
            if (colorFaction instanceof ColorFaction) {
                for (Player on : ((ColorFaction) colorFaction).getOnlinePlayers()) {
                    plugin.getEconomyManager().setBalance(on.getUniqueId(), 500);
                    on.getInventory().clear();
                    on.getInventory().setArmorContents(null);
                    on.setHealth(20);
                    on.setFoodLevel(20);
                    on.setGameMode(GameMode.SURVIVAL);
                    on.getInventory().addItem(new ItemBuilder(Material.STONE_PICKAXE).lore(ChatColor.GOLD + "Soul Bound").build());
                    on.getInventory().addItem(new ItemBuilder(Material.COOKED_BEEF, 16).build());
                    on.getInventory().addItem(new ItemBuilder(Material.STONE_AXE).lore(ChatColor.GOLD + "Soul Bound").build());
                    on.teleport(((ColorFaction) colorFaction).getHome().clone().add(0 , 0.5 ,0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                switch (((ColorFaction) colorFaction).getColor()) {
                    case GREEN: {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (spawnVillager(((ColorFaction) colorFaction).getBlockVillager(), ((ColorFaction) colorFaction).getColor() + "Build Shop") && spawnVillager(((ColorFaction) colorFaction).getBuyVillager(), ((ColorFaction) colorFaction).getColor() + "Combat Shop") &&
                                        spawnVillager(((ColorFaction) colorFaction).getSellVillager(), ((ColorFaction) colorFaction).getColor() + "Sell Items") && spawnVillager(((ColorFaction) colorFaction).getEnchantVillager(), ((ColorFaction) colorFaction).getColor() + "Tim the Enchanter")) {
                                    ((ColorFaction) colorFaction).setHasVillager(true);
                                } else {
                                    System.out.println("Failed to spawn " + ((ColorFaction) colorFaction).getDisplayName(Bukkit.getConsoleSender()));
                                    ((ColorFaction) colorFaction).setHasVillager(false);
                                }
                            }
                        }.runTaskLater(plugin, 20);
                        break;
                    }
                    case RED: {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (spawnVillager(((ColorFaction) colorFaction).getBlockVillager(), ((ColorFaction) colorFaction).getColor() + "Build Shop") && spawnVillager(((ColorFaction) colorFaction).getBuyVillager(), ((ColorFaction) colorFaction).getColor() + "Combat Shop") &&
                                        spawnVillager(((ColorFaction) colorFaction).getSellVillager(), ((ColorFaction) colorFaction).getColor() + "Sell Items") && spawnVillager(((ColorFaction) colorFaction).getEnchantVillager(), ((ColorFaction) colorFaction).getColor() + "Tim the Enchanter")) {
                                    ((ColorFaction) colorFaction).setHasVillager(true);
                                } else {
                                    System.out.println("Failed to spawn " + ((ColorFaction) colorFaction).getDisplayName(Bukkit.getConsoleSender()));
                                    ((ColorFaction) colorFaction).setHasVillager(false);
                                }
                            }
                        }.runTaskLater(plugin, 40);
                        break;
                    }
                    case BLUE: {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (spawnVillager(((ColorFaction) colorFaction).getBlockVillager(), ((ColorFaction) colorFaction).getColor() + "Build Shop") && spawnVillager(((ColorFaction) colorFaction).getBuyVillager(), ((ColorFaction) colorFaction).getColor() + "Combat Shop") &&
                                        spawnVillager(((ColorFaction) colorFaction).getSellVillager(), ((ColorFaction) colorFaction).getColor() + "Sell Items") && spawnVillager(((ColorFaction) colorFaction).getEnchantVillager(), ((ColorFaction) colorFaction).getColor() + "Tim the Enchanter")) {
                                    ((ColorFaction) colorFaction).setHasVillager(true);
                                } else {
                                    System.out.println("Failed to spawn " + ((ColorFaction) colorFaction).getDisplayName(Bukkit.getConsoleSender()));
                                    ((ColorFaction) colorFaction).setHasVillager(false);
                                }
                            }
                        }.runTaskLater(plugin, 60);
                        break;
                    }
                    case YELLOW: {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (spawnVillager(((ColorFaction) colorFaction).getBlockVillager(), ((ColorFaction) colorFaction).getColor() + "Build Shop") && spawnVillager(((ColorFaction) colorFaction).getBuyVillager(), ((ColorFaction) colorFaction).getColor() + "Combat Shop") &&
                                        spawnVillager(((ColorFaction) colorFaction).getSellVillager(), ((ColorFaction) colorFaction).getColor() + "Sell Items") && spawnVillager(((ColorFaction) colorFaction).getEnchantVillager(), ((ColorFaction) colorFaction).getColor() + "Tim the Enchanter")) {
                                    ((ColorFaction) colorFaction).setHasVillager(true);
                                } else {
                                    System.out.println("Failed to spawn " + ((ColorFaction) colorFaction).getDisplayName(Bukkit.getConsoleSender()));
                                    ((ColorFaction) colorFaction).setHasVillager(false);
                                }
                            }
                        }.runTaskLater(plugin, 0);
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }
        }
    }


    public void saveArena(Location l1, Location l2) {
        //   Location l1 = new Location(Bukkit.getWorld("world"), 127.476, 69.00, -12.508);// Location representing one corner of the region
        //   Location l2 = new Location(Bukkit.getWorld("world"), 156.554, 69.00, -32.518); // Location representing the corner opposite to <l1>
        if (plugin.getWorldEditPlugin() == null)
            return;

// OR - without needing an associated Player
        TerrainManager tm = new TerrainManager(plugin.getWorldEditPlugin(), Bukkit.getWorld("world"));

// don't include an extension - TerrainManager will auto-add ".schematic"
        File saveFile = new File(plugin.getDataFolder(), "testWorld");

// save the terrain to a schematic file
        try {
            tm.saveTerrain(saveFile, l1, l2);
        } catch (FilenameException e) {
            // thrown by WorldEdit - it doesn't like the file name/location etc.
        } catch (DataException e) {
            // thrown by WorldEdit - problem with the data
        } catch (IOException e) {
            // problem with creating/writing to the file
        }
    }

    public void loadArena() {
        Preconditions.checkNotNull(plugin.getWorldEditPlugin());
        TerrainManager tm = new TerrainManager(plugin.getWorldEditPlugin(), Bukkit.getWorld("world"));
        Preconditions.checkNotNull(tm);
// don't include an extension - TerrainManager will auto-add ".schematic"
        File saveFile = new File(plugin.getDataFolder(), "testWorld");
        Preconditions.checkNotNull(saveFile);

// reload a schematic
        try {
            tm.loadSchematic(saveFile);
        } catch (FilenameException e) {
            // thrown by WorldEdit - it doesn't like the file name/location etc.
        } catch (DataException e) {
            // thrown by WorldEdit - problem with the data
        } catch (IOException e) {
            // problem with opening/reading the file
        } catch (MaxChangedBlocksException e) {
            // thrown by WorldEdit - the schematic is larger than the configured block limit for the player
        } catch (EmptyClipboardException e) {
// thrown by WorldEdit - should be self-explanatory
        }
    }

    public boolean spawnVillager(Location loc, String name) {
        loc.getChunk().load();

        final WorldServer mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final Villager customEnt = new Villager(mcWorld);
        customEnt.setCustomName(name);
        customEnt.setCustomNameVisible(true);
        customEnt.setJob(name);
        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        customEnt.setPosition(loc.getX(), loc.getY(), loc.getZ());

        boolean result = mcWorld.addEntity(customEnt, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if(result){
            Iterator<Map.Entry<Villager, Long>> iterator = EconomyListener.respawners.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<Villager, Long> entry = iterator.next();
                if(entry.getKey().getBukkitEntity().getLocation().distanceSquared(loc) <= 4){
                    if(ChatColor.stripColor(entry.getKey().getCustomName()).contains(ChatColor.stripColor(name))){
                        iterator.remove();
                        System.out.println("Removing from respawnee: " + name);
                    }
                }
            }
            for(Entity entity : customEnt.getBukkitEntity().getNearbyEntities(4 , 4, 4)){
                if(entity instanceof LivingEntity && !(entity instanceof  Player) && entity != customEnt.getBukkitEntity()){
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if(livingEntity.getCustomName() != null && ChatColor.stripColor(livingEntity.getCustomName()).contains(ChatColor.stripColor(name))){
                        livingEntity.remove();
                        System.out.println("Removing from already spawned: " + name);
                    }
                }
            }
        }
        return result;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public long getGameTimer() {
        return gameStartTimeStamp;
    }

    public Set<ColorFaction> getDeadFactions() {
        return deadFactions;
    }
}
