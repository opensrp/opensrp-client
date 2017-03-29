package org.ei.opensrp.core.db.repository;

import android.content.ContentValues;
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

import net.sqlcipher.database.SQLiteQueryBuilder;

import static org.ei.opensrp.core.db.utils.ColumnAttribute.*;
import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.domain.Drug;
import org.ei.opensrp.core.db.domain.DrugOrder;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.util.StringUtil;
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

	private static CESQLiteHelper instance;

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
		@Override
		public String toString() {
			return name();
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
		@Override
		public String toString() {
			return name();
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
		@Override
		public String toString() {
			return name();
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
		@Override
		public String toString() {
			return name();
		}
	}

	public enum drug_column implements Column {
		creator (Type.text, false, false),
		dateCreated (Type.date, false, true),
		editor (Type.text, false, false),
		dateEdited (Type.date, false, true),
		voided (Type.bool, false, false),
		dateVoided (Type.date, false, false),
		voider (Type.text, false, false),
		voidReason (Type.text, false, false),

		id(Type.text, true, false),
		drugName(Type.text, false, true),
		drugBaseName (Type.text, false, false),
		codes (Type.map, false, true),
		dosageForm (Type.text, false, false),
		route (Type.text, false, false),
		doseStrength (Type.text, false, false),
		units (Type.text, false, false),
		maximumDailyDose (Type.text, false, false),
		minimumDailyDose (Type.text, false, false),
		description (Type.text, false, false),
		combination (Type.text, false, false);

		drug_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
		@Override
		public String toString() {
			return name();
		}
	}

	public enum drug_order_column implements Column {
		creator (Type.text, false, false),
		dateCreated (Type.date, false, true),
		editor (Type.text, false, false),
		dateEdited (Type.date, false, true),
		voided (Type.bool, false, false),
		dateVoided (Type.date, false, false),
		voider (Type.text, false, false),
		voidReason (Type.text, false, false),

		_id(Type.text, true, false),
		baseEntityId(Type.text, false, true),
		codes (Type.map, false, true),
		orderType(Type.text, false, false),
		drug (Type.text, false, true),
		orderNumber (Type.text, false, false),
		action (Type.text, false, false),
		previousOrder (Type.text, false, false),
		dateActivated (Type.date, false, false),
		discontinuedBy (Type.text, false, false),
		discontinuedDate (Type.date, false, false),
		discontinuedReason (Type.text, false, false),
		autoExpireDate (Type.date, false, false),
		urgency (Type.text, false, false),
		instructions (Type.text, false, false),
		orderReason (Type.text, false, false),
		dosingType (Type.text, false, false),
		dose (Type.text, false, false),
		frequency (Type.text, false, false),
		descriptions (Type.text, false, false),
		quantity (Type.text, false, false),
		orderer (Type.text, false, false),
		route (Type.text, false, false),
		quantityUnits (Type.text, false, false);

		drug_order_column(Type type, boolean pk, boolean index) {
			this.column = new ColumnAttribute(type, pk, index);
		}
		private ColumnAttribute column;
		public ColumnAttribute column() {
			return column;
		}
		@Override
		public String toString() {
			return name();
		}
	}

	public enum Table{
		client (client_column.values()), event (event_column.values()), 
		address (address_column.values()), obs (obs_column.values()),
		drug (drug_column.values()), drug_order (drug_order_column.values()),
		;
		private Column[] columns;
		public Column[] columns() {
			return columns;
		}
		Table(Column[] columns) {
			this.columns = columns;
		}
		@Override
		public String toString() {
			return name();
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
		createTable(Table.drug, drug_column.values());
		createTable(Table.drug_order, drug_order_column.values());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CESQLiteHelper.class.getName(),
			"Upgrading database from version " + oldVersion + " to "
			+ newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + SmsTarseelTables.unsubmitted_outbound);
	}

	public static CESQLiteHelper getClientEventDb(Context context){
		if (instance == null){
			instance = new CESQLiteHelper(context);
		}
		return instance;
	}

	private void insert(Class<?> cls, Table table, Column[] cols, Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		insert(cls, table, cols, null, null, o);
	}
	
	private void insert(Class<?> cls, Table table, Column[] cols, String referenceColumn, String referenceValue, Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		Map<Column, Object> fm = toColumnValue(cls, cols, referenceColumn, o);
		
		String columns = referenceColumn==null?"":("`"+referenceColumn+"`,");
		String values = referenceColumn==null?"":("'"+referenceValue+"',");
		for (Column c : fm.keySet()) {
			columns += "`"+c.name()+"`,";
			values += formatValue(fm.get(c), c.column())+",";
		}
		
		columns = removeEndingComma(columns);
		values = removeEndingComma(values);
		
		String sql = "INSERT INTO "+table.name()+" ("+columns+") VALUES ("+values+")";
		Log.v(getClass().getName(), sql);
		getDatabase().execSQL(sql);
	}

	private Map<Column, Object> toColumnValue(Class cls, Column[] cols, String referenceColumn, Object o) throws NoSuchFieldException, IllegalAccessException {
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
		return fm;
	}

	private void update(Class<?> cls, Table table, Column[] cols, Column pk, String rowFilter, String referenceColumn, Object o) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException{
		Map<Column, Object> fm = toColumnValue(cls, cols, referenceColumn, o);
		fm.remove(pk);

		ContentValues contentValues = new ContentValues();

		for (Column c : fm.keySet()) {
			contentValues.put(c.name(), formatValueUnquoted(fm.get(c), c.column()));
		}

		getDatabase().update(table.name(), contentValues, rowFilter, null);
	}
	
	public void insert(Client client) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(Client.class, Table.client, client_column.values(), client);
		for (Address a : client.getAddresses()) {
			insert(Address.class, Table.address, address_column.values(), address_column.baseEntityId.name(), client.getBaseEntityId(), a);
		}
	}

	public void update(Client client) throws NoSuchFieldException, IllegalAccessException {
		update(Client.class, Table.client, client_column.values(), client_column.baseEntityId, client_column.baseEntityId.name()+"='"+client.getBaseEntityId()+"'", null, client);

		// delete and re-insert all addresses as Client is a consolidated JSON
		getDatabase().delete(Table.address.name(), address_column.baseEntityId.name()+"='"+client.getBaseEntityId()+"'", null);
		for (Address a : client.getAddresses()) {
			insert(Address.class, Table.address, address_column.values(), address_column.baseEntityId.name(), client.getBaseEntityId(), a);
		}
	}

	public void update(Event event) throws NoSuchFieldException, IllegalAccessException {
		update(Event.class, Table.event, event_column.values(), event_column._id, event_column._id.name()+"='"+event.getId()+"'", null, event);

		// delete and re-insert all obs as Event is a consolidated JSON
		getDatabase().delete(Table.obs.name(), obs_column.eventId.name()+"='"+event.getId()+"'", null);
		for (Obs o : event.getObs()) {
			insert(Obs.class, Table.obs, obs_column.values(), obs_column.eventId.name(), event.getId(), o);
		}
	}

	public void update(Drug drug) throws NoSuchFieldException, IllegalAccessException {
		update(Drug.class, Table.drug, drug_column.values(), drug_column.id, drug_column.id.name()+"='"+drug.getId()+"'", null, drug);
	}

	public void update(DrugOrder drugOrder) throws NoSuchFieldException, IllegalAccessException {
		update(DrugOrder.class, Table.drug_order, drug_column.values(), drug_order_column._id, drug_order_column._id.name()+"='"+drugOrder.getId()+"'", null, drugOrder);
	}

	public void insert(Event event) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(Event.class, Table.event, event_column.values(), event);
		for (Obs o : event.getObs()) {
			insert(Obs.class, Table.obs, obs_column.values(), obs_column.eventId.name(), event.getId(), o);
		}
	}

	public void insert(Drug drug) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(Drug.class, Table.drug, drug_column.values(), drug);
	}

	public void insert(DrugOrder drugOrder) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		insert(DrugOrder.class, Table.drug_order, drug_order_column.values(), drugOrder);
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

			ares.close();

			cl.put("addresses", alist);
			Log.v(getClass().getName(), "HHHHHHHHHHHHHHHHH"+cl.toString());

			clist.add(Utils.getStringDateAwareGson().fromJson(cl.toString(), Client.class));
		}
		cres.close();
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
			Log.v(getClass().getName(), "HHHHHHHHHHHHHHHHH" + cl.toString());

			ares.close();
			cres.close();
			return Utils.getStringDateAwareGson().fromJson(cl.toString(), Client.class);
		}
		cres.close();
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
			JSONObject ev = readEvent(eres, null, null);

			JSONArray olist = new JSONArray();
			Cursor ores = getDatabase().rawQuery("SELECT * FROM "+Table.obs.name()+" WHERE "+obs_column.eventId.name()+"='"+ev.getString(event_column._id.name())+"'", null);
			while (ores.moveToNext()) {
				JSONObject o = readObs(ores, null, null);
				olist.put(o);
			}

			ev.put("obs", olist);
			Log.v(getClass().getName(), "HHHHHHHHHHHHHHHHH"+ev.toString());

			elist.add(Utils.getStringDateAwareGson().fromJson(ev.toString(), Event.class));
			ores.close();
		}

		eres.close();
		return elist;
	}

	public List<Drug> getDrugs() throws JSONException, ParseException {
		List<Drug> list = new ArrayList<>();
		Cursor res = getDatabase().rawQuery("SELECT * FROM " + Table.drug.name(), null);
		while (res.moveToNext()) {
			JSONObject dv = readDrug(res, null, null);
			list.add(Utils.getStringDateAwareGson().fromJson(dv.toString(), Drug.class));
		}

		res.close();
		return list;
	}

	public List<DrugOrder> getDrugOrders(String baseEntityId) throws JSONException, ParseException {
		List<DrugOrder> list = new ArrayList<>();
		Cursor res = getDatabase().rawQuery("SELECT * FROM " + Table.drug_order.name(), null);
		while (res.moveToNext()) {
			JSONObject dv = readDrugOrder(res, null, null);
			list.add(Utils.getStringDateAwareGson().fromJson(dv.toString(), DrugOrder.class));
		}

		res.close();
		return list;
	}

	public Event getEvent(String eventId) throws JSONException, ParseException {
		Cursor eres = getDatabase().rawQuery("SELECT * FROM " + Table.event.name() +
						" WHERE " + event_column._id.name() + "='" + eventId + "'", null);
		while (eres.moveToNext()) {
			JSONObject ev = readEvent(eres, null, null);

			JSONArray olist = new JSONArray();
			Cursor ores = getDatabase().rawQuery("SELECT * FROM "+Table.obs.name()+" WHERE "+obs_column.eventId.name()+"='"+ev.getString(event_column._id.name())+"'", null);
			while (ores.moveToNext()) {
				JSONObject o = readObs(ores, null, null);
				olist.put(o);
			}

			ev.put("obs", olist);
			Log.v(getClass().getName(), "HHHHHHHHHHHHHHHHH"+ev.toString());

			ores.close();
			eres.close();
			return Utils.getStringDateAwareGson().fromJson(ev.toString(), Event.class);
		}

		eres.close();
		return null;
	}

	public Drug getDrug(String drugIdOrName) throws JSONException, ParseException {
		Cursor res = getDatabase().rawQuery("SELECT * FROM " + Table.drug.name() +
				" WHERE " + drug_column.id.name() + "='" + drugIdOrName + "'" +
				" OR " + drug_column.drugName.name() + "='" + drugIdOrName + "'", null);
		while (res.moveToNext()) {
			JSONObject dv = readDrug(res, null, null);

			res.close();
			return Utils.getStringDateAwareGson().fromJson(dv.toString(), Drug.class);
		}

		res.close();
		return null;
	}

	public DrugOrder getDrugOrder(String id) throws JSONException, ParseException {
		Cursor res = getDatabase().rawQuery("SELECT * FROM " + Table.drug_order.name() +
				" WHERE " + drug_order_column._id.name() + "='" + id + "'" , null);
		while (res.moveToNext()) {
			JSONObject dv = readDrugOrder(res, null, null);

			res.close();
			return Utils.getStringDateAwareGson().fromJson(dv.toString(), DrugOrder.class);
		}

		res.close();
		return null;
	}

	public Cursor getClientEventCursor(boolean distinctClients, String filter, String addressType, String group, String order) {
		// Get clients participating in filter
		String sql = "SELECT "+(distinctClients?"DISTINCT":"")+" client." + client_column.baseEntityId +", client.*, address.*, event._id, event.eventType, event.entityType, event.locationId, obs.* FROM " + Table.client.name() + " client " +
				" LEFT JOIN " + Table.address + " address ON " +
				"		address."+ address_column.baseEntityId + " = client." + client_column.baseEntityId + " AND address.addressType = '" + addressType + "' " +
				" LEFT JOIN " + Table.event + " event ON event." + event_column.baseEntityId + " = client." + client_column.baseEntityId +
				" LEFT JOIN " + Table.obs + " obs ON obs." + obs_column.eventId + " = event." + event_column._id + " AND obs.`" + obs_column.values + "` NOT IN('', '[]') ";

		if (StringUtils.isNotBlank(filter)){
			sql += " WHERE "+filter ;
		}
		if (StringUtils.isNotBlank(group)){
			sql += " GROUP BY "+group;
		}
		if (StringUtils.isNotBlank(order)){
			sql += " ORDER BY " + order ;
		}

		Log.v(getClass().getName(), "CE SQL : " + sql);

		return getDatabase().rawQuery(sql, null);
	}

	public List<ClientEvent> getClientEvent(String filter, String addressType, String order) throws JSONException, ParseException {
		Cursor res = getClientEventCursor(true, filter, addressType, client_column.baseEntityId.name(), order);
		List<ClientEvent> ce = convertToClientEventList(res);
		res.close();
		return ce;
	}

	/**
	 * MUST have been queried with distinctClients flag
	 * @param res
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static List<ClientEvent> convertToClientEventList(Cursor res) throws JSONException, ParseException {
		List<ClientEvent> CE_LIST = new ArrayList<>();

		while (res.moveToNext()){
			CE_LIST.add(convertToClientEvent(res));
		}

		return CE_LIST;
	}

	public static ClientEvent convertToClientEvent(Cursor mainCursor) throws JSONException, ParseException {
		JSONObject c = readClient(mainCursor, null, null);
		Client client = Utils.getStringDateAwareGson().fromJson(c.toString(), Client.class);

		ClientEvent clientEvent = new ClientEvent(client, null);

		Log.v(CESQLiteHelper.class.getName(), "READING Events and Obs for CLIENT: "+c.toString());

		String sql = "SELECT e.*, obs.* FROM event e "+
				" LEFT JOIN " + Table.obs + " obs ON obs." + obs_column.eventId + " = e." + event_column._id + " AND obs.`" + obs_column.values + "` NOT IN('', '[]') "+
				" WHERE e." + event_column.baseEntityId + " = '"+ client.getBaseEntityId() +"' ";

		Log.v(CESQLiteHelper.class.getName(), "EO SQL : " + sql);

		Cursor eventCursor = instance.getDatabase().rawQuery(sql, null);

		Map<String, JSONObject> eventMap = new HashMap<>();
		while (eventCursor.moveToNext()){
			JSONObject o = null;
			if (!eventCursor.isNull(eventCursor.getColumnIndex(obs_column.eventId.name()))){// if obs is not null for given record
				o = readObs(eventCursor, null, null);
			}

			if (o == null){ // no obs just read client and go as it means no event exists and here must exists a client record
				JSONObject e = readEvent(eventCursor, null, null);
				eventMap.put(e.getString(event_column._id.name()), e);
			}
			else {
				String eid = getValue(eventCursor, event_column._id).toString();
				if(!eventMap.containsKey(eid)){ // if not read already then read and put obs later
					eventMap.put(eid, readEvent(eventCursor, null, null));
				}// otherwise read and put

				if(eventMap.get(eid).has("obs")) {
					eventMap.get(eid).getJSONArray("obs").put(o);
				}
				else {
					eventMap.get(eid).put("obs", new JSONArray().put(o));
				}
			}
		}
		eventCursor.close();

		Log.v(CESQLiteHelper.class.getName(), "EVENTS:"+eventMap);

		for (JSONObject jo: eventMap.values()) {
			clientEvent.addEvent(Utils.getStringDateAwareGson().fromJson(jo.toString(), Event.class));
		}

		// donot close res as it should be closed by manager service
		return clientEvent;
	}

	public int getClientEventCount(boolean distinctClients, String filter, String addressType, String group) throws JSONException, ParseException {
		String sql = "SELECT COUNT("+(distinctClients?"DISTINCT":"")+" "+group+") c FROM "+Table.client.name()+" client "+
				" LEFT JOIN "+Table.address+" address ON " +
				"		address."+address_column.baseEntityId+" = client."+client_column.baseEntityId+" AND address.addressType = '"+addressType+"' "+
				" LEFT JOIN "+Table.event+" event ON event."+event_column.baseEntityId+" = client."+client_column.baseEntityId+
				" LEFT JOIN "+Table.obs+" obs ON obs."+obs_column.eventId+" = event."+event_column._id+" AND obs.`"+obs_column.values+"` NOT IN('', '[]') ";

		if (StringUtils.isNotBlank(filter)){
			sql += " WHERE "+filter ;
		}

		Log.v(getClass().getName(), "CE COUNT SQL : "+sql);

		Cursor res = getDatabase().rawQuery(sql, null);

		int c = -1;
		if (res.moveToNext()) {
			c = res.getInt(0);
		}

		res.close();

		return c;
	}

	private static JSONObject readObs(Cursor res, Integer startColumnIndex, Integer endColumnIndex) throws JSONException, ParseException {
		JSONObject o = new JSONObject();
		for (Column oc : Table.obs.columns()) {
			if(!oc.name().equalsIgnoreCase(event_column._id.name())){//skip reference column
				o.put(oc.name(), getValue(res, oc, startColumnIndex, endColumnIndex));
			}
		}
		return o;
	}

	private static JSONObject readEvent(Cursor res, Integer startColumnIndex, Integer endColumnIndex) throws JSONException, ParseException {
		JSONObject ev = new JSONObject();
		for (Column ec : Table.event.columns()) {
			ev.put(ec.name(), getValue(res, ec, startColumnIndex, endColumnIndex));
		}
		return ev;
	}

	private static JSONObject readDrug(Cursor res, Integer startColumnIndex, Integer endColumnIndex) throws JSONException, ParseException {
		JSONObject dv = new JSONObject();
		for (Column dc : Table.drug.columns()) {
			dv.put(dc.name(), getValue(res, dc, startColumnIndex, endColumnIndex));
		}
		return dv;
	}

	private static JSONObject readDrugOrder(Cursor res, Integer startColumnIndex, Integer endColumnIndex) throws JSONException, ParseException {
		JSONObject dv = new JSONObject();
		for (Column dc : Table.drug_order.columns()) {
			dv.put(dc.name(), getValue(res, dc, startColumnIndex, endColumnIndex));
		}
		return dv;
	}

	private static JSONObject readClient(Cursor res, Integer startColumnIndex, Integer endColumnIndex) throws JSONException, ParseException {
		JSONObject cl = new JSONObject();
		for (Column cc : Table.client.columns()) {
			cl.put(cc.name(), getValue(res, cc, startColumnIndex, endColumnIndex));
		}

		JSONObject a = new JSONObject();
		for (Column cc : Table.address.columns()) {
			if (!cc.name().equalsIgnoreCase(client_column.baseEntityId.name())) {//skip reference column
				a.put(cc.name(), getValue(res, cc, startColumnIndex, endColumnIndex));
			}
		}
		cl.put("addresses", new JSONArray().put(a));
		return cl;
	}

	public List<Obs> getObs(String baseEntityId, String eventType, String order, String... fields) throws JSONException, ParseException {
		List<Obs> olist = new ArrayList<>();
		Cursor ores = getDatabase().rawQuery("SELECT * FROM " + Table.obs.name() +" obs "+
				" JOIN "+Table.event.name()+" e ON e."+event_column._id.name()+"= obs."+obs_column.eventId.name()+
				" WHERE e." + event_column.baseEntityId.name() + "='" + baseEntityId + "' " +
				" AND obs.`"+obs_column.values.name()+"` IS NOT NULL " +
				" AND obs.`"+obs_column.values.name()+"` <> '' " +
				" AND obs.`"+obs_column.values.name()+"` <> '[]' " +
				(StringUtils.isBlank(eventType)?"":" AND e."+event_column.eventType.name()+"='"+eventType+"' ")+
				(fields != null && fields.length > 0? (" AND (obs."+obs_column.fieldCode.name()+" IN ('"+StringUtils.join(fields, "','")+"') " +
						" OR obs."+obs_column.formSubmissionField.name()+" IN ('"+StringUtils.join(fields, "','")+"') )"):"")+
				(StringUtils.isBlank(order)?"":" ORDER BY "+order+" ")
				, null);
		while (ores.moveToNext()) {
			JSONObject ov = readObs(ores, null, null);

			olist.add(Utils.getStringDateAwareGson().fromJson(ov.toString(), Obs.class));
		}
		ores.close();
		return olist;
	}

	private static Object getValue(Cursor cur, Column c) throws JSONException, ParseException {
		return getValue(cur, c, null, null);
	}

	private static Object getValue(Cursor cur, Column c, Integer startColumn, Integer endColumn) throws JSONException, ParseException {
		Integer ind = null;
		if(startColumn == null || endColumn == null){// no range specified use regular method
			ind = cur.getColumnIndex(c.name());
		}
		else {// in specified range go through columns to find desired one
			String[] cnl = cur.getColumnNames();
			for (int i = startColumn; i <= endColumn; i++){
				if(cnl[i].equalsIgnoreCase(c.name())){
					ind = i;
					break;
				}
			}
			if (ind == null){ // not found in given range then use regular method
				ind = cur.getColumnIndex(c.name());
			}
		}

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

	private String formatValueUnquoted(Object v, ColumnAttribute c){
		if(v == null || v.toString().trim().equalsIgnoreCase("")){
			return null;
		}

		Type type = c.type();
		if(type.name().equalsIgnoreCase(Type.text.name())){
			return v.toString();
		}
		if(type.name().equalsIgnoreCase(Type.bool.name())){
			return (Boolean.valueOf(v.toString())?1:0)+"";
		}
		if(type.name().equalsIgnoreCase(Type.date.name())){
			return getSQLDate((DateTime) v);
		}
		if(type.name().equalsIgnoreCase(Type.list.name())){
			return new Gson().toJson(v);
		}
		if(type.name().equalsIgnoreCase(Type.map.name())){
			return new Gson().toJson(v);
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
		cursor.close();
		return maplist;
	}

	public Cursor rawQueryForCursor(String query){
		Cursor cursor = getDatabase().rawQuery(query, null);
		return cursor;
	}
}
