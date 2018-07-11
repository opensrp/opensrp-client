package org.ei.opensrp.path.domain;

/**
 * Created by keyman on 1/23/18.
 */

public class NamedObject<T> {
    public final String name;
    public final T object;

    public NamedObject(String name, T object) {
        this.name = name;
        this.object = object;
    }
}