package com.org.pointmax.commands;

import com.org.pointmax.database.DataBaseManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Location location = player.getLocation();
            UUID uuid = player.getUniqueId();

            if (strings.length == 0) strings = new String[]{"home"};

            if (homeExists(uuid, strings[0])) {
                player.sendMessage("такой дом уже существует. Вы уверены? Введите команду еще раз, если да.");


            } else {
                try (PreparedStatement preparedStatement = DataBaseManager.getInstance().getConnection().prepareStatement("INSERT INTO home_locations (uuid, home_name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, strings[0]);
                    preparedStatement.setString(3, Objects.requireNonNull(location.getWorld()).getName());
                    preparedStatement.setDouble(4, location.getX());
                    preparedStatement.setDouble(5, location.getY());
                    preparedStatement.setDouble(6, location.getZ());
                    preparedStatement.setFloat(7, location.getYaw());
                    preparedStatement.setFloat(8, location.getPitch());

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else commandSender.sendMessage("Команда доступна только для игроков.");
        return true;
    }

    private boolean homeExists(UUID uuid, String homeName) {
        try (PreparedStatement preparedStatement = DataBaseManager.getInstance().getConnection().prepareStatement("SELECT COUNT(*) FROM home_locations WHERE uuid = ? AND home_name = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, homeName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if home exists", e);
        }
        return false;
    }
}


