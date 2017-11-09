package org.ei.opensrp.view.controller;

import android.support.annotation.Nullable;

import org.ei.opensrp.repository.AllSettingsINA;
import org.ei.opensrp.repository.UniqueIdRepository;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 + * Created by Dimas on 9/8/2015.
 + */
public class UniqueIdController {

    private AllSettingsINA allSettings;
    private UniqueIdRepository uniqueIdRepository;
    private Cache<List<Long>> cache;

    public UniqueIdController(UniqueIdRepository uniqueIdRepository, AllSettingsINA allSettings, Cache<List<Long>> cache) {
        this.uniqueIdRepository = uniqueIdRepository;
        this.allSettings = allSettings;
        this.cache = cache;
    }

    public String getUniqueId() {
        List<Long> uids = getAllUniqueId();
        return processLatestUniqueId(uids);
    }

    public String getUniqueIdJson() {
        String uniqueId = getUniqueId();
        if(uniqueId == null || uniqueId.isEmpty()) {
            return null;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("unique_id", uniqueId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public void updateCurrentUniqueId(String id) {
        if(Long.parseLong(allSettings.fetchCurrentId()) < Long.parseLong(id)) {
            allSettings.saveCurrentId(id);
        }
    }

    public List<Long> getAllUniqueId() {
        return cache.get("UNIQUE_ID_LIST", new CacheableData<List<Long>>() {
            @Override
            public List<Long> fetch() {
                    return uniqueIdRepository.getAllUniqueId();
                }
        });
    }

    public void saveUniqueId(String uniqueId) {
        uniqueIdRepository.saveUniqueId(uniqueId);
    }

    public boolean needToRefillUniqueId() {
        List<Long> uids = getAllUniqueId();
        int currentId = Integer.parseInt(allSettings.fetchCurrentId());
        return uids==null || uids.isEmpty() || uids.get(uids.size()-15) < currentId;
    }

    // Class for testing
    public boolean needToRefillUniqueIdTest() {
        List<Long> uids = uniqueIdRepository.getAllUniqueId();
        long currentId = Long.parseLong(allSettings.fetchCurrentId());
        return (uids.get(uids.size()-1)/10) - currentId <= uids.size()/4;
    }

    public String getUniqueIdTest() {
        List<Long> uids = uniqueIdRepository.getAllUniqueId();
        return processLatestUniqueId(uids);
    }

    @Nullable
    private String processLatestUniqueId(List<Long> uids) {
        Long currentId = Long.parseLong(allSettings.fetchCurrentId());
        if(uids == null || uids.isEmpty() || currentId > uids.get(uids.size()-1)) {
            return null;
        }
        int index = Arrays.binarySearch(uids.toArray(), currentId);
        if(index<-1) index = -1;
        return index >= uids.size()-1 ? null : "" + String.valueOf(uids.get(index+1));
    }

}