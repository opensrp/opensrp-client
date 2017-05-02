package org.ei.opensrp.path.repository;

import java.util.UUID;

/**
 * Created by keyman on 09/03/2017.
 */
public class BaseRepository {
    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";

    private PathRepository pathRepository;

    public BaseRepository(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    public PathRepository getPathRepository() {
        return pathRepository;
    }

    protected String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }
}
