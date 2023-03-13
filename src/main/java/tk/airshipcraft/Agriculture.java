package tk.airshipcraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tk.airshipcraft.listeners.SaplingListener;
import tk.airshipcraft.utils.TreeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public final class Agriculture extends JavaPlugin {

    public static FileConfiguration config;
    public static final HashMap<String, HashMap<Material, HashSet<TreeUtils>>> trees = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerListeners();

        File folder = getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                getLogger().severe("Unable to create data folder.");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        File schematicsFolder = new File(folder, "schematics");
        if (!schematicsFolder.exists()) {
            getLogger().severe("Unable to find schematics folder.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        File configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    getLogger().severe("Unable to create config.yml");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            } catch (Exception e) {
                getLogger().severe("Unable to create config.yml");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);

        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Failed to read from config.yml");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadTrees();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Load trees from data
     */
    private void loadTrees() {
        File biomeFolder = new File(getDataFolder(), "biomes");
        if (!biomeFolder.exists()) {
            getLogger().severe("Unable to find biomes folder");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        for (File file : biomeFolder.listFiles()) {
            FileConfiguration config = new YamlConfiguration();
            HashMap<Material, HashSet<TreeUtils>> treeMap = new HashMap<>();
            try {
                config.load(file);
                for (String materialName : config.getConfigurationSection("materials").getKeys(false)) {
                    ConfigurationSection section  = config.getConfigurationSection("materials." + materialName);

                    Material material = Material.valueOf(materialName);
                    HashSet<TreeUtils> matSet = new HashSet<>();

                    for (String treeName : section.getKeys(false)) {
                        TreeUtils customTree = new TreeUtils();
                        customTree.setMaterial(material);
                        customTree.setName(treeName);
                        customTree.setXOffset(section.getInt(treeName + ".x-offset", 0));
                        customTree.setYOffset(section.getInt(treeName + ".y-offset", 0));
                        customTree.setZOffset(section.getInt(treeName + ".z-offset", 0));
                        customTree.setWeight(section.getInt(treeName + ".weight", 100));
                        matSet.add(customTree);
                    }
                    treeMap.put(material, matSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Unable to read " + file.getName());
            }
            for (String biomeName : config.getStringList("biomes")) {
                trees.put(biomeName.toUpperCase(), treeMap);
            }
        }
    }

    /**
     * Register listeners
     */
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SaplingListener(this), this);
    }
}
