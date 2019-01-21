package me.prestige.bases;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.bizarrealex.aether.Aether;
import com.customhcf.base.BasePlugin;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.prestige.bases.command.CreateArenaCommand;
import me.prestige.bases.command.ForceStartCommand;
import me.prestige.bases.economy.EconomyCommand;
import me.prestige.bases.economy.EconomyListener;
import me.prestige.bases.economy.EconomyManager;
import me.prestige.bases.economy.EconomyRunnable;
import me.prestige.bases.economy.FlatFileEconomyManager;
import me.prestige.bases.economy.PayCommand;
import me.prestige.bases.economy.VillagerRunnable;
import me.prestige.bases.faction.FactionExecutor;
import me.prestige.bases.faction.FactionManager;
import me.prestige.bases.faction.FactionMember;
import me.prestige.bases.faction.FlatFileFactionManager;
import me.prestige.bases.faction.claim.Claim;
import me.prestige.bases.faction.event.PlayerJoinFactionEvent;
import me.prestige.bases.faction.struct.ChatChannel;
import me.prestige.bases.faction.struct.Role;
import me.prestige.bases.faction.type.ClaimableFaction;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.faction.type.PlayerFaction;
import me.prestige.bases.game.GameManager;
import me.prestige.bases.game.GameState;
import me.prestige.bases.inventory.InventoryHandler;
import me.prestige.bases.kothgame.CaptureZone;
import me.prestige.bases.kothgame.EventExecutor;
import me.prestige.bases.kothgame.faction.CapturableFaction;
import me.prestige.bases.kothgame.faction.KothFaction;
import me.prestige.bases.listener.ChatListener;
import me.prestige.bases.listener.CoreListener;
import me.prestige.bases.listener.DeathListener;
import me.prestige.bases.listener.EntityHandler;
import me.prestige.bases.listener.FactionListener;
import me.prestige.bases.listener.FactionsCoreListener;
import me.prestige.bases.listener.JoinListener;
import me.prestige.bases.listener.LobbyListener;
import me.prestige.bases.listener.NametagListener;
import me.prestige.bases.listener.PlayerStatsListener;
import me.prestige.bases.listener.SkullListener;
import me.prestige.bases.listener.WaitingSnapshotListener;
import me.prestige.bases.listener.WorldListener;
import me.prestige.bases.nms.Nms;
import me.prestige.bases.nms.Villager;
import me.prestige.bases.scoreboard.ScoreboardHandler;
import me.prestige.bases.scoreboard.provider.GameProvider;
import me.prestige.bases.tab.FactionTabManager;
import me.prestige.bases.timer.TimerExecutor;
import me.prestige.bases.timer.TimerManager;
import me.prestige.bases.user.FactionUser;
import me.prestige.bases.user.UserManager;
import net.minecraft.server.v1_7_R4.EntityVillager;
import net.minecraft.util.com.google.gson.Gson;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class Bases extends JavaPlugin
{

	private FactionManager factionManager;
	private GameManager gameManager;
	private TimerManager timerManager;
	private ScoreboardHandler scoreboardHandler;
	private InventoryHandler inventoryHandler;
	private WorldEditPlugin worldEditPlugin;
	private UserManager userManager;
	private EconomyManager economyManager;
	private EconomyRunnable economyRunnable;

	private String serverName;
	private String scoreboardTitle;
	private String mapName;
	private Aether aether;
	private static Bases plugin;

	public static String getRemaining(final long millis, final boolean milliseconds)
	{
		return getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail)
	{
		if (milliseconds && duration < TimeUnit.MINUTES.toMillis(1L)) { return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's'; }
		return DurationFormatUtils.formatDuration(duration, ((duration >= TimeUnit.HOURS.toMillis(1L)) ? "HH:" : "") + "mm:ss");
	}

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new NametagListener(), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		plugin = this;
		aether = new Aether(this, new GameProvider(this));
		if (!new File(getDataFolder(), "config.yml").exists())
		{
			saveDefaultConfig();
		}
		getConfig().options().copyDefaults(true);
		for (Player on : Bukkit.getOnlinePlayers())
		{
			on.kickPlayer("Resetting");
		}
		this.serverName = getConfig().getString("server-name");
		this.mapName = ChatColor.translateAlternateColorCodes('&', getConfig().getString("map-name"));

		scoreboardTitle = ChatColor.translateAlternateColorCodes('&', getConfig().getString("scoreboard.title"));
		System.out.println("Server Name set to " + serverName);
		Nms.registerEntity("Villager", 120, EntityVillager.class, Villager.class);
		final Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
		worldEditPlugin = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin) wep) : null);

		loadConfiguations();
		loadCommands();
		loadManagers();
		loadListeners();
		new VillagerRunnable().runTaskTimer(plugin, 1, 1);
		economyRunnable = new EconomyRunnable(this);
		economyRunnable.runTaskTimerAsynchronously(this, 60, 60);
		reserverRedis();
		listenRedis();
		new FactionTabManager(this).init();
	}

	private void loadConfiguations()
	{
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(ColorFaction.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ColorFaction.BlueFaction.class);
		ConfigurationSerialization.registerClass(ColorFaction.GreenFaction.class);
		ConfigurationSerialization.registerClass(ColorFaction.YellowFaction.class);
		ConfigurationSerialization.registerClass(ColorFaction.RedFaction.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
	}

	private void loadManagers()
	{
		factionManager = new FlatFileFactionManager(this);
		gameManager = new GameManager(this);
		timerManager = new TimerManager(this);
		inventoryHandler = new InventoryHandler(this);
		scoreboardHandler = new ScoreboardHandler(this);
		userManager = new UserManager(this);
		economyManager = new FlatFileEconomyManager(this);
	}

	private void loadListeners()
	{
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new CoreListener(this), this);
		pm.registerEvents(new FactionListener(this), this);
		pm.registerEvents(new FactionsCoreListener(this), this);
		pm.registerEvents(new JoinListener(this), this);
		pm.registerEvents(new WorldListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		pm.registerEvents(new EconomyListener(this), this);
		pm.registerEvents(new EntityHandler(this), this);
		pm.registerEvents(new WaitingSnapshotListener(this), this);
		pm.registerEvents(new SkullListener(), this);
		pm.registerEvents(new LobbyListener(this), this);
		pm.registerEvents(new PlayerStatsListener(this), this);

	}

	private void loadCommands()
	{
		getCommand("game").setExecutor(new EventExecutor(this));
		getCommand("createarena").setExecutor(new CreateArenaCommand());
		getCommand("faction").setExecutor(new FactionExecutor(this));
		getCommand("forcestart").setExecutor(new ForceStartCommand());
		getCommand("timer").setExecutor(new TimerExecutor(this));
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("pay").setExecutor(new PayCommand(this));
		final Map<String, Map<String, Object>> map = getDescription().getCommands();
		for (final Map.Entry<String, Map<String, Object>> entry : map.entrySet())
		{
			final PluginCommand command = getCommand(entry.getKey());
			command.setPermission("command." + entry.getKey());
			command.setPermissionMessage(ChatColor.RED + "You do not have permission for this command.");
		}
	}

	private void saveData()
	{
		userManager.saveUserData();
		factionManager.saveFactionData();

	}

	@Override
	public void onDisable()
	{
		Bukkit.getWorld("world").getEntities().clear();
		saveData();
		if (!gameManager.getGameState().equals(GameState.ENDING))
		{
			gameManager.end(null);
		}
		plugin = null;
	}

	public EconomyRunnable getEconomyRunnable()
	{
		return economyRunnable;
	}

	public static Bases getPlugin()
	{
		return plugin;
	}

	public EconomyManager getEconomyManager()
	{
		return economyManager;
	}

	public UserManager getUserManager()
	{
		return userManager;
	}

	public GameManager getGameManager()
	{
		return gameManager;
	}

	public ScoreboardHandler getScoreboardHandler()
	{
		return scoreboardHandler;
	}

	public FactionManager getFactionManager()
	{
		return factionManager;
	}

	public TimerManager getTimerManager()
	{
		return timerManager;
	}

	public InventoryHandler getInventoryHandler()
	{
		return inventoryHandler;
	}

	public WorldEditPlugin getWorldEditPlugin()
	{
		return worldEditPlugin;
	}

	public String getScoreboardTitle()
	{
		return scoreboardTitle;
	}

	public String getMapName()
	{
		return mapName;
	}

	public void listenRedis()
	{

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				while (getGameManager().getGameState() == GameState.WAITING)
				{
					try
					{
						try (Jedis jedis = BasePlugin.getPlugin().getRedis().getResource())
						{
							jedis.subscribe(new BinaryJedisPubSub()
							{
								@Override
								public void onMessage(byte[] channel, byte[] message)
								{
									ByteArrayDataInput input = ByteStreams.newDataInput(message);
									String subchannel = input.readUTF();
									if (subchannel.equals("JoinOpenFaction"))
									{
										int amount = input.readInt();
										ColorFaction working = null;
										if (plugin.getFactionManager().getColorFaction("Green").getMembers().size() + amount < 5)
										{
											working = plugin.getFactionManager().getColorFaction("Green");
										}
										else if (plugin.getFactionManager().getColorFaction("Yellow").getMembers().size() + amount < 5)
										{
											working = plugin.getFactionManager().getColorFaction("Yellow");
										}
										else if (plugin.getFactionManager().getColorFaction("Red").getMembers().size() + amount < 5)
										{
											working = plugin.getFactionManager().getColorFaction("Red");
										}
										else if (plugin.getFactionManager().getColorFaction("Blue").getMembers().size() + amount < 5)
										{
											working = plugin.getFactionManager().getColorFaction("Blue");
										}
										else
										{
											return;
										}
										if (working == null) { return; }
										for (int i = 0; i < amount; i++)
										{
											String name = input.readUTF();
											UUID uuid = UUID.fromString(input.readUTF());
											if (getFactionManager().getColorFaction(uuid) == working)
											{
												continue;
											}
											ensureLeaveFromOthers(uuid, working);
											if (working.setMember(uuid, new FactionMember(uuid, ChatChannel.FACTION, Role.MEMBER)))
											{
												Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(uuid, working, plugin.getFactionManager().getColorFaction(uuid)));
												Player player = Bukkit.getPlayer(uuid);
												if (player != null)
												{
													player.sendMessage(net.md_5.bungee.api.ChatColor.YELLOW + "You have joined " + plugin.getFactionManager().getColorFaction(player.getUniqueId()).getDisplayName(player));
												}
											}
										}
									}
								}
							}, ("bases-" + serverName).getBytes(StandardCharsets.UTF_8));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					try
					{
						Thread.sleep(10000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(this);
	}

	public void deleteRedis()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				deleteSync();
			}

		}.runTaskAsynchronously(this);
	}

	public void deleteSync()
	{
		try (Jedis jedis = BasePlugin.getPlugin().getRedis().getResource())
		{
			jedis.set("bases-lock", serverName, "NX", "PX", 20000);
			if (jedis.get("bases-lock").equals(serverName))
			{
				jedis.del("bases-lock");
				jedis.del("bases-data-" + serverName);
				jedis.zrem("bases-lock-queue", serverName);
			}
		}
	}

	public class BasesBeen
	{
		public int greenCount;
		public int blueCount;
		public int yellowCount;
		public int redCount;
	}

	public void reserverRedis()
	{

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				deleteSync();
				while (getGameManager().getGameState() == GameState.WAITING)
				{
					try
					{
						try (Jedis jedis = BasePlugin.getPlugin().getRedis().getResource())
						{
							jedis.set("keepalive-" + serverName, String.valueOf(System.currentTimeMillis()));
							Double value = jedis.zscore("bases-lock-queue", serverName);
							if (value != null)
							{
								Tuple tuple = jedis.zrangeByScoreWithScores("bases-lock-queue", "-inf", "+inf", 0, 1).iterator().next();
								if (tuple.getElement().equals(serverName))
								{
									jedis.setex("bases-lock", 20, serverName);
								}
								Tuple second = jedis.zrangeByScoreWithScores("bases-lock-queue", "-inf", "+inf", 1, 1).iterator().next();
								if (second.getElement().equals(serverName))
								{
									String keepAliveString = jedis.get("keepalive-" + tuple.getElement());
									if (keepAliveString == null || System.currentTimeMillis() - Long.valueOf(keepAliveString) > 15000)
									{
										jedis.zrem("bases-lock-queue", tuple.getElement());
										System.out.println("Removing a crashed node from the queue: " + tuple.getElement());
									}
								}
							}
							else
							{
								jedis.zadd("bases-lock-queue", System.currentTimeMillis(), serverName);
							}
							BasesBeen basesBeen = new BasesBeen();
							basesBeen.greenCount = plugin.getFactionManager().getColorFaction("Green").getMembers().size();
							basesBeen.blueCount = plugin.getFactionManager().getColorFaction("Blue").getMembers().size();
							basesBeen.yellowCount = plugin.getFactionManager().getColorFaction("Yellow").getMembers().size();
							basesBeen.redCount = plugin.getFactionManager().getColorFaction("Red").getMembers().size();
							jedis.setex("bases-data-" + serverName, 20, new Gson().toJson(basesBeen));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(this);
	}

	public void ensureLeaveFromOthers(UUID player, ColorFaction toJoin)
	{

		ColorFaction green = plugin.getFactionManager().getColorFaction("Green");
		ColorFaction blue = plugin.getFactionManager().getColorFaction("Blue");
		ColorFaction yellow = plugin.getFactionManager().getColorFaction("Yellow");
		ColorFaction red = plugin.getFactionManager().getColorFaction("Red");
		if (green != toJoin && green.getMember(player) != null)
		{
			green.setMember(player, null, true);
		}
		if (blue != toJoin && blue.getMember(player) != null)
		{
			blue.setMember(player, null, true);
		}
		if (yellow != toJoin && yellow.getMember(player) != null)
		{
			yellow.setMember(player, null, true);
		}
		if (red != toJoin && red.getMember(player) != null)
		{
			red.setMember(player, null, true);
		}
	}
}
