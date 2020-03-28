package bz.dcr.deinleben.command;

import bz.dcr.deinleben.DeinLebenPlugin;
import bz.dcr.deinleben.config.Permission;
import bz.dcr.deinleben.model.Marriage;
import bz.dcr.deinleben.model.Proposal;
import de.ketrwu.levitate.ParameterSet;
import de.ketrwu.levitate.annotation.Command;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class MarriageCommand {

    private DeinLebenPlugin plugin;

    public MarriageCommand(DeinLebenPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            syntax = "/ehe",
            readable = "/ehe",
            description = "Sieh den Status deiner Ehe ein",
            permission = Permission.MARRIAGE_INFO
    )
    public void onMarriageCommand(CommandSender sender, String cmd, ParameterSet args) {
        final Player player = (Player) sender;
        printMarriageStatus(player, player);
    }

    @Command(
            syntax = "/ehe <player>",
            readable = "/ehe <Spieler>",
            description = "Sieh den Status der Ehe eines Spielers ein",
            permission = Permission.MARRIAGE_INFO_OTHERS
    )
    public void onMarriageOthersCommand(CommandSender sender, String cmd, ParameterSet args) {
        printMarriageStatus(args.getOfflinePlayer(0), sender);
    }

    private void printMarriageStatus(OfflinePlayer player, CommandSender requester) {
        // Get marriage
        final Optional<Marriage> marriage = plugin.getPersistence().getMarriageBySpouse(player.getUniqueId());

        if (marriage.isPresent()) {
            final String spouseName = plugin.getDcCorePlugin().getIdentificationProvider().getName(
                    marriage.get().getOtherSpouse(player.getUniqueId()));
            final String weddingDayFormatted = plugin.getLangManager().formatDate(marriage.get().getWeddingDay());

            requester.sendMessage(ChatColor.GOLD + "Beziehungsstatus: " + ChatColor.GRAY + (marriage.isPresent() ? "Verheiratet" : "Single"));
            requester.sendMessage(ChatColor.GOLD + "Ehepartner: " + ChatColor.GRAY + spouseName);
            requester.sendMessage(ChatColor.GOLD + "Verheiratet seit: " + ChatColor.GRAY + weddingDayFormatted);
        } else {
            // Get proposal
            final Optional<Proposal> proposal = plugin.getPersistence()
                    .getProposalByApplicantOrRecipient(player.getUniqueId());

            if (proposal.isPresent()) {
                final String applicantName = plugin.getDcCorePlugin().getIdentificationProvider()
                        .getName(proposal.get().getApplicant());
                final String recipientName = plugin.getDcCorePlugin().getIdentificationProvider()
                        .getName(proposal.get().getRecipient());
                final String proposalDateFormatted = plugin.getLangManager()
                        .formatDate(proposal.get().getProposalDate());

                requester.sendMessage(ChatColor.GOLD + "Beziehungsstatus: " + ChatColor.GRAY + "Heiratsantrag offen");
                requester.sendMessage(ChatColor.GOLD + "Antragsteller: " + ChatColor.GRAY + applicantName);
                requester.sendMessage(ChatColor.GOLD + "Antragsempfänger: " + ChatColor.GRAY + recipientName);
                requester.sendMessage(ChatColor.GOLD + "Antragsdatum: " + ChatColor.GRAY + proposalDateFormatted);
            } else {
                requester.sendMessage(ChatColor.GOLD + "Beziehungsstatus: " + ChatColor.GRAY + "Single");
            }
        }
    }

}
