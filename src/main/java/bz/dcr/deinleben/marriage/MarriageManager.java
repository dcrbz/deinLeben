package bz.dcr.deinleben.marriage;

import bz.dcr.deinleben.DeinLebenPlugin;
import bz.dcr.deinleben.lang.LangKey;
import bz.dcr.deinleben.model.Marriage;
import bz.dcr.deinleben.model.Proposal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class MarriageManager {

    private DeinLebenPlugin plugin;

    public MarriageManager(DeinLebenPlugin plugin) {
        this.plugin = plugin;
    }

    public void makeProposal(Player player, Player targetPlayer) {
        // Get existing marriage of player
        final Optional<Marriage> existingMarriageSelf = plugin.getPersistence()
                .getMarriageBySpouse(player.getUniqueId());

        // Applicant is already married
        if (existingMarriageSelf.isPresent()) {
            final UUID otherSpouse = existingMarriageSelf.get().getOtherSpouse(player.getUniqueId());

            final Map<String, String> placeholder = new HashMap<>();
            placeholder.put("player", plugin.getDcCorePlugin().getIdentificationProvider().getName(otherSpouse));

            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_APPLICANT_MARRIED, true, placeholder));
            return;
        }

        // Get existing marriage of target player
        final Optional<Marriage> existingMarriageTarget = plugin.getPersistence()
                .getMarriageBySpouse(targetPlayer.getUniqueId());

        // Target player is already married
        if (existingMarriageTarget.isPresent()) {
            final Map<String, String> placeholder = new HashMap<>();
            placeholder.put("player", targetPlayer.getName());

            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_TARGET_MARRIED, true, placeholder));
            return;
        }

        // Get existing proposal of player
        final Optional<Proposal> existingProposalSelf = plugin.getPersistence()
                .getProposalByApplicantOrRecipient(player.getUniqueId());

        // Player already has a pending proposal
        if (existingProposalSelf.isPresent()) {
            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_APPLICANT_HAS_PROPOSAL, true));
            return;
        }

        // Get existing proposal of target player
        final Optional<Proposal> existingProposalTarget = plugin.getPersistence()
                .getProposalByApplicantOrRecipient(targetPlayer.getUniqueId());

        // Target already has a pending proposal
        if (existingProposalTarget.isPresent()) {
            final Map<String, String> placeholder = new HashMap<>();
            placeholder.put("player", targetPlayer.getName());

            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_TARGET_HAS_PROPOSAL, true, placeholder));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Create proposal
            Proposal proposal = new Proposal();
            proposal.setApplicant(player.getUniqueId());
            proposal.setRecipient(targetPlayer.getUniqueId());
            proposal.setProposalDate(new Date());

            // Persist proposal
            plugin.getPersistence().createProposal(proposal);

            // Send proposal message to applicant
            final Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetPlayer.getName());
            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_SENT, true, placeholders));

            // Send proposal message to target
            placeholders.put("player", player.getName());
            targetPlayer.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_RECEIVED, true, placeholders));

            // Play proposal sound
            Bukkit.getScheduler().runTask(plugin, () -> {
                playProposalSound(player);
                playProposalSound(targetPlayer);
            });
        });
    }

    public void acceptProposal(Player player, Proposal proposal) {
        // Create marriage
        final Marriage marriage = new Marriage();
        marriage.setSpouse1(proposal.getApplicant());
        marriage.setSpouse2(proposal.getRecipient());
        marriage.setWeddingDay(new Date());
        plugin.getPersistence().createMarriage(marriage);

        // Delete proposal
        plugin.getPersistence().deleteProposal(proposal);

        // Get target player
        final Player applicant = Bukkit.getPlayer(proposal.getApplicant());

        // Target player is not online
        if (applicant == null) {
            final Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", plugin.getDcCorePlugin().getIdentificationProvider().getName(proposal.getApplicant()));
            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_TARGET_OFFLINE, true));
            return;
        }

        // Send message to recipient
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", applicant.getName());
        player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_ACCEPTED_RECIPIENT, true, placeholders));

        // Send message to applicant
        placeholders.put("player", player.getName());
        applicant.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_ACCEPTED_APPLICANT, true, placeholders));
    }

    public void denyProposal(Player player, Proposal proposal) {
        // Delete proposal
        plugin.getPersistence().deleteProposal(proposal);

        // Get target player
        final Player applicant = Bukkit.getPlayer(proposal.getApplicant());

        // Target player is not online
        if (applicant == null) {
            final Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", plugin.getDcCorePlugin().getIdentificationProvider().getName(proposal.getApplicant()));
            player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_TARGET_OFFLINE, true));
            return;
        }

        // Send message to recipient
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", applicant.getName());
        player.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_DENIED_RECIPIENT, true, placeholders));

        // Send message to applicant
        placeholders.put("player", player.getName());
        applicant.sendMessage(plugin.getLangManager().getMessage(LangKey.PROPOSAL_DENIED_APPLICANT, true, placeholders));
    }

    private void playProposalSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1.6f);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1.6f);
        }, 15);
    }

}
