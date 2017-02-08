package org.ei.opensrp.core.db.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import static org.ei.opensrp.core.db.utils.ColumnAttribute.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ei.opensrp.core.db.domain.Address;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.Event;
import org.ei.opensrp.core.db.domain.Obs;
import org.ei.opensrp.core.db.utils.Column;
import org.ei.opensrp.core.db.utils.ColumnAttribute;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CESQLiteHelper extends SQLiteOpenHelper {

	public enum client_column implements Column {
		creator (Type.text, false, false),
		dateCreated (Type.date, false, true), 
		editor (Type.text, false, false), 
		dateEdited (Type.date, false, true), 
		voided (Type.bool, false, false), 
		dateVoided (Type.date, false, false), 
		voider (Type.text, false, false), 
		voidReason (Type.text, false, false),
		
		baseEntityId(Type.text, true, false), 
		identifiers (Type.map, false, true), 
		attributes (Type.map, false, true),
		firstName (Type.text, false, false), 
		middleName (Type.text, false, false), 
		lastName (Type.text, false, false), 
		birthdate (Type.date, false, false), 
		deathdate (Type.date, false, false), 
		birthdateApprox (Type.bool, false, false), 
		deathdateApprox (Type.bool, false, false), 
		gender (Type.text, false, false), 
		relationships (Type.map, false, false);
		
		client_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
	}
	public enum address_column implements Column{
		baseEntityId (Type.text, false, true), 
		addressType (Type.text, false, true), 
		startDate (Type.date, false, false), 
		endDate (Type.date, false, false), 
		addressFields (Type.map, false, false), 
		latitude (Type.text, false, false), 
		longitude (Type.text, false, false), 
		geopoint (Type.text, false, false), 
		postalCode (Type.text, false, false), 
		subTown (Type.text, false, false), 
		town (Type.text, false, false), 
		subDistrict (Type.text, false, false), 
		countyDistrict (Type.text, false, false),
		cityVillage (Type.text, false, false), 
		stateProvince (Type.text, false, false), 
		country (Type.text, false, false);

		address_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
	}
	public enum event_column implements Column{
		creator (Type.text, false, false), 
		dateCreated (Type.date, false, true), 
		editor (Type.text, false, false), 
		dateEdited (Type.date, false, false), 
		voided (Type.bool, false, false), 
		dateVoided (Type.date, false, false), 
		voider (Type.text, false, false), 
		voidReason (Type.text, false, false),
		
		_id (Type.text, true, false),
		identifiers (Type.map, false, true),
		baseEntityId (Type.text, false, true), 
		locationId (Type.text, false, false), 
		eventDate (Type.date, false, true), 
		eventType (Type.text, false, true), 
		formSubmissionId (Type.text, false, false), 
		providerId (Type.text, false, false), 
		entityType (Type.text, false, false), 
		details (Type.map, false, false), 
		version (Type.text, false, false);

		event_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
	}
	public enum obs_column implements Column{
		eventId (Type.text, false, true),
		fieldType (Type.text, false, false), 
		fieldDataType (Type.text, false, false), 
		fieldCode (Type.text, false, false), 
		parentCode (Type.text, false, false),
		values (Type.list, false, false),
		humanReadableValues (Type.list, false, false),
		comments (Type.text, false, false), 
		formSubmissionField (Type.text, false, true);
		
		obs_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
	}

	public enum Table{
		client (client_column.values()), event (event_column.values()), 
		address (address_column.values()), obs (obs_column.values());
		private Column[] columns;
		public Column[] columns() {
			return columns;
		}
		Table(Column[] columns) {
			this.columns = columns;
		}
	}

	private String getSqliteType (Type type){
		if(type.name().equalsIgnoreCase(Type.text.name())){
			return "varchar";
		}
		if(type.name().equalsIgnoreCase(Type.bool.name())){
			return "boolean";
		}
		if(type.name().equalsIgnoreCase(Type.date.name())){
			return "datetime";
		}
		if(type.name().equalsIgnoreCase(Type.list.name())){
			return "varchar";
		}
		if(type.name().equalsIgnoreCase(Type.map.name())){
			return "varchar";
		}
		return null;
	}
	private static final String DATABASE_NAME = "opensrp.db";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase database;
	
	public CESQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private String getCreateTableColumn(Column col) {
		ColumnAttribute c = col.column();
		return "`"+col.name()+"` "+ getSqliteType(c.type()) + (c.pk()?" PRIMARY KEY ":" collate nocase ");
	}
	
	private String removeEndingComma (String str){
		if(str.trim().endsWith(",")){
			return str.substring(0, str.lastIndexOf(","));
		}
		return str;
	}
	
	private void createTable(Table table, Column[] columns) {
		String cl = "";
		String indl = "";
		for (Column cc : columns) {
			cl += getCreateTableColumn(cc) +",";
			if(cc.column().index()){
				indl+=cc.name()+",";
			}
		}
		cl = removeEndingComma(cl);
		indl = removeEndingComma(indl);
		String create_tb = "CREATE TABLE "+table.name() + " ( "+cl +" )";
		String create_id = "CREATE INDEX "+table.name()+"_index ON "+table.name()+" ("+indl+"); ";
		
		getDatabase().execSQL(create_tb);
		getDatabase().execSQL(create_id);
	}
	
	private SQLiteDatabase getDatabase(){
		if(database == null){
			database = this.getWritableDatabase();
		}
		return database;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		this.database = database;
		createTable(Table.client, client_column.values());
		createTable(Table.address, address_column.values());
		createTable(Table.event, event_column.values());
		createTable(Table.obs, obs_column.values());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CESQLiteHelper.class.getName(),
			"Upgrading database from version " + oldVersion + " to "
			+ newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + SmsTarseelTables.unsubmitted_outbound);
	}

	public static CESQLiteHelper getClientEventDb(Context context){
		return new CESQLiteHelper(context);
	}

	private void insert(Class<?> cls, Table table, Column[] cols, Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		insert(cls, table, cols, null, null, o);
	}
	
	private void insert(Class<?> cls, Table table, Column[] cols, String referenceColumn, String referenceValue, Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		Map<Column, Object> fm = new HashMap<>();
		
		for (Column c : cols) {
			if(c.name().equalsIgnoreCase(referenceColumn)){
				continue;//skip reference column as it is already appended
			}
			Field f;
			try{
				f = cls.getDeclaredField(c.name());// 1st level
			}
			catch (NoSuchFieldException e) {
				try{
					f = cls.getSuperclass().getDeclaredField(c.name()); // 2nd level
				}
				catch(NoSuchFieldException e2){
					f = cls.getSuperclass().getSuperclass().getDeclaredField(c.name()); // 3rd level
				}
			}
			
			f.setAccessible(true);
			Object v = f.get(o);
			fm.put(c, v);
		}
		
		String columns = referenceColumn==null?"":("`"+referenceColumn+"`,");
		String values = referenceColumn==null?"":("'"+referenceValue+"',");
		for (Column c : fm.keySet()) {
			columns += "`"+c.name()+"`,";
			values += formatValue(fm.get(c), c.column())+",";
		}
		
		columns = removeEndingComma(columns);
		values = removeEndingComma(values);
		
		String sql = "INSERT INTO "+table.name()+" ("+columns+") VALUES ("+values+")";
		Log.i("", sql);
		getDatabase().execSQL(sql);
	}
	
	public void insert(Client client) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(Client.class, Table.client, client_column.values(), client);
		for (Address a : client.getAddresses()) {
			insert(Address.class, Table.address, address_column.values(), address_column.baseEntityId.name(), client.getBaseEntityId(), a);
		}
	}

	public void insert(Event event) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(Event.class, Table.event, event_column.values(), event);
		for (Obs o : event.getObs()) {
			insert(Obs.class, Table.obs, obs_column.values(), obs_column.eventId.name(), event.getId(), o);
		}
	}
	
	public List<Client> getClients() throws JSONException, ParseException {
		List<Client> clist = new ArrayList<>();
		Cursor cres = getDatabase().rawQuery("SELECT * FROM "+Table.client.name(), null);
		while (cres.moveToNext()) {
			JSONObject cl = new JSONObject();
			for (Column cc : Table.client.columns()) {
				cl.put(cc.name(), getValue(cres, cc));
			}
			
			JSONArray alist = new JSONArray();
			Cursor ares = getDatabase().rawQuery("SELECT * FROM "+Table.address.name()+" WHERE "+address_column.baseEntityId.name()+"='"+cl.getString(client_column.baseEntityId.name())+"'", null);
			while (ares.moveToNext()) {
				JSONObject a = new JSONObject();
				for (Column cc : Table.address.columns()) {
					if(!cc.name().equalsIgnoreCase(client_column.baseEntityId.name())){//skip reference column
						cl.put(cc.name(), getValue(ares, cc));
					}
				}
				alist.put(a);
			}
			
			cl.put("addresses", alist);
			Log.i("mytag", "HHHHHHHHHHHHHHHHH"+cl.toString());
			Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
				@Override
				public DateTime deserialize(JsonElement e, java.lang.reflect.Type t, JsonDeserializationContext jd) throws JsonParseException {
					return new DateTime(e.getAsString());
				}
			}).create();
			clist.add(g.fromJson(cl.toString(), Client.class));
		}
		return clist;
	}

	public Client getClient(String identifier) throws JSONException, ParseException {
		Cursor cres = getDatabase().rawQuery("SELECT * FROM "+Table.client.name()+
				" WHERE "+client_column.baseEntityId.name()+"='"+identifier+"' " +
				" OR "+client_column.identifiers+" LIKE '%\""+identifier+"\"%'", null);
		if (cres.moveToNext()) {
			JSONObject cl = new JSONObject();
			for (Column cc : Table.client.columns()) {
				cl.put(cc.name(), getValue(cres, cc));
			}

			JSONArray alist = new JSONArray();
			Cursor ares = getDatabase().rawQuery("SELECT * FROM " + Table.address.name() + " WHERE " + address_column.baseEntityId.name() + "='" + cl.getString(client_column.baseEntityId.name()) + "'", null);
			while (ares.moveToNext()) {
				JSONObject a = new JSONObject();
				for (Column cc : Table.address.columns()) {
					if (!cc.name().equalsIgnoreCase(client_column.baseEntityId.name())) {//skip reference column
						a.put(cc.name(), getValue(ares, cc));
					}
				}
				alist.put(a);
			}

			cl.put("addresses", alist);
			Log.i("mytag", "HHHHHHHHHHHHHHHHH" + cl.toString());
			Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
				@Override
				public DateTime deserialize(JsonElement e, java.lang.reflect.Type t, JsonDeserializationContext jd) throws JsonParseException {
					return new DateTime(e.getAsString());
				}
			}).create();

			return g.fromJson(cl.toString(), Client.class);
		}
		return null;
	}

	public List<Event> getEvents(String baseEntityId, String eventType, String order) throws JSONException, ParseException {
		List<Event> elist = new ArrayList<>();
		Cursor eres = getDatabase().rawQuery("SELECT * FROM " + Table.event.name() +
				" WHERE " + event_column.baseEntityId.name() + "='" + baseEntityId + "'" +
				(StringUtils.isBlank(eventType)?"":" AND "+event_column.eventType.name()+"='"+eventType+"' ")+
				(StringUtils.isBlank(order)?"":" ORDER BY "+order+"' ")
				, null);
		while (eres.moveToNext()) {
			JSONObject ev = new JSONObject();
			for (Column ec : Table.event.columns()) {
				ev.put(ec.name(), getValue(eres, ec));
			}

			JSONArray olist = new JSONArray();
			Cursor ores = getDatabase().rawQuery("SELECT * FROM "+Table.obs.name()+" WHERE "+obs_column.eventId.name()+"='"+ev.getString(event_column._id.name())+"'", null);
			while (ores.moveToNext()) {
				JSONObject o = new JSONObject();
				for (Column oc : Table.obs.columns()) {
					if(!oc.name().equalsIgnoreCase(event_column._id.name())){//skip reference column
						o.put(oc.name(), getValue(eres, oc));
					}
				}
				olist.put(o);
			}

			ev.put("obs", olist);
			Log.i("mytag", "HHHHHHHHHHHHHHHHH"+ev.toString());
			Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
				@Override
				public DateTime deserialize(JsonElement e, java.lang.reflect.Type t, JsonDeserializationContext jd) throws JsonParseException {
					return new DateTime(e.getAsString());
				}
			}).create();
			elist.add(g.fromJson(ev.toString(), Event.class));
			ores.close();
		}
		return elist;
	}

	public List<Obs> getObs(String baseEntityId, String eventType, String order, String... fields) throws JSONException, ParseException {
		List<Obs> olist = new ArrayList<>();
		Cursor ores = getDatabase().rawQuery("SELECT * FROM " + Table.obs.name() +" obs "+
				" JOIN "+Table.event.name()+" e ON e."+event_column._id.name()+"= obs."+obs_column.eventId.name()+
				" WHERE e." + event_column.baseEntityId.name() + "='" + baseEntityId + "' " +
				" AND obs."+obs_column.values.name()+" IS NOT NULL " +
				" AND obs."+obs_column.values.name()+" <> '' " +
				" AND obs."+obs_column.values.name()+" <> '[]' " +
				(StringUtils.isBlank(eventType)?"":" AND e."+event_column.eventType.name()+"='"+eventType+"' ")+
				(fields != null && fields.length > 0? (" AND (obs."+obs_column.fieldCode.name()+" IN ("+StringUtils.join(fields)+") " +
						" OR obs."+obs_column.formSubmissionField.name()+" IN ("+StringUtils.join(fields)+") )"):"")+
				(StringUtils.isBlank(order)?"":" ORDER BY "+order+"' ")
				, null);
		while (ores.moveToNext()) {
			JSONObject ov = new JSONObject();
			for (Column oc : Table.obs.columns()) {
				ov.put(oc.name(), getValue(ores, oc));
			}

			Log.i("mytag", "HHHHHHHHHHHHHHHHH"+ov.toString());
			Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
				@Override
				public DateTime deserialize(JsonElement e, java.lang.reflect.Type t, JsonDeserializationContext jd) throws JsonParseException {
					return new DateTime(e.getAsString());
				}
			}).create();
			olist.add(g.fromJson(ov.toString(), Obs.class));
		}
		return olist;
	}
	
	private Object getValue(Cursor cur, Column c) throws JSONException, ParseException {
		int ind = cur.getColumnIndex(c.name());
		if(cur.isNull(ind)){
			return null;
		}
		
		Type type = c.column().type();
		if(type.name().equalsIgnoreCase(Type.text.name())){
			return ""+cur.getString(ind)+"";
		}
		if(type.name().equalsIgnoreCase(Type.bool.name())){
			return cur.getInt(ind) != 0;
		}
		if(type.name().equalsIgnoreCase(Type.date.name())){
			return new DateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cur.getString(ind)).getTime());
		}
		if(type.name().equalsIgnoreCase(Type.list.name())){
			return new JSONArray(cur.getString(ind));
		}
		if(type.name().equalsIgnoreCase(Type.map.name())){
			return new JSONObject(cur.getString(ind));
		}
		return null;
	}
	
	private String formatValue(Object v, ColumnAttribute c){
		if(v == null || v.toString().trim().equalsIgnoreCase("")){
			return null;
		}
		
		Type type = c.type();
		if(type.name().equalsIgnoreCase(Type.text.name())){
			return "'"+v.toString()+"'";
		}
		if(type.name().equalsIgnoreCase(Type.bool.name())){
			return (Boolean.valueOf(v.toString())?1:0)+"";
		}
		if(type.name().equalsIgnoreCase(Type.date.name())){
			return "'"+getSQLDate((DateTime) v)+"'";
		}
		if(type.name().equalsIgnoreCase(Type.list.name())){
			return "'"+new Gson().toJson(v)+"'";
		}
		if(type.name().equalsIgnoreCase(Type.map.name())){
			return "'"+new Gson().toJson(v)+"'";
		}
		return null;
	}
	
	private String getSQLDate(DateTime date){
		try{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.toDate());
		}
		finally{
			
		}
	}

	public ArrayList<HashMap<String, String>> rawQuery(String query){
		Cursor cursor = getDatabase().rawQuery(query, null);
		ArrayList<HashMap<String, String>> maplist = new ArrayList<>();
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<>();
				for(int i=0; i<cursor.getColumnCount();i++)
				{
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}

				maplist.add(map);
			} while (cursor.moveToNext());
		}
		getDatabase().close();
		return maplist;
	}

	public Cursor rawQueryForCursor(String query){
		Cursor cursor = getDatabase().rawQuery(query, null);
		return cursor;
	}
}
