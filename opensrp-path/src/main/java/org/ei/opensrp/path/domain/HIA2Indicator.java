package org.ei.opensrp.path.domain;

import java.io.Serializable;

/**
 * Holds data for a single HIA2 indicator
 */
public class HIA2Indicator implements Serializable {
    public final String category;
    public final String id;
    public final String name;
    public final double value;

    public HIA2Indicator(String category, String id, String name, double value) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
