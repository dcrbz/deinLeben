package bz.dcr.deinleben.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Indexes({
        @Index(fields = {@Field("applicant")}, options = @IndexOptions(unique = true)),
        @Index(fields = {@Field("recipient")}, options = @IndexOptions(unique = true)),
        @Index(fields = {@Field("applicant"), @Field("recipient")}, options = @IndexOptions(unique = true))
})
public class Proposal {

    private static final UUID PADDY_UUID = UUID.fromString("12e191c1-3c01-4261-b31e-eee996235490");
    private static final UUID KALY_UUID = UUID.fromString("5b66cafc-ec39-4630-8cc3-c94c02f77dc6");

    @Id
    private ObjectId id;

    private UUID applicant;
    private UUID recipient;
    private Date proposalDate;
    private String message;

    public Proposal() {
        proposalDate = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getApplicant() {
        return applicant;
    }

    public void setApplicant(UUID applicant) {
        this.applicant = applicant;
    }

    public UUID getRecipient() {
        return recipient;
    }

    public void setRecipient(UUID recipient) {
        this.recipient = recipient;
    }

    public Date getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Date proposalDate) {
        this.proposalDate = proposalDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPaddyAndKaly() {
        return (applicant.equals(PADDY_UUID) && recipient.equals(KALY_UUID)) ||
                (recipient.equals(PADDY_UUID) && applicant.equals(KALY_UUID));
    }

}
