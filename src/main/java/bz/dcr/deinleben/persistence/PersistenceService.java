package bz.dcr.deinleben.persistence;

import bz.dcr.deinleben.model.Marriage;
import bz.dcr.deinleben.model.Proposal;

import java.util.Optional;
import java.util.UUID;

public interface PersistenceService {

    void close();

    void createProposal(Proposal proposal);
    void saveProposal(Proposal proposal);
    void deleteProposal(Proposal proposal);
    Optional<Proposal> getProposalByRecipient(UUID recipient);
    Optional<Proposal> getProposalByApplicantOrRecipient(UUID applicantOrRecipient);

    void createMarriage(Marriage marriage);
    void saveMarriage(Marriage marriage);
    void deleteMarriage(Marriage marriage);
    Optional<Marriage> getMarriageBySpouse(UUID spouse);

}
