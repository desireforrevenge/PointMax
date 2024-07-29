package com.org.pointmax.commands;

import com.org.pointmax.database.DataBaseManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Location location = ((Player) commandSender).getLocation();
            try (PreparedStatement preparedStatement = DataBaseManager.getInstance().getConnection().prepareStatement("INSERT INTO spawn_location (world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement preparedStatement2 = DataBaseManager.getInstance().getConnection().prepareStatement("DELETE FROM spawn_location")) {
                preparedStatement.setString(1, Objects.requireNonNull(location.getWorld()).getName());
                preparedStatement.setDouble(2, location.getX());
                preparedStatement.setDouble(3, location.getY());
                preparedStatement.setDouble(4, location.getZ());
                preparedStatement.setFloat(5, location.getYaw());
                preparedStatement.setFloat(6, location.getPitch());

                preparedStatement2.executeUpdate();
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
