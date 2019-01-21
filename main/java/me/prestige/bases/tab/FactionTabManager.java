package me.prestige.bases.tab;

import com.customhcf.util.JavaUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.prestige.bases.Bases;
import me.prestige.bases.faction.type.ColorFaction;
import me.prestige.bases.faction.type.Faction;
import me.prestige.bases.listener.DeathListener;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FactionTabManager implements Listener
{
	private final Bases plugin;
	private String[][] texts = new String[21][3];
	private Map<Player, PlayerTab> maps = Maps.newHashMap();
	private AsyncTabParaseEvent asyncTabParaseEvent = new AsyncTabParaseEvent(null, null);

	public void reload()
	{
		texts[0][0] = ChatColor.translateAlternateColorCodes('&', "&4&lKirai.rip");
		texts[0][1] = ChatColor.translateAlternateColorCodes('&', "&c&lWarzone");
		texts[0][2] = ChatColor.translateAlternateColorCodes('&', "&4&lKirai.rip");

		texts[2][0] = ChatColor.translateAlternateColorCodes('&', "&6&lTeam Info");
		texts[3][0] = ChatColor.translateAlternateColorCodes('&', "DTR: {dtr}");
		texts[4][0] = ChatColor.translateAlternateColorCodes('&', "Online: {online}");
		texts[6][0] = ChatColor.translateAlternateColorCodes('&', "&6&lLocation");
		texts[7][0] = ChatColor.translateAlternateColorCodes('&', "{location.name}");
		texts[8][0] = ChatColor.translateAlternateColorCodes('&', "{location}");
		texts[10][0] = ChatColor.translateAlternateColorCodes('&', "&6&lGame Info");
		texts[11][0] = ChatColor.translateAlternateColorCodes('&', "&cRed&f: {red.dtr}");
		texts[12][0] = ChatColor.translateAlternateColorCodes('&', "&9Blue&f: {blue.dtr}");
		texts[13][0] = ChatColor.translateAlternateColorCodes('&', "&eYellow&f: {yellow.dtr}");
		texts[14][0] = ChatColor.translateAlternateColorCodes('&', "&aGreen&f: {green.dtr}");

		texts[2][1] = ChatColor.translateAlternateColorCodes('&', "&c&lRed Team");
		texts[3][1] = ChatColor.translateAlternateColorCodes('&', "&c{red.online.1}");
		texts[4][1] = ChatColor.translateAlternateColorCodes('&', "&c{red.online.2}");
		texts[5][1] = ChatColor.translateAlternateColorCodes('&', "&c{red.online.3}");
		texts[6][1] = ChatColor.translateAlternateColorCodes('&', "&c{red.online.4}");
		texts[7][1] = ChatColor.translateAlternateColorCodes('&', "&c{red.online.5}");
		texts[9][1] = ChatColor.translateAlternateColorCodes('&', "&a&lGreen Team");

		texts[10][1] = ChatColor.translateAlternateColorCodes('&', "&a{green.online.1}");
		texts[11][1] = ChatColor.translateAlternateColorCodes('&', "&a{green.online.2}");
		texts[12][1] = ChatColor.translateAlternateColorCodes('&', "&a{green.online.3}");
		texts[13][1] = ChatColor.translateAlternateColorCodes('&', "&a{green.online.4}");
		texts[14][1] = ChatColor.translateAlternateColorCodes('&', "&a{green.online.5}");

		texts[2][2] = ChatColor.translateAlternateColorCodes('&', "&9&lBlue Team");
		texts[3][2] = ChatColor.translateAlternateColorCodes('&', "&9{blue.online.1}");
		texts[4][2] = ChatColor.translateAlternateColorCodes('&', "&9{blue.online.2}");
		texts[5][2] = ChatColor.translateAlternateColorCodes('&', "&9{blue.online.3}");
		texts[6][2] = ChatColor.translateAlternateColorCodes('&', "&9{blue.online.4}");
		texts[7][2] = ChatColor.translateAlternateColorCodes('&', "&9{blue.online.5}");
		texts[9][2] = ChatColor.translateAlternateColorCodes('&', "&e&lYellow Team");

		texts[10][2] = ChatColor.translateAlternateColorCodes('&', "&e{yellow.online.1}");
		texts[11][2] = ChatColor.translateAlternateColorCodes('&', "&e{yellow.online.2}");
		texts[12][2] = ChatColor.translateAlternateColorCodes('&', "&e{yellow.online.3}");
		texts[13][2] = ChatColor.translateAlternateColorCodes('&', "&e{yellow.online.4}");
		texts[14][2] = ChatColor.translateAlternateColorCodes('&', "&e{yellow.online.5}");
		for (int y = 0; y < 21; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				if (texts[y][x] == null)
				{
					texts[y][x] = "";
				}
			}
		}
	}

	public void init()
	{
		reload();
		new TinyProtocol(Bases.getPlugin())
		{
			@Override
			public Object onPacketOutAsync(Player reciever, Channel channel, Object packet)
			{
				if (reciever == null || ((CraftPlayer) reciever).getHandle().playerConnection == null) { return packet; }
				if (((CraftPlayer) reciever).getHandle().playerConnection.networkManager.getVersion() >= 47) { return packet; }
				CraftPlayer craftplayer = ((CraftPlayer) reciever);
				if (packet instanceof PacketPlayOutPlayerInfo)
				{
					PacketPlayOutPlayerInfo info = (PacketPlayOutPlayerInfo) packet;
					if (!info.username.contains(String.valueOf(ChatColor.COLOR_CHAR))) { return null; }
				}
				return packet;
			}
		};
		Bases.getPlugin().getServer().getPluginManager().registerEvents(this, Bases.getPlugin());
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				while (Bases.getPlugin().isEnabled())
				{
					try
					{
						for (Map.Entry<Player, PlayerTab> tab : Lists.newArrayList(maps.entrySet()))
						{
							if (((CraftPlayer) tab.getKey()).getHandle().playerConnection.networkManager.getVersion() >= 47) { return; }
							for (int y = 0; y < 21; y++)
							{
								for (int x = 0; x < 3; x++)
								{
									String text = texts[y][x];
									asyncTabParaseEvent.setPlayer(tab.getKey());
									asyncTabParaseEvent.setInput(text);
									asyncTabParaseEvent.setPing(-1);
									Bukkit.getPluginManager().callEvent(asyncTabParaseEvent);
									tab.getValue().setText(y, x, asyncTabParaseEvent.getInput());
									if (asyncTabParaseEvent.getPing() != -1)
									{
										tab.getValue().setPing(y, x, asyncTabParaseEvent.getPing());
									}
								}
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					try
					{
						Thread.sleep(500L);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.runTaskAsynchronously(Bases.getPlugin());
	}

	private String[] colorMapping = new String[] {
			"&4",
			"&c",
			"&6",
			"&e",
			"&a",
			"&a",
			"&a"
	};
	public static DecimalFormat DTR_FORMATTER = new DecimalFormat("0.0");

	@EventHandler
	public void onAsyncTabParase(AsyncTabParaseEvent event)
	{
		if (!event.getPlayer().isOnline()) { return; }

		String result = event.getInput();
		Player player = event.getPlayer();
		ColorFaction colorFaction = plugin.getFactionManager().getColorFaction(player.getUniqueId());
		int ping = 1;

		ColorFaction green = plugin.getFactionManager().getColorFaction("Green");
		ColorFaction blue = plugin.getFactionManager().getColorFaction("Blue");
		ColorFaction yellow = plugin.getFactionManager().getColorFaction("Yellow");
		ColorFaction red = plugin.getFactionManager().getColorFaction("Red");

		ColorFaction lookingAt = null;
		double diffYaw = Double.MAX_VALUE;
		List<ColorFaction> set = Lists.newArrayList(green, blue, yellow, red);
		for (ColorFaction colored : set)
		{
			double yaw = getYawChange(player, colored.getHome());
			if (Math.abs(yaw) < diffYaw)
			{
				diffYaw = Math.abs(yaw);
				lookingAt = colored;
			}
			result = result.replace("{" + colored.getName().toLowerCase() + ".dtr}", ChatColor.translateAlternateColorCodes('&', colorMapping[Math.max(0, (int) colored.getDeathsUntilRaidable())]) + DTR_FORMATTER.format(colored.getDeathsUntilRaidable()));
			List<Player> online = Lists.newArrayList(colored.getOnlinePlayers());
			Collections.sort(online, new Comparator<Player>()
			{
				@Override
				public int compare(Player o1, Player o2)
				{
					return o1.getName().compareTo(o2.getName());
				}
			});
			Collections.sort(online, new Comparator<Player>()
			{
				@Override
				public int compare(Player o1, Player o2)
				{
					return colored.getMember(o1).getRole().ordinal() - colored.getMember(o2).getRole().ordinal();
				}
			});
			for (int i = 0; i < 5; i++)
			{
				if (i >= online.size())
				{
					result = result.replace("{" + colored.getName().toLowerCase() + ".online." + (i + 1) + "}", " ");
				}
				else
				{
					boolean isAlive = !DeathListener.spectators.containsKey(online.get(i).getUniqueId());
					if (result.contains("{" + colored.getName().toLowerCase() + ".online." + (i + 1) + "}"))
					{
						if (!isAlive)
						{
							ping = -999;
						}
					}
					result = result.replace("{" + colored.getName().toLowerCase() + ".online." + (i + 1) + "}", (isAlive ? "" : ChatColor.GRAY.toString()) + online.get(i).getName());
				}
			}
		}

		if (colorFaction == null)
		{
			if (result.contains("{dtr}"))
			{
				result = "";
			}
			if (result.contains("{online}"))
			{
				result = "";
			}
			if (result.contains("{base.location}"))
			{
				result = "";
			}
		}
		else
		{
			result = result.replace("{dtr}", ChatColor.translateAlternateColorCodes('&', colorMapping[Math.max(0, (int) colorFaction.getDeathsUntilRaidable())]) + DTR_FORMATTER.format(colorFaction.getDeathsUntilRaidable()));
			result = result.replace("{online}", String.valueOf(colorFaction.getOnlinePlayers().size()) + "/" + String.valueOf(colorFaction.getMembers().size()));
			Location home = colorFaction.getHome();
			if (home != null)
			{
				result = result.replace("{base.location}", home.getBlockX() + ", " + home.getBlockZ());

			}
			else
			{
				result = result.replace("{base.location}", "Not Set");
			}

		}
		Location location = player.getLocation();
		Faction current = plugin.getFactionManager().getFactionAt(player.getLocation());
		String suffix = "";
		if (current instanceof ColorFaction)
		{
			suffix += " Base";
		}
		String color = lookingAt.getColor().toString();
		result = result.replace("{location.name}", current == null ? "Wilderness" : current.getDisplayName(player) + suffix);
		result = result.replace("{location}", location.getBlockX() + ", " + location.getBlockZ() + color + " [" + getCardinalDirection(player) + "]");
		result = result.replace("{red.online}", String.valueOf(red.getOnlinePlayers().size()));
		result = result.replace("{yellow.online}", String.valueOf(yellow.getOnlinePlayers().size()));
		result = result.replace("{green.online}", String.valueOf(green.getOnlinePlayers().size()));
		result = result.replace("{blue.online}", String.valueOf(blue.getOnlinePlayers().size()));

		event.setInput(result);
		event.setPing(ping);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (((CraftPlayer) event.getPlayer()).getHandle().playerConnection.networkManager.getVersion() >= 47) { return; }
				PlayerTab tab = new PlayerTab(event.getPlayer());
				tab.prepareOrReset();
				maps.put(event.getPlayer(), tab);
				System.out.println("[Debug] Preparing tab for " + event.getPlayer().getName());
			}
		}.runTaskLater(Bases.getPlugin(), 30);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		maps.remove(event.getPlayer());
	}

	// https://github.com/sk89q/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/entity/Player.java
	public static String getCardinalDirection(Player player)
	{
		double rot = (player.getLocation().getYaw()) % 360; // let's use real
															// yaw now
		if (rot < 0)
		{
			rot += 360.0;
		}
		return getDirection(rot);
	}

	private static String getDirection(double rot)
	{
		if (0 <= rot && rot < 22.5)
		{
			return "S";
		}
		else if (22.5 <= rot && rot < 67.5)
		{
			return "SW";
		}
		else if (67.5 <= rot && rot < 112.5)
		{
			return "W";
		}
		else if (112.5 <= rot && rot < 157.5)
		{
			return "NW";
		}
		else if (157.5 <= rot && rot < 202.5)
		{
			return "N";
		}
		else if (202.5 <= rot && rot < 247.5)
		{
			return "NW";
		}
		else if (247.5 <= rot && rot < 292.5)
		{
			return "E";
		}
		else if (292.5 <= rot && rot < 337.5)
		{
			return "SE";
		}
		else if (337.5 <= rot && rot < 360.0)
		{
			return "S";
		}
		else
		{
			return null;
		}
	}

	// https://gitlab.com/Apteryx/XIV/blob/251d3fc357b373fe29084c827b734cac98031ee7/src/main/java/pw/latematt/xiv/utils/EntityUtils.java
	public static float getYawChange(Player me, Location entity)
	{
		final double deltaX = entity.getX() - me.getLocation().getX();
		final double deltaZ = entity.getZ() - me.getLocation().getZ();
		double yawToEntity;

		if ((deltaZ < 0.0D) && (deltaX < 0.0D))
		{
			yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		}
		else
		{
			if ((deltaZ < 0.0D) && (deltaX > 0.0D))
			{
				yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
			}
			else
			{
				yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
			}
		}

		return wrapAngleTo180_float(-(me.getLocation().getYaw() - (float) yawToEntity));
	}

	// Minecraft Source
	public static float wrapAngleTo180_float(float p_76142_0_)
	{
		p_76142_0_ %= 360.0F;

		if (p_76142_0_ >= 180.0F)
		{
			p_76142_0_ -= 360.0F;
		}

		if (p_76142_0_ < -180.0F)
		{
			p_76142_0_ += 360.0F;
		}

		return p_76142_0_;
	}

}
