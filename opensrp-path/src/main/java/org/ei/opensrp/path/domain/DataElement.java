package org.ei.opensrp.path.domain;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by coder on 6/6/17.
 */
public class DataElement {
    @JsonProperty
    private String name;

    @JsonProperty
    private String dhis2Id;

    @JsonProperty
    private String value;

    public DataElement() {

    }

    public DataElement(String name, String dhis2Id, String value) {
        this.name = name;
        this.dhis2Id = dhis2Id;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDhis2Id(String dhis2Id) {
        this.dhis2Id = dhis2Id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getDhis2Id() {
        return this.dhis2Id;
    }

    public String getValue() {
        return this.value;
    }
}