package com.org.pointmax.commands;

import com.org.pointmax.database.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DeleteHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1 && commandSender instanceof Player) {
            if (homeExists(((Player) commandSender).getUniqueId(), strings[0])) {
                deleteHome(((Player) commandSender).getUniqueId(), strings[0]);
            } else commandSender.sendMessage("Эта точка дома не существует.");
        } else if (strings.length == 2) {
            if (homeExists((Bukkit.getOfflinePlayer(strings[0])).getUniqueId(), strings[1])) {
                deleteHome((Bukkit.getOfflinePlayer(strings[0])).getUniqueId(), strings[1]);
            } else commandSender.sendMessage("Эта точка дома не существует.");
        }
        return false;
    }

    private void deleteHome(UUID uuid, String homeName) {
        try (PreparedStatement pstmt = DataBaseManager.getInstance().getConnection().prepareStatement("DELETE FROM home_locations WHERE uuid = ? AND home_name = ?")) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, homeName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
