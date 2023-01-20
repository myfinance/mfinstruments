package de.hf.myfinance.instruments.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "IsInactivatableMap")
public class InActivationInfoEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private String businesskey;
    private boolean isInactivateable;


    public InActivationInfoEntity(){};
    public InActivationInfoEntity(String businesskey, boolean isInactivateable){
        this.businesskey = businesskey;
        this.isInactivateable = isInactivateable;
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBusinesskey() {
        return businesskey;
    }

    public void setBusinesskey(String businesskey) {
        this.businesskey = businesskey;
    }

    public boolean isInactivateable() {
        return isInactivateable;
    }

    public void setInactivateable(boolean inactivateable) {
        isInactivateable = inactivateable;
    }
}
