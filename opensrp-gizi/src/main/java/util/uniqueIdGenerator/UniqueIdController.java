package util.uniqueIdGenerator;

import android.support.annotation.Nullable;

import org.ei.opensrp.util.Cache;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
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
        List<String> uidstring = getAllUniqueIdString();
        return processLatestUniqueId(uids,uidstring);
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
        Long ids = Long.parseLong(allSettings.fetchCurrentId());
        id = id.length()>8? id.substring(0,8) : id;
        if((ids > 20000000) || (ids < Long.parseLong(id))) {
            allSettings.saveCurrentId(id);
        }
    }

    public List<Long> getAllUniqueId() {
        return uniqueIdRepository.getAllUniqueId();
    }

    public List<String> getAllUniqueIdString() {
        return uniqueIdRepository.getAllUniqueIdString();
    }

    public void saveUniqueId(String uniqueId) {
        uniqueIdRepository.saveUniqueId(uniqueId);
    }

    public boolean needToRefillUniqueId() {
        List<Long> uids = getAllUniqueId();
        int currentId = Integer.parseInt(allSettings.fetchCurrentId());
        return uids==null || uids.isEmpty() || uids.get(uids.size()-15) < currentId;
    }

    public boolean needToRefillUniqueId(int limit) {
        List<Long> uids = getAllUniqueId();
        int currentId = Integer.parseInt(allSettings.fetchCurrentId());
        return uids==null || uids.isEmpty() || (uids.get(uids.size()-(limit+1) < 0 ? 0 : uids.size()-(limit+1)) <= currentId);
    }

    public int countRemainingUniqueId(){
        List<Long> uids = getAllUniqueId();
        int currentId = Integer.parseInt(allSettings.fetchCurrentId())>200000000
                ? 10000000 : Integer.parseInt(allSettings.fetchCurrentId());
        return (uids.size()-1)- Collections.binarySearch(uids,new Long(currentId));
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
        Long currentId = Long.parseLong(allSettings.fetchCurrentId()) > 20000000 ? 10000000 : Long.parseLong(allSettings.fetchCurrentId());
        if(uids == null || uids.isEmpty() || currentId > uids.get(uids.size()-1)) {
            return null;
        }
        int index = Arrays.binarySearch(uids.toArray(), currentId);
        if(index<-1) index = -1;
        return index >= uids.size()-1 ? null : "" + String.valueOf(uids.get(index+1));
    }

    @Nullable
    private String processLatestUniqueId(List<Long> uids,List<String>uidstring) {
        Long currentId = Long.parseLong(allSettings.fetchCurrentId()) > 20000000 ? 10000000 : Long.parseLong(allSettings.fetchCurrentId());
        if(uids == null || uids.isEmpty() || currentId > uids.get(uids.size() - 1))
                return null;
        int index = Arrays.binarySearch(uids.toArray(), currentId);
        if(index<-1) index = -1;
        return index >= uidstring.size()-1 ? null : "" + String.valueOf(uidstring.get(index+1));
    }

}
