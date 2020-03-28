package bz.dcr.deinleben.command;

import bz.dcr.dccore.DcCorePlugin;
import bz.dcr.deinleben.DeinLebenPlugin;
import bz.dcr.deinleben.config.Permission;
import bz.dcr.deinleben.lang.LangKey;
import bz.dcr.deinleben.model.Marriage;
import bz.dcr.deinleben.model.Proposal;
import de.ketrwu.levitate.ParameterSet;
import de.ketrwu.levitate.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProposalCommand {

    private DeinLebenPlugin plugin;

    public ProposalCommand(DeinLebenPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            syntax = "/antrag <player>",
            readable = "/antrag <Spieler>",
            description = "Mache ihm/ihr einen Heiratsantrag",
            permission = Permission.PROPOSAL_SEND
    )
    public void onCommand(CommandSender sender, String cmd, ParameterSet args) {
        final Player player = (Player) sender;
        final Player targetPlayer = args.getPlayer(0);

        // Target player is offline
        if (targetPlayer == null) {
            final Map<String, String> placeholder = new HashMap<>();
            placeholder.put("player", args.getString(0));

            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_TARGET_OFFLINE, true, placeholder));
            return;
        }

        // Make proposal
        plugin.getMarriageManager().makeProposal(player, targetPlayer);
    }

}
