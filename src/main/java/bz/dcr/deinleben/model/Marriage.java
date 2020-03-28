package bz.dcr.deinleben.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Indexes({
        @Index(fields = {@Field("spouse1")}),
        @Index(fields = {@Field("spouse2")}),
        @Index(fields = {@Field("spouse1"), @Field("spouse2")}, options = @IndexOptions(unique = true))
})
public class Marriage {

    @Id
    private ObjectId id;

    private UUID spouse1;
    private UUID spouse2;
    private Date weddingDay;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getSpouse1() {
        return spouse1;
    }

    public void setSpouse1(UUID spouse1) {
        this.spouse1 = spouse1;
    }

    public UUID getSpouse2() {
        return spouse2;
    }

    public void setSpouse2(UUID spouse2) {
        this.spouse2 = spouse2;
    }

    public Date getWeddingDay() {
        return weddingDay;
    }

    public void setWeddingDay(Date weddingDay) {
        this.weddingDay = weddingDay;
    }

    public boolean hasSpouse(UUID uuid) {
        return spouse1.equals(uuid) || spouse2.equals(uuid);
    }

    public UUID getOtherSpouse(UUID spouse) {
        if (spouse1.equals(spouse)) {
            return spouse2;
        } else {
            return spouse1;
        }
    }

}
