package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.Address;
import org.ei.opensrp.path.db.Client;
import org.ei.opensrp.path.db.Column;
import org.ei.opensrp.path.db.ColumnAttribute;
import org.ei.opensrp.path.db.Event;
import org.ei.opensrp.path.domain.Vaccine_names;
import org.ei.opensrp.repository.AlertRepository;
import org.ei.opensrp.repository.Repository;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import util.DatabaseUtils;
import util.JsonFormUtils;
import util.MoveToMyCatchmentUtils;
import util.PathConstants;
import util.Utils;

public class PathRepository extends Repository {

    private static final String TAG = PathRepository.class.getCanonicalName();
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context context;

    public PathRepository(Context context) {
        super(context, PathConstants.DATABASE_NAME, PathConstants.DATABASE_VERSION, org.ei.opensrp.Context.getInstance().session(), VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        createTable(database, Table.client, client_column.values());
        createTable(database, Table.address, address_column.values());
        createTable(database, Table.event, event_column.values());
        createTable(database, Table.obs, obs_column.values());
        UniqueIdRepository.createTable(database);
        WeightRepository.createTable(database);
        VaccineRepository.createTable(database);
        onUpgrade(database, 1, PathConstants.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PathRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                case 7:
                    upgradeToVersion7Stock(db);
                    upgradeToVersion7Hia2(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private void upgradeToVersion7Stock(SQLiteDatabase db) {
        try {
//            db.execSQL("DROP TABLE IF EXISTS  ");
            StockRepository.createTable(db);
            Vaccine_NamesRepository.createTable(db);
            Vaccine_typesRepository.createTable(db);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion7Stock " + e.getMessage());
        }
    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(VaccinatorApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(VaccinatorApplication.getInstance().getPassword());
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private void insert(SQLiteDatabase db, Class<?> cls, Table table, Column[] cols, Object o, JSONObject serverJsonObject) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        insert(db, cls, table, cols, null, null, o, serverJsonObject);
    }

    private void insert(SQLiteDatabase db, Class<?> cls, Table table, Column[] cols, String referenceColumn, String referenceValue, Object o, JSONObject serverJsonObject) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        try {
            Map<Column, Object> fm = new HashMap<Column, Object>();
            if (!table.name().equalsIgnoreCase("obs") && !table.name().equalsIgnoreCase("address")) {
                fm.put(client_column.json, serverJsonObject);
                fm.put(client_column.baseEntityId, serverJsonObject.getString(client_column.baseEntityId.name()));
                fm.put(client_column.syncStatus, BaseRepository.TYPE_Synced);
                fm.put(client_column.updatedAt, new DateTime(new Date().getTime()));
                if (table.name().equalsIgnoreCase("event")) {
                    fm.put(event_column.eventId, serverJsonObject.getString("id"));
                }
            } else {
                return;
            }

            for (Column c : cols) {
                if (c.name().equalsIgnoreCase(referenceColumn)) {
                    continue;//skip reference column as it is already appended
                }
                Field f = null;
                try {
                    f = cls.getDeclaredField(c.name());// 1st level
                } catch (NoSuchFieldException e) {
                    try {
                        f = cls.getSuperclass().getDeclaredField(c.name()); // 2nd level
                    } catch (NoSuchFieldException e2) {
                        continue;
                    }
                }

                f.setAccessible(true);
                Object v = f.get(o);
                if (c.name().equalsIgnoreCase(event_column.eventId.name())) {
                    fm.put(c, serverJsonObject.getString("id"));//grrr!!!!!!
                } else {
                    fm.put(c, v);
                }
            }

            String columns = referenceColumn == null ? "" : ("`" + referenceColumn + "`,");
            String values = referenceColumn == null ? "" : ("'" + referenceValue + "',");
            ContentValues cv = new ContentValues();

            for (Column c : fm.keySet()) {
                columns += "`" + c.name() + "`,";
                values += formatValue(fm.get(c), c.column()) + ",";

                cv.put(c.name(), formatValueRemoveSingleQuote(fm.get(c), c.column())); //These Fields should be your String values of actual column names

            }
            String beid = fm.get(client_column.baseEntityId).toString();
            String formSubmissionId = null;
            if (table.name().equalsIgnoreCase("event")) {
                formSubmissionId = fm.get(event_column.formSubmissionId).toString();

            }


            if (table.name().equalsIgnoreCase("client") && checkIfExists(table, beid)) {
                //check if a client exists
                if (cv.containsKey(client_column.baseEntityId.name())) {
                    cv.remove(client_column.baseEntityId.name());//this tends to avoid unique constraint exception :)
                }
                int id = db.update(table.name(), cv, client_column.baseEntityId.name() + "=?", new String[]{beid});

            } else if (table.name().equalsIgnoreCase("event") && checkIfExistsByFormSubmissionId(table, formSubmissionId)) {
                //check if a event exists
                if (cv.containsKey(event_column.formSubmissionId.name())) {
                    cv.remove(event_column.formSubmissionId.name());//this tends to avoid unique constraint exception :)
                }
                int id = db.update(table.name(), cv, event_column.formSubmissionId.name() + "=?", new String[]{formSubmissionId});

            } else {
                //for events just insert
                columns = removeEndingComma(columns);
                values = removeEndingComma(values);

                String sql = "INSERT INTO " + table.name() + " (" + columns + ") VALUES (" + values + ")";
                db.execSQL(sql);
            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }

    private Boolean checkIfExists(Table table, String baseEntityId) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + event_column.baseEntityId + " FROM " + table.name() + " WHERE " + event_column.baseEntityId + " = '" + baseEntityId + "'";
            mCursor = getWritableDatabase().rawQuery(query, null);
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) mCursor.close();
        }
        return false;
    }

    private Boolean checkIfExistsByFormSubmissionId(Table table, String formSubmissionId) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + event_column.formSubmissionId + " FROM " + table.name() + " WHERE " + event_column.formSubmissionId + " = '" + formSubmissionId + "'";
            mCursor = getWritableDatabase().rawQuery(query, null);
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) mCursor.close();
        }
        return false;
    }

    public void insert(SQLiteDatabase db, Client client, JSONObject serverJsonObject) {
        try {
//            JSONObject jsonClient = getClient(db, client.getBaseEntityId());
//            if (jsonClient != null) {
//                return;
//            }
            insert(db, Client.class, Table.client, client_column.values(), client, serverJsonObject);
            for (Address a : client.getAddresses()) {
                insert(db, Address.class, Table.address, address_column.values(), address_column.baseEntityId.name(), client.getBaseEntityId(), a, serverJsonObject);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
    }

    public void insert(SQLiteDatabase db, Event event, JSONObject serverJsonObject) {
        try {
            if (StringUtils.isBlank(event.getFormSubmissionId())) {
                event.setFormSubmissionId(generateRandomUUIDString());
            }
            insert(db, Event.class, Table.event, event_column.values(), event, serverJsonObject);
//            for (Obs o : event.getObs()) {
//                insert(db, Obs.class, Table.obs, obs_column.values(), obs_column.formSubmissionId.name(), event.getFormSubmissionId(), o, serverJsonObject);
//            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
    }

    public long batchInsertClients(JSONArray array) throws Exception {
        if (array == null || array.length() == 0) {
            return 0l;
        }

        long lastServerVersion = 0l;

        getWritableDatabase().beginTransaction();

        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);
            if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject) o;
                Client c = convert(jo, Client.class);
                if (c != null) {
                    insert(getWritableDatabase(), c, jo);
                    if (c.getServerVersion() > 01) {
                        lastServerVersion = c.getServerVersion();
                    }
                }
            }
        }

        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
        return lastServerVersion;
    }

    public long batchInsertEvents(JSONArray array, long serverVersion) throws Exception {
        if (array == null || array.length() == 0) {
            return 0l;
        }

        long lastServerVersion = serverVersion;

        getWritableDatabase().beginTransaction();

        for (int i = 0; i < array.length(); i++) {
            Object o = array.get(i);
            if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject) o;
                Event e = convert(jo, Event.class);
                if (e != null) {
                    insert(getWritableDatabase(), e, jo);
                    if (e.getServerVersion() > 01) {
                        lastServerVersion = e.getServerVersion();
                    }
                }
            }
        }

        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
        return lastServerVersion;
    }

    public <T> T convert(JSONObject jo, Class<T> t) {
        if (jo == null) {
            return null;
        }
        try {
            return JsonFormUtils.gson.fromJson(jo.toString(), t);
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            Log.e(getClass().getName(), "Unable to convert: " + jo.toString());
            return null;
        }
    }

    public JSONObject convertToJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return new JSONObject(JsonFormUtils.gson.toJson(object));
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            Log.e(getClass().getName(), "Unable to convert to json : " + object.toString());
            return null;
        }
    }

    public List<JSONObject> getEvents(long startServerVersion, long lastServerVersion) throws JSONException, ParseException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT json FROM " + Table.event.name() +
                    " WHERE " + event_column.serverVersion.name() + " > " + startServerVersion +
                    " AND " + event_column.serverVersion.name() + " <= " + lastServerVersion +
                    " ORDER BY " + event_column.serverVersion.name()
                    , null);
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);


                if (ev.has(event_column.baseEntityId.name())) {
                    String baseEntityId = ev.getString(event_column.baseEntityId.name());
                    JSONObject cl = getClient(getWritableDatabase(), baseEntityId);
                    ev.put("client", cl);
                }
                list.add(ev);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return list;
    }

    public List<JSONObject> getEvents(Date lastSyncDate) throws JSONException, ParseException, UnsupportedEncodingException {

        List<JSONObject> list = new ArrayList<JSONObject>();
        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);

        List<JSONObject> eventAndAlerts = new ArrayList<JSONObject>();

        String query = "select " + event_column.json + "," + event_column.updatedAt + " from " + Table.event.name() + " where " + event_column.updatedAt + " > '" + lastSyncString + "'  and length(" + event_column.json + ")>2 order by " + event_column.updatedAt + " asc ";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                // String jsonEventStr = new String(json, "UTF-8");
                if (StringUtils.isBlank(jsonEventStr) || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }

                JSONObject jsonObectEventOrAlert = new JSONObject(jsonEventStr);
                String type = jsonObectEventOrAlert.has("type") ? jsonObectEventOrAlert.getString("type") : null;
                if (StringUtils.isBlank(type)) { // Skip blank types
                    continue;
                }

                if (!type.equals("Event") && !type.equals("Action")) { // Skip type that isn't Event or Action
                    continue;
                }
                if (jsonObectEventOrAlert.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEventOrAlert.getString(event_column.baseEntityId.name());
                    JSONObject cl = getClientByBaseEntityId(baseEntityId);
                    jsonObectEventOrAlert.put("client", cl);
                }

                eventAndAlerts.add(jsonObectEventOrAlert);
                try {
                    lastSyncDate.setTime(DateUtil.yyyyMMddHHmmss.parse(cursor.getString(1)).getTime());
                } catch (ParseException e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

        if (eventAndAlerts.isEmpty()) {
            return eventAndAlerts;
        }


        Collections.sort(eventAndAlerts, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    String lhvar = "version";
                    String rhvar = "version";
                    if (lhs.getString("type") == "Action") {
                        lhvar = "timeStamp";
                    }

                    if (rhs.getString("type") == "Action") {
                        rhvar = "timeStamp";
                    }

                    if (!lhs.has(lhvar)) {
                        return 1;
                    }
                    if (!rhs.has(rhvar)) {
                        return -1;
                    }
                    if (lhs.getLong(lhvar) > rhs.getLong(rhvar)) {
                        return 1;
                    }
                    if (lhs.getLong(lhvar) < rhs.getLong(rhvar)) {
                        return -1;
                    }
                    return 0;
                } catch (JSONException e) {
                    return -1;
                }
            }
        });

        return eventAndAlerts;
    }

    public List<JSONObject> getEvents(Date lastSyncDate, String syncStatus) throws JSONException, ParseException, UnsupportedEncodingException {

        List<JSONObject> list = new ArrayList<JSONObject>();
        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);

        List<JSONObject> eventAndAlerts = new ArrayList<JSONObject>();

        String query = "select " + event_column.json + "," + event_column.updatedAt + " from " + Table.event.name() + " where " + event_column.syncStatus + " = '" + syncStatus + "' and " + event_column.updatedAt + " > '" + lastSyncString + "'  and length(" + event_column.json + ")>2 order by " + event_column.updatedAt + " asc ";
        Cursor cursor = getWritableDatabase().rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                // String jsonEventStr = new String(json, "UTF-8");
                if (StringUtils.isBlank(jsonEventStr) || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }

                JSONObject jsonObectEventOrAlert = new JSONObject(jsonEventStr);
                String type = jsonObectEventOrAlert.has("type") ? jsonObectEventOrAlert.getString("type") : null;
                if (StringUtils.isBlank(type)) { // Skip blank types
                    continue;
                }

                if (!type.equals("Event") && !type.equals("Action")) { // Skip type that isn't Event or Action
                    continue;
                }
                if (jsonObectEventOrAlert.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEventOrAlert.getString(event_column.baseEntityId.name());
                    JSONObject cl = getClientByBaseEntityId(baseEntityId);
                    jsonObectEventOrAlert.put("client", cl);
                }

                eventAndAlerts.add(jsonObectEventOrAlert);
                try {
                    lastSyncDate.setTime(DateUtil.yyyyMMddHHmmss.parse(cursor.getString(1)).getTime());
                } catch (ParseException e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

        if (eventAndAlerts.isEmpty()) {
            return eventAndAlerts;
        }


        Collections.sort(eventAndAlerts, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    String lhvar = "version";
                    String rhvar = "version";
                    if (lhs.getString("type") == "Action") {
                        lhvar = "timeStamp";
                    }

                    if (rhs.getString("type") == "Action") {
                        rhvar = "timeStamp";
                    }

                    if (!lhs.has(lhvar)) {
                        return 1;
                    }
                    if (!rhs.has(rhvar)) {
                        return -1;
                    }
                    if (lhs.getLong(lhvar) > rhs.getLong(rhvar)) {
                        return 1;
                    }
                    if (lhs.getLong(lhvar) < rhs.getLong(rhvar)) {
                        return -1;
                    }
                    return 0;
                } catch (JSONException e) {
                    return -1;
                }
            }
        });

        return eventAndAlerts;
    }

    public Map<String, Object> getUnSyncedEvents(int limit) throws JSONException, ParseException, UnsupportedEncodingException {
        Map<String, Object> result = new HashMap<>();
        List<JSONObject> clients = new ArrayList<JSONObject>();
        List<JSONObject> events = new ArrayList<JSONObject>();

        String query = "select " + event_column.json + "," + event_column.syncStatus + " from " + Table.event.name() + " where " + event_column.syncStatus + " = '" + BaseRepository.TYPE_Unsynced + "'  and length(" + event_column.json + ")>2 order by " + event_column.updatedAt + " asc limit " + limit;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, null);

            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr) || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                events.add(jsonObectEvent);
                if (jsonObectEvent.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEvent.getString(event_column.baseEntityId.name());
                    JSONObject cl = getUnSyncedClientByBaseEntityId(baseEntityId);
                    if (cl != null) {
                        clients.add(cl);
                    }
                }

            }
            if (!clients.isEmpty()) {
                result.put("clients", clients);
            }
            if (!events.isEmpty()) {
                result.put("events", events);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

        return result;
    }

    public List<JSONObject> getUnSyncedReports(int limit) throws JSONException, ParseException, UnsupportedEncodingException {
        List<JSONObject> reports = new ArrayList<JSONObject>();

        String query = "select " + report_column.json + "," + report_column.syncStatus + " from " + Table.path_reports.name() + " where " + report_column.syncStatus + " = '" + BaseRepository.TYPE_Unsynced + "'  and length(" + report_column.json + ")>2 order by " + report_column.updatedAt + " asc limit " + limit;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, null);

            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr) || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                reports.add(jsonObectEvent);


            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

        return reports;
    }

    public void markAllAsUnSynced() throws JSONException, ParseException, UnsupportedEncodingException {

        String events = "select " + event_column.baseEntityId + "," + event_column.syncStatus + " from " + Table.event.name();
        String clients = "select " + client_column.baseEntityId + "," + client_column.syncStatus + " from " + Table.client.name();
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(clients, null);

            while (cursor.moveToNext()) {
                String beid = (cursor.getString(0));
                if (StringUtils.isBlank(beid) || beid.equals("{}")) { // Skip blank/empty json string
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(client_column.baseEntityId.name(), beid);
                values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);

                getWritableDatabase().update(Table.client.name(), values, client_column.baseEntityId.name() + " = ?", new String[]{beid});


            }
            cursor.close();
            cursor = getWritableDatabase().rawQuery(events, null);

            while (cursor.moveToNext()) {
                String beid = (cursor.getString(0));
                if (StringUtils.isBlank(beid) || beid.equals("{}")) { // Skip blank/empty json string
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(event_column.baseEntityId.name(), beid);
                values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);

                getWritableDatabase().update(Table.event.name(), values, event_column.baseEntityId.name() + " = ?", new String[]{beid});


            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }


    }

    public JSONObject getClient(SQLiteDatabase db, String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT json FROM " + Table.client.name() +
                    " WHERE " + client_column.baseEntityId.name() + "='" + baseEntityId + "' ", null);
            if (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject cl = new JSONObject(jsonEventStr);

                return cl;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public List<JSONObject> getEventsByBaseEntityId(String baseEntityId) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (StringUtils.isBlank(baseEntityId)) {
            return list;
        }

        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT json FROM " + Table.event.name() +
                    " WHERE " + event_column.baseEntityId.name() + "='" + baseEntityId + "' ", null);
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);


                if (ev.has(event_column.baseEntityId.name())) {
                    JSONObject cl = getClient(getWritableDatabase(), baseEntityId);
                    ev.put("client", cl);
                }
                list.add(ev);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return list;
    }

    public JSONObject getEventsByEventId(String eventId) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (StringUtils.isBlank(eventId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT json FROM " + Table.event.name() +
                    " WHERE " + event_column.eventId.name() + "='" + eventId + "' ", null);
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);
                return ev;

            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public JSONObject getEventsByFormSubmissionId(String formSubmissionId) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (StringUtils.isBlank(formSubmissionId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT json FROM " + Table.event.name() +
                    " WHERE " + event_column.formSubmissionId.name() + "='" + formSubmissionId + "' ", null);
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);
                return ev;

            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public JSONObject getClientByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT " + client_column.json + " FROM " + Table.client.name() +
                    " WHERE " + client_column.baseEntityId.name() + "='" + baseEntityId + "' ", null);
            if (cursor.moveToNext()) {
                String jsonString = cursor.getString(0);
                jsonString = jsonString.replaceAll("'", "");
                JSONObject cl = new JSONObject(jsonString);

                return cl;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public JSONObject getUnSyncedClientByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT " + client_column.json + " FROM " + Table.client.name() +
                    " WHERE " + client_column.syncStatus.name() + "='" + BaseRepository.TYPE_Unsynced + "' and " + client_column.baseEntityId.name() + "='" + baseEntityId + "' ", null);
            if (cursor.moveToNext()) {
                String json = cursor.getString(0);
                json = json.replaceAll("'", "");
                JSONObject cl = new JSONObject(json);

                return cl;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public void addorUpdateClient(String baseEntityId, JSONObject jsonObject) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT baseEntityId FROM " + Table.client.name() +
                    " WHERE " + client_column.baseEntityId.name() + "='" + baseEntityId + "' ", null);
            if (cursor.moveToNext()) {
                String beid = cursor.getString(0);
                if (beid != null) {
                    ContentValues values = new ContentValues();
                    values.put(client_column.json.name(), jsonObject.toString());
                    values.put(client_column.updatedAt.name(), dateFormat.format(new Date()));
                    values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
                    getWritableDatabase().update(Table.client.name(), values, client_column.baseEntityId.name() + " = ?", new String[]{baseEntityId});
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
                values.put(client_column.updatedAt.name(), dateFormat.format(new Date()));
                values.put(client_column.json.name(), jsonObject.toString());
                values.put(client_column.baseEntityId.name(), baseEntityId);

                getWritableDatabase().insert(Table.client.name(), null, values);

            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject) {
        try {

            ContentValues values = new ContentValues();
            values.put(event_column.json.name(), jsonObject.toString());
            values.put(event_column.eventType.name(), jsonObject.has("eventType") ? jsonObject.getString("eventType") : "");
            values.put(event_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(event_column.baseEntityId.name(), baseEntityId);
            values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
            //update existing event if eventid present
            if (jsonObject.has(event_column.formSubmissionId.name()) && jsonObject.getString(event_column.formSubmissionId.name()) != null) {
                //sanity check
                if (checkIfExistsByFormSubmissionId(Table.event, jsonObject.getString(event_column.formSubmissionId.name()))) {
                    int id = getWritableDatabase().update(Table.event.name(), values, event_column.formSubmissionId.name() + "=?", new String[]{jsonObject.getString(event_column.formSubmissionId.name())});
                } else {
                    //that odd case
                    values.put(event_column.formSubmissionId.name(), jsonObject.getString(event_column.formSubmissionId.name()));

                    getWritableDatabase().insert(Table.event.name(), null, values);

                }
            } else {
// a case here would be if an event comes from openmrs
                getWritableDatabase().insert(Table.event.name(), null, values);
            }

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void addReport(JSONObject jsonObject) {
        try {

            ContentValues values = new ContentValues();
            values.put(report_column.json.name(), jsonObject.toString());
            values.put(report_column.reportType.name(), jsonObject.has(report_column.reportType.name()) ? jsonObject.getString(report_column.reportType.name()) : "");
            values.put(report_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(report_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
            //update existing event if eventid present
            if (jsonObject.has(report_column.formSubmissionId.name()) && jsonObject.getString(report_column.formSubmissionId.name()) != null) {
                //sanity check
                if (checkIfExistsByFormSubmissionId(Table.path_reports, jsonObject.getString(report_column.formSubmissionId.name()))) {
                    int id = getWritableDatabase().update(Table.path_reports.name(), values, report_column.formSubmissionId.name() + "=?", new String[]{jsonObject.getString(report_column.formSubmissionId.name())});
                } else {
                    //that odd case
                    values.put(report_column.formSubmissionId.name(), jsonObject.getString(report_column.formSubmissionId.name()));

                    getWritableDatabase().insert(Table.path_reports.name(), null, values);

                }
            } else {
// a case here would be if an event comes from openmrs
                getWritableDatabase().insert(Table.path_reports.name(), null, values);
            }

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void markEventAsSynced(String formSubmissionId) {
        try {

            ContentValues values = new ContentValues();
            values.put(event_column.formSubmissionId.name(), formSubmissionId);
            values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Synced);

            getWritableDatabase().update(Table.event.name(), values, event_column.formSubmissionId.name() + " = ?", new String[]{formSubmissionId});

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void markReportAsSynced(String formSubmissionId) {
        try {

            ContentValues values = new ContentValues();
            values.put(report_column.formSubmissionId.name(), formSubmissionId);
            values.put(report_column.syncStatus.name(), BaseRepository.TYPE_Synced);

            getWritableDatabase().update(Table.path_reports.name(), values, report_column.formSubmissionId.name() + " = ?", new String[]{formSubmissionId});

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void markClientAsSynced(String baseEntityId) {
        try {

            ContentValues values = new ContentValues();
            values.put(client_column.baseEntityId.name(), baseEntityId);
            values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Synced);

            getWritableDatabase().update(Table.client.name(), values, client_column.baseEntityId.name() + " = ?", new String[]{baseEntityId});

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void markEventsAsSynced(Map<String, Object> syncedEvents) {
        try {
            List<JSONObject> clients = syncedEvents.containsKey("clients") ? (List<JSONObject>) syncedEvents.get("clients") : null;
            List<JSONObject> events = syncedEvents.containsKey("events") ? (List<JSONObject>) syncedEvents.get("events") : null;

            if (clients != null && !clients.isEmpty()) {
                for (JSONObject client : clients) {
                    String baseEntityId = client.getString(client_column.baseEntityId.name());
                    markClientAsSynced(baseEntityId);
                }
            }
            if (events != null && !events.isEmpty()) {
                for (JSONObject event : events) {
                    String formSubmissionId = event.getString(event_column.formSubmissionId.name());
                    markEventAsSynced(formSubmissionId);
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }

    }

    public void markReportsAsSynced(List<JSONObject> syncedReports) {
        try {

            if (syncedReports != null && !syncedReports.isEmpty()) {
                for (JSONObject report : syncedReports) {
                    String formSubmissionId = report.getString(report_column.formSubmissionId.name());
                    markReportAsSynced(formSubmissionId);
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }

    }

    private String getCreateTableColumn(Column col) {
        ColumnAttribute c = col.column();
        return "`" + col.name() + "` " + getSqliteType(c.type()) + (c.pk() ? " PRIMARY KEY " : "");
    }

    private String removeEndingComma(String str) {
        if (str.trim().endsWith(",")) {
            return str.substring(0, str.lastIndexOf(","));
        }
        return str;
    }

    private void createTable(SQLiteDatabase db, Table table, Column[] columns) {
        String cl = "";
        String indl = "";
        for (Column cc : columns) {
            cl += getCreateTableColumn(cc) + ",";
            if (cc.column().index()) {
                indl += cc.name() + ",";
            }
        }
        cl = removeEndingComma(cl);
        indl = removeEndingComma(indl);
        String create_tb = "CREATE TABLE " + table.name() + " ( " + cl + " )";
        String create_id = "CREATE INDEX " + table.name() + "_index ON " + table.name() + " (" + indl + "); ";

        db.execSQL(create_tb);
        db.execSQL(create_id);
    }

    private Object getValue(Cursor cur, Column c) throws JSONException, ParseException {
        int ind = cur.getColumnIndex(c.name());
        if (cur.isNull(ind)) {
            return null;
        }

        ColumnAttribute.Type type = c.column().type();
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.text.name())) {
            return "" + cur.getString(ind) + "";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.bool.name())) {
            return cur.getInt(ind) == 0 ? false : true;
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.date.name())) {
            return new DateTime(dateFormat.parse(cur.getString(ind)).getTime());
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.list.name())) {
            return new JSONArray(cur.getString(ind));
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.map.name())) {
            return new JSONObject(cur.getString(ind));
        }

        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.longnum.name())) {
            return cur.getLong(ind);
        }

        return null;
    }

    private String formatValue(Object v, ColumnAttribute c) {
        if (v == null || v.toString().trim().equalsIgnoreCase("")) {
            return null;
        }

        ColumnAttribute.Type type = c.type();
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.text.name())) {
            return "'" + v.toString() + "'";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.bool.name())) {
            return (Boolean.valueOf(v.toString()) ? 1 : 0) + "";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.date.name())) {
            return "'" + getSQLDate((DateTime) v) + "'";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.list.name())) {
            return "'" + new Gson().toJson(v) + "'";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.map.name())) {
            return "'" + new Gson().toJson(v) + "'";
        }

        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.longnum.name())) {
            return v.toString();
        }
        return null;
    }

    private String formatValueRemoveSingleQuote(Object v, ColumnAttribute c) {
        String formatValue = formatValue(v, c);
        if (formatValue != null) {
            formatValue = formatValue.replace("'", "");
        }

        return formatValue;
    }

    private String getSQLDate(DateTime date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.toDate());
        } finally {

        }
    }

    public ArrayList<HashMap<String, String>> rawQuery(SQLiteDatabase db, String query) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    maplist.add(map);
                } while (cursor.moveToNext());
            }

            return maplist;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    // Definitions
    public enum Table {
        client(client_column.values()), event(event_column.values()),
        path_reports(report_column.values()),
        address(address_column.values()), obs(obs_column.values());
        private Column[] columns;

        public Column[] columns() {
            return columns;
        }

        private Table(Column[] columns) {
            this.columns = columns;
        }
    }


    public enum client_column implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, true),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        baseEntityId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        identifiers(ColumnAttribute.Type.map, false, true),
        attributes(ColumnAttribute.Type.map, false, true),
        firstName(ColumnAttribute.Type.text, false, false),
        middleName(ColumnAttribute.Type.text, false, false),
        lastName(ColumnAttribute.Type.text, false, false),
        birthdate(ColumnAttribute.Type.date, false, false),
        deathdate(ColumnAttribute.Type.date, false, false),
        birthdateApprox(ColumnAttribute.Type.bool, false, false),
        deathdateApprox(ColumnAttribute.Type.bool, false, false),
        gender(ColumnAttribute.Type.text, false, false),
        relationships(ColumnAttribute.Type.map, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true);


        private client_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    public enum address_column implements Column {
        baseEntityId(ColumnAttribute.Type.text, false, true),
        addressType(ColumnAttribute.Type.text, false, true),
        startDate(ColumnAttribute.Type.date, false, false),
        endDate(ColumnAttribute.Type.date, false, false),
        addressFields(ColumnAttribute.Type.map, false, false),
        latitude(ColumnAttribute.Type.text, false, false),
        longitude(ColumnAttribute.Type.text, false, false),
        geopoint(ColumnAttribute.Type.text, false, false),
        postalCode(ColumnAttribute.Type.text, false, false),
        subTown(ColumnAttribute.Type.text, false, false),
        town(ColumnAttribute.Type.text, false, false),
        subDistrict(ColumnAttribute.Type.text, false, false),
        countyDistrict(ColumnAttribute.Type.text, false, false),
        cityVillage(ColumnAttribute.Type.text, false, false),
        stateProvince(ColumnAttribute.Type.text, false, false),
        country(ColumnAttribute.Type.text, false, false);

        private address_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    public enum event_column implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, false),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        eventId(ColumnAttribute.Type.text, true, true),
        baseEntityId(ColumnAttribute.Type.text, false, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        locationId(ColumnAttribute.Type.text, false, false),
        eventDate(ColumnAttribute.Type.date, false, true),
        eventType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, false),
        providerId(ColumnAttribute.Type.text, false, false),
        entityType(ColumnAttribute.Type.text, false, false),
        details(ColumnAttribute.Type.map, false, false),
        version(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true);

        private event_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    public enum report_column implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, false),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        reportId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        locationId(ColumnAttribute.Type.text, false, false),
        reportDate(ColumnAttribute.Type.date, false, true),
        reportType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, false),
        providerId(ColumnAttribute.Type.text, false, false),
        entityType(ColumnAttribute.Type.text, false, false),
        version(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true);

        private report_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    public enum obs_column implements Column {
        formSubmissionId(ColumnAttribute.Type.text, false, true),
        fieldType(ColumnAttribute.Type.text, false, false),
        fieldDataType(ColumnAttribute.Type.text, false, false),
        fieldCode(ColumnAttribute.Type.text, false, false),
        parentCode(ColumnAttribute.Type.text, false, false),
        values(ColumnAttribute.Type.list, false, false),
        comments(ColumnAttribute.Type.text, false, false),
        formSubmissionField(ColumnAttribute.Type.text, false, true);

        private obs_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        private ColumnAttribute column;

        public ColumnAttribute column() {
            return column;
        }
    }

    private String getSqliteType(ColumnAttribute.Type type) {
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.text.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.bool.name())) {
            return "boolean";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.date.name())) {
            return "datetime";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.list.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.map.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.longnum.name())) {
            return "integer";
        }
        return null;
    }

    protected String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public boolean deleteClient(String baseEntityId) {
        try {
            int rowsAffected = getWritableDatabase().delete(Table.client.name(), client_column.baseEntityId.name() + " = ?", new String[]{baseEntityId});
            if (rowsAffected > 0) {
                return true;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return false;
    }

    public boolean deleteEventsByBaseEntityId(String baseEntityId) {

        try {
            int rowsAffected = getWritableDatabase().delete(Table.event.name(), event_column.baseEntityId.name() + " = ? AND " + event_column.eventType.name() + " != ?", new String[]{baseEntityId, MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT});
            if (rowsAffected > 0) {
                return true;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return false;
    }

    /**
     * Version 2 added some columns to the ec_child table
     *
     * @param database
     */
    private void upgradeToVersion2(SQLiteDatabase database) {
        // Create the new ec_child table
        try {
            String newTableNameSuffix = "_v2";
            String originalTableName = PathConstants.CHILD_TABLE_NAME;

            Set<String> searchColumns = new LinkedHashSet<String>();
            searchColumns.add(CommonFtsObject.idColumn);
            searchColumns.add(CommonFtsObject.relationalIdColumn);
            searchColumns.add(CommonFtsObject.phraseColumn);
            searchColumns.add(CommonFtsObject.isClosedColumn);

            String[] mainConditions = this.commonFtsObject.getMainConditions(originalTableName);
            if (mainConditions != null)
                for (String mainCondition : mainConditions) {
                    if (!mainCondition.equals(CommonFtsObject.isClosedColumnName))
                        searchColumns.add(mainCondition);
                }

            String[] sortFields = this.commonFtsObject.getSortFields(originalTableName);
            if (sortFields != null) {
                for (String sortValue : sortFields) {
                    if (sortValue.startsWith("alerts.")) {
                        sortValue = sortValue.split("\\.")[1];
                    }
                    searchColumns.add(sortValue);
                }
            }

            String joinedSearchColumns = StringUtils.join(searchColumns, ",");

            String searchSql = "create virtual table "
                    + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                    + " using fts4 (" + joinedSearchColumns + ");";
            Log.d(TAG, "Create query is\n---------------------------\n" + searchSql);

            database.execSQL(searchSql);

            // Run insert query
            ArrayList<String> newlyAddedFields = new ArrayList<>();
            newlyAddedFields.add("BCG_2");
            newlyAddedFields.add("inactive");
            newlyAddedFields.add("lost_to_follow_up");
            ArrayList<String> oldFields = new ArrayList<>();

            for (String curColumn : searchColumns) {
                curColumn = curColumn.trim();
                if (curColumn.contains(" ")) {
                    String[] curColumnParts = curColumn.split(" ");
                    curColumn = curColumnParts[0];
                }

                if (!newlyAddedFields.contains(curColumn)) {
                    oldFields.add(curColumn);
                } else {
                    Log.d(TAG, "Skipping field " + curColumn + " from the select query");
                }
            }

            String insertQuery = "insert into "
                    + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                    + " (" + StringUtils.join(oldFields, ", ") + ")"
                    + " select " + StringUtils.join(oldFields, ", ") + " from "
                    + CommonFtsObject.searchTableName(originalTableName);

            Log.d(TAG, "Insert query is\n---------------------------\n" + insertQuery);
            database.execSQL(insertQuery);

            // Run the drop query
            String dropQuery = "drop table " + CommonFtsObject.searchTableName(originalTableName);
            Log.d(TAG, "Drop query is\n---------------------------\n" + dropQuery);
            database.execSQL(dropQuery);

            // Run rename query
            String renameQuery = "alter table "
                    + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                    + " rename to " + CommonFtsObject.searchTableName(originalTableName);
            Log.d(TAG, "Rename query is\n---------------------------\n" + renameQuery);
            database.execSQL(renameQuery);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(WeightRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(WeightRepository.FORMSUBMISSION_INDEX);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion3 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion4" + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion5(SQLiteDatabase db) {
        try {
            RecurringServiceTypeRepository.createTable(db);
            RecurringServiceRecordRepository.createTable(db);

            RecurringServiceTypeRepository recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
            DatabaseUtils.populateRecurringServices(context, db, recurringServiceTypeRepository);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion5 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        try {
            ZScoreRepository.createTable(db);
            db.execSQL(WeightRepository.ALTER_ADD_Z_SCORE_COLUMN);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion6" + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion7Hia2(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            DailyTalliesRepository.createTable(db);
            MonthlyTalliesRepository.createTable(db);
            createTable(db, Table.path_reports, report_column.values());
            HIA2IndicatorsRepository.createTable(db);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

            //csv column no to table column names
            Map<Integer, String> columnMappings = new HashMap<>();
            columnMappings.put(0, HIA2IndicatorsRepository.ID_COLUMN);
            columnMappings.put(1, HIA2IndicatorsRepository.INDICATOR_CODE);
            columnMappings.put(2, HIA2IndicatorsRepository.LABEL);
            columnMappings.put(3, HIA2IndicatorsRepository.DHIS_ID);
            columnMappings.put(4, HIA2IndicatorsRepository.DESCRIPTION);
            columnMappings.put(999, HIA2IndicatorsRepository.CATEGORY);//999 means nothing really, just to hold the column name for categories since category is a row in the hia2 csv

            List<Map<String, String>> csvData = Utils.populateTableFromCSV(context, HIA2IndicatorsRepository.INDICATORS_CSV_FILE, columnMappings);
            HIA2IndicatorsRepository hIA2IndicatorsRepository = VaccinatorApplication.getInstance().hIA2IndicatorsRepository();
            hIA2IndicatorsRepository.save(db, csvData);


        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion7Hia2 " + e.getMessage());
        }
    }

    private void upgradeToVersion8(SQLiteDatabase db) {
        try {

            // Recurring service json changed. update
            RecurringServiceTypeRepository recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
            DatabaseUtils.populateRecurringServices(context, db, recurringServiceTypeRepository);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion8 " + Log.getStackTraceString(e));
        }
    }

}
