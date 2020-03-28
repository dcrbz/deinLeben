package bz.dcr.deinleben.command;

import bz.dcr.deinleben.DeinLebenPlugin;
import bz.dcr.deinleben.config.Permission;
import bz.dcr.deinleben.lang.LangKey;
import bz.dcr.deinleben.model.Proposal;
import de.ketrwu.levitate.ParameterSet;
import de.ketrwu.levitate.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AcceptProposalCommand {

    private DeinLebenPlugin plugin;

    public AcceptProposalCommand(DeinLebenPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            syntax = "/jasagen <player>",
            readable = "/jasagen <Spieler>",
            description = "Sage Ja zu ihm/ihr",
            permission = Permission.PROPOSAL_ACCEPT
    )
    public void onCommand(CommandSender sender, String cmd, ParameterSet args) {
        final Player player = (Player) sender;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Get proposal
            final Optional<Proposal> proposal = plugin.getPersistence().getProposalByRecipient(player.getUniqueId());

            // No proposal found
            if (!proposal.isPresent()) {
                player.sendMessage(plugin.getLangManager().getMessage(LangKey.NO_PROPOSAL_RECEIVED, true));
                return;
            }

            // Deny proposal
            plugin.getMarriageManager().acceptProposal(player, proposal.get());
        });
    }

}
