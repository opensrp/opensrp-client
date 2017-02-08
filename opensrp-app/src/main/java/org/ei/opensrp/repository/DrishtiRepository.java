package org.ei.opensrp.repository;

import net.sqlcipher.database.SQLiteDatabase;

public abstract class DrishtiRepository {
    public Repository getMasterRepository() {
        return masterRepository;
    }

    protected Repository masterRepository;

    public void updateMasterRepository(Repository repository) {
        this.masterRepository = repository;
    }

    abstract protected void onCreate(SQLiteDatabase database);
}
