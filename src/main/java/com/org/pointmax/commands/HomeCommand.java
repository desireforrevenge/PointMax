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
import java.util.HashMap;
import java.util.UUID;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 0 && homesCounter(player.getUniqueId()) > 1) {
                StringBuilder message = new StringBuilder();

                message.append("Список точек дома: ");
                for (String homeName : getHomesList(player).keySet()) {
                    message.append(homeName);
                    message.append(" ");
                }
                player.sendMessage(message.toString());
            } else if (strings.length == 0 && homesCounter(player.getUniqueId()) == 1) {
                for (String homeName : getHomesList(player).keySet()) {
                    player.teleport(getHomesList(player).get(homeName));
                }
            } else if (strings.length == 0 && homesCounter(player.getUniqueId()) == 0) {
                //todo: tp na spawn
                player.sendMessage("У вас нет сохраненных точек дома.");
            } else if (strings.length > 0) {
                boolean homeFound = false;
                for (String homeName : getHomesList(player).keySet()) {
                    if (strings[0].equalsIgnoreCase(homeName)) {
                        player.teleport(getHomesList(player).get(homeName));
                        homeFound = true;
                        break;
                    }
                }
                if (!homeFound) {
                    player.sendMessage("Дом с именем " + strings[0] + " не найден.");
                }
            }

        } else {
            commandSender.sendMessage("Команда доступна только для игроков.");
        }
        return true;
    }


    private HashMap<String, Location> getHomesList(Player player) {
        HashMap<String, Location> locations = new HashMap<>();

        try (PreparedStatement pstmt = DataBaseManager.getInstance().getConnection().prepareStatement("SELECT home_name, world_name, x, y, z, yaw, pitch FROM home_locations WHERE uuid = ?")) {
            pstmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String homeName = rs.getString("home_name");
                    String worldName = rs.getString("world_name");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    float yaw = rs.getFloat("yaw");
                    float pitch = rs.getFloat("pitch");

                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        locations.put(homeName, new Location(world, x, y, z, yaw, pitch));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return locations;
    }

    private int homesCounter(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM home_locations WHERE uuid = ?";
        try (PreparedStatement preparedStatement = DataBaseManager.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if home exists", e);
        }
        return 0;
    }
}
