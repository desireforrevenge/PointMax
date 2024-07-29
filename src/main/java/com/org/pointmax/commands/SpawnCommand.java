package com.org.pointmax.commands;

import com.org.pointmax.database.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            try (PreparedStatement pstmt = DataBaseManager.getInstance().getConnection().prepareStatement("SELECT world_name, x, y, z, yaw, pitch FROM spawn_location")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String worldName = rs.getString("world_name");
                        double x = rs.getDouble("x");
                        double y = rs.getDouble("y");
                        double z = rs.getDouble("z");
                        float yaw = rs.getFloat("yaw");
                        float pitch = rs.getFloat("pitch");

                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            player.teleport(new Location(world, x, y, z, yaw, pitch));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
