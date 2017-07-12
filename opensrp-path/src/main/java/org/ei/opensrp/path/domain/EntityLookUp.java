package org.ei.opensrp.path.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by keyman on 11/04/2017.
 */
public class EntityLookUp {

    private Map<String, String> map = new HashMap<>();

    public void clear() {
        map.clear();
    }

    public void put(String key, String value) {
        if (StringUtils.isBlank(value) && map.containsKey(key)) {
            remove(key);
        } else {
            map.put(key, value);
        }
    }

    public void remove(String key) {
        map.remove(key);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
