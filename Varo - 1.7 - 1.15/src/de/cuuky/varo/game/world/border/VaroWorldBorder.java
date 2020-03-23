package de.cuuky.varo.game.world.border;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.cuuky.varo.Main;
import de.cuuky.varo.version.BukkitVersion;
import de.cuuky.varo.version.VersionUtils;

public class VaroWorldBorder {

	private static Method setCenterMethod, getCenterMethod, setSizeMethod, getSizeMethod;

	static {
		if(VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7)) {

			try {
				Class<?> borderClass = Class.forName("org.bukkit.WorldBorder");
				
				setCenterMethod = borderClass.getDeclaredMethod("setCenter", Location.class);
				getCenterMethod = borderClass.getDeclaredMethod("getCenter");
				setSizeMethod = borderClass.getDeclaredMethod("setSize", double.class, long.class);
				getSizeMethod = borderClass.getDeclaredMethod("getSize");
			} catch(ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private World world;
	private Object border;
	private HashMap<Player, Double> distances;

	public VaroWorldBorder(World world) {
		if(!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;

		this.world = world;
		this.distances = new HashMap<>();

		loadBorder();
		startCalculating();
	}

	private void loadBorder() {
		try {
			border = world.getClass().getDeclaredMethod("getWorldBorder").invoke(world);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private double getDistanceToBorder(Player player) {
		Location playerLoc = player.getLocation();

		Location center;
		double size = 0;

		try {
			center = getCenter();
			size = getBorderSize() / 2;
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}

		ArrayList<Double> distanceArray = new ArrayList<>();
		double playerDifferenceX = playerLoc.getX() - center.getX();
		double playerDifferenceZ = playerLoc.getZ() - center.getZ();
		distanceArray.add(Math.abs(playerDifferenceX + size));
		distanceArray.add(Math.abs(playerDifferenceX - size));
		distanceArray.add(Math.abs(playerDifferenceZ + size));
		distanceArray.add(Math.abs(playerDifferenceZ - size));
		double nearest = Double.MAX_VALUE;
		for(double distance : distanceArray)
			if(distance < nearest)
				nearest = distance;

		return Math.round(nearest);
	}

	private void startCalculating() {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				for(Player player : VersionUtils.getOnlinePlayer())
					distances.put(player, getDistanceToBorder(player));
			}
		}, 20, 20 * 1);
	}
	
	public Location getCenter() {
		try {
			return (Location) getCenterMethod.invoke(border);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public double getBorderDistanceTo(Player p) {
		if(p == null || distances == null || !distances.containsKey(p))
			return 0;

		return distances.get(p);
	}

	public double getBorderSize() {
		try {
			return (double) getSizeMethod.invoke(border);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void setBorderCenter(Location loc) {
		try {
			setCenterMethod.invoke(border, loc);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setBorderSize(double size, long time) {
		try {
			setSizeMethod.invoke(border, size, time);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}