package org.ei.opensrp.path.repository;

import net.sqlcipher.database.SQLiteDatabase;

public class HIA2Repository extends BaseRepository {
    private static final String TAG = HIA2Repository.class.getCanonicalName();
    private static final String HIA2_SQL = "CREATE TABLE hia2 (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,provider_id VARCHAR NOT NULL,key VARCHAR NOT NULL,dhis_id VARCHAR NOT NULL, value VARCHAR NOT NULL,month VARCHAR NOT NULL,status VARCHAR,created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";
    public static final String HIA2_TABLE_NAME = "hia2";
    public static final String ID_COLUMN = "_id";
    public static final String PROVIDER_ID = "provider_id";
    public static final String KEY = "key";
    public static final String VALUE = "value";// ID to be used to identify entity when base_entity_id is unavailable
    public static final String DHIS_ID = "dhis_id";
    public static final String MONTH = "month";

    public static final String STATUS = "status";
    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String[] WEIGHT_TABLE_COLUMNS = {ID_COLUMN, PROVIDER_ID, KEY, VALUE, DHIS_ID, STATUS,MONTH, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};

    private static final String PROVIDER_ID_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + PROVIDER_ID + "_index ON " + HIA2_TABLE_NAME + "(" + PROVIDER_ID + " COLLATE NOCASE);";
    private static final String KEY_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + KEY + "_index ON " + HIA2_TABLE_NAME + "(" + KEY + " COLLATE NOCASE);";
    private static final String VALUE_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + HIA2_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";
    private static final String DHIS_ID_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + DHIS_ID + "_index ON " + HIA2_TABLE_NAME + "(" + DHIS_ID + ");";
    private static final String MONTH_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + MONTH + "_index ON " + HIA2_TABLE_NAME + "(" + MONTH + ");";




    public HIA2Repository(PathRepository pathRepository) {
        super(pathRepository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(HIA2_SQL);
        database.execSQL(PROVIDER_ID_INDEX);
        database.execSQL(KEY_INDEX);
        database.execSQL(VALUE_INDEX);
        database.execSQL(DHIS_ID_INDEX);
        database.execSQL(MONTH_INDEX);
    }


}
