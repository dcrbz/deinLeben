package bz.dcr.deinleben.persistence;

import bz.dcr.dccore.commons.db.MongoDB;
import bz.dcr.deinleben.model.Marriage;
import bz.dcr.deinleben.model.Proposal;
import org.mongodb.morphia.query.Query;

import java.util.Optional;
import java.util.UUID;

public class MongoPersistenceService implements PersistenceService {

    private MongoDB database;

    public MongoPersistenceService(MongoDB database) {
        this.database = database;
    }

    @Override
    public void close() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public void createProposal(Proposal proposal) {
        proposal.setId(null);
        saveProposal(proposal);
    }

    @Override
    public void saveProposal(Proposal proposal) {
        database.getDatastore().save(proposal);
    }

    @Override
    public void deleteProposal(Proposal proposal) {
        database.getDatastore().delete(proposal);
    }

    @Override
    public Optional<Proposal> getProposalByRecipient(UUID recipient) {
        return Optional.ofNullable(database.getDatastore()
                .createQuery(Proposal.class)
                .field("recipient").equal(recipient)
                .get());
    }

    @Override
    public Optional<Proposal> getProposalByApplicantOrRecipient(UUID applicantOrRecipient) {
        final Query<Proposal> query = database.getDatastore()
                .createQuery(Proposal.class);

        query.or(
                query.criteria("applicant").equal(applicantOrRecipient),
                query.criteria("recipient").equal(applicantOrRecipient)
        );

        return Optional.ofNullable(query.get());
    }

    @Override
    public void createMarriage(Marriage marriage) {
        marriage.setId(null);
        saveMarriage(marriage);
    }

    @Override
    public void saveMarriage(Marriage marriage) {
        database.getDatastore().save(marriage);
    }

    @Override
    public void deleteMarriage(Marriage marriage) {
        database.getDatastore().delete(marriage);
    }

    @Override
    public Optional<Marriage> getMarriageBySpouse(UUID spouse) {
        final Query<Marriage> query = database.getDatastore()
                .createQuery(Marriage.class);

        query.or(
                query.criteria("spouse1").equal(spouse),
                query.criteria("spouse2").equal(spouse)
        );

        return Optional.ofNullable(query.get());
    }

}
