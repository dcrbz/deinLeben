package bz.dcr.deinleben;

import bz.dcr.dccore.DcCorePlugin;
import bz.dcr.dccore.commons.db.MongoDB;
import bz.dcr.deinleben.command.AcceptProposalCommand;
import bz.dcr.deinleben.command.DenyProposalCommand;
import bz.dcr.deinleben.command.MarriageCommand;
import bz.dcr.deinleben.command.ProposalCommand;
import bz.dcr.deinleben.config.ConfigKey;
import bz.dcr.deinleben.lang.LangManager;
import bz.dcr.deinleben.marriage.MarriageManager;
import bz.dcr.deinleben.persistence.MongoPersistenceService;
import bz.dcr.deinleben.persistence.PersistenceService;
import com.mongodb.MongoClientURI;
import de.ketrwu.levitate.Levitate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DeinLebenPlugin extends JavaPlugin {

    private static DeinLebenPlugin instance;

    private DcCorePlugin dcCorePlugin;

    private PersistenceService persistenceService;
    private LangManager langManager;

    private MarriageManager marriageManager;


    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        setupPersistenceManager();
        setupLangManager();
        setupDcCore();

        marriageManager = new MarriageManager(this);

        setupCommands();
    }


    private void setupPersistenceManager() {
        // Build MongoDB uri and register codecs
        final MongoClientURI uri = new MongoClientURI(
                getConfig().getString(ConfigKey.MONGODB_URI)
        );

        final MongoDB db = new MongoDB(uri, getClassLoader());

        persistenceService = new MongoPersistenceService(db);
    }

    private void setupLangManager() {
        final File langFile = new File(getDataFolder(), "lang.yml");

        // Create file if not existing
        if (!langFile.exists()) {
            try {
                langFile.getParentFile().mkdirs();
                saveResource("lang.yml", false);
            } catch (Exception ex) {
                ex.printStackTrace();
                getLogger().warning("Could not create language file (lang.yml)!");
                getServer().getPluginManager().disablePlugin(this);
            }
        }

        // Load configuration from file
        final Configuration langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Initialize LangManager
        langManager = new LangManager(langConfig);
    }

    private void setupDcCore() {
        final Plugin dcCorePlugin = getServer().getPluginManager().getPlugin("dcCore");

        if (dcCorePlugin == null) {
            getLogger().warning("dcCore wurde nicht gefunden!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.dcCorePlugin = (DcCorePlugin) dcCorePlugin;
    }

    private void setupCommands() {
        Levitate levitate = new Levitate(this);
        levitate.registerCommands(new ProposalCommand(this));
        levitate.registerCommands(new AcceptProposalCommand(this));
        levitate.registerCommands(new DenyProposalCommand(this));
        levitate.registerCommands(new MarriageCommand(this));
    }


    public DcCorePlugin getDcCorePlugin() {
        return dcCorePlugin;
    }

    public PersistenceService getPersistence() {
        return persistenceService;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public MarriageManager getMarriageManager() {
        return marriageManager;
    }

    public static DeinLebenPlugin getInstance() {
        return instance;
    }

}
