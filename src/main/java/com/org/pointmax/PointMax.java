package com.org.pointmax;

import com.org.pointmax.commands.*;
import com.org.pointmax.database.DataBaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public final class PointMax extends JavaPlugin {
    private File pluginDirectory;

    @Override
    public void onEnable() {
        createPluginDirectory();

        Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand());
        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DeleteHomeCommand());

        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCommand());
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawnCommand());
    }

    @Override
    public void onDisable() {
        try {
            DataBaseManager.getInstance().getConnection().close();
            DataBaseManager.getInstance().getStatement().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        DataBaseManager.getInstance().close();
    }

    private void createPluginDirectory() {
        pluginDirectory = new File(getDataFolder(), "");

        if (!pluginDirectory.exists()) {
            if (pluginDirectory.mkdirs()) {
                getLogger().info("Директория плагина успешно создана: " + pluginDirectory.getAbsolutePath());
            } else {
                getLogger().warning("Не удалось создать директорию плагина: " + pluginDirectory.getAbsolutePath());
            }
        } else {
            getLogger().info("Директория плагина уже существует: " + pluginDirectory.getAbsolutePath());
        }
    }
}
