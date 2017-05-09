package org.ei.opensrp.util;

import android.content.Context;
import android.util.Xml;

import org.ei.opensrp.Logger.Logger;
import org.ei.opensrp.domain.SyncStatus;
import org.ei.opensrp.domain.form.FormSubmission;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by koros on 9/28/15.
 */
public class FormUtils {

    static FormUtils instance;
    Context mContext;
    org.ei.opensrp.Context theAppContext;

    private static final String shouldLoadValueKey = "shouldLoadValue";
    private static final String relationalIdKey = "relationalid";
    private static final String databaseIdKey = "_id";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FormUtils(Context context){
        mContext = context;
        theAppContext = org.ei.opensrp.Context.getInstance();
    }

    public static FormUtils getInstance(Context ctx){
        if (instance == null){
            instance = new FormUtils(ctx);
        }
        return instance;
    }

    public FormSubmission generateFormSubmisionFromXMLString(String entity_id, String formData, String formName, JSONObject overrides) throws Exception{
        JSONObject formSubmission = XML.toJSONObject(formData);

        FileUtilities fu = new FileUtilities();
        fu.write("xmlform.txt", formData);
        fu.write("xmlformsubmission.txt", formSubmission.toString());
        System.out.println(formSubmission);

        android.util.Log.d("test0",formSubmission.toString());
        // use the form_definition.json to iterate through fields
        String formDefinitionJson = readFileFromAssetsFolder("www/form/" + formName + "/form_definition.json");
        JSONObject formDefinition = new JSONObject(formDefinitionJson);

        String rootNodeKey = formSubmission.keys().next();

        //retrieve the id, if it fails use the provided value by the param
        entity_id = formSubmission.getJSONObject(rootNodeKey).has(databaseIdKey) ? formSubmission.getJSONObject(rootNodeKey).getString(databaseIdKey) : generateRandomUUIDString();

        //String bindPath = formDefinition.getJSONObject("form").getString("bind_type");
        JSONObject fieldsDefinition = formDefinition.getJSONObject("form");
        JSONArray populatedFieldsArray = getPopulatedFieldsForArray(fieldsDefinition, entity_id, formSubmission, overrides);

        // replace all the fields in the form
        formDefinition.getJSONObject("form").put("fields", populatedFieldsArray);

        //get the subforms
        if (formDefinition.getJSONObject("form").has("sub_forms")){
            JSONObject subFormDefinition = formDefinition.getJSONObject("form").getJSONArray("sub_forms").getJSONObject(0);
            //get the bind path for the sub-form helps us to locate the node that holds the data in the corresponding data json
            String bindPath = subFormDefinition.getString("default_bind_path");

            //get the actual sub-form data
            JSONArray subForms = new JSONArray();
            Object subFormDataObject = getObjectAtPath(bindPath.split("/"), formSubmission);
            if(subFormDataObject instanceof JSONObject){
                JSONObject subFormData = (JSONObject)subFormDataObject;
                JSONArray subFormFields = getFieldsArrayForSubFormDefinition(subFormDefinition);
                String relationalId = subFormData.has(relationalIdKey) ? subFormData.getString(relationalIdKey) : entity_id;
                String id = subFormData.has(databaseIdKey) ? subFormData.getString(databaseIdKey) : generateRandomUUIDString();
                //String id = generateRandomUUIDString();
                JSONObject subFormInstance = getFieldValuesForSubFormDefinition(subFormDefinition, relationalId, id, subFormData, overrides);
                JSONArray subFormInstances = new JSONArray();
                subFormInstances.put(0,subFormInstance);
                subFormDefinition.put("instances", subFormInstances);
                subFormDefinition.put("fields", subFormFields);
                subForms.put(0, subFormDefinition);
            }else if (subFormDataObject instanceof JSONArray){
                JSONArray subFormDataArray = (JSONArray)subFormDataObject;
                JSONArray subFormFields = getFieldsArrayForSubFormDefinition(subFormDefinition);
                JSONArray subFormInstances = new JSONArray();

                // the id of each subform is contained in the attribute of the enclosing element
                for (int i = 0; i < subFormDataArray.length(); i++){
                    JSONObject subFormData = subFormDataArray.getJSONObject(i);
                    String relationalId = subFormData.has(relationalIdKey) ? subFormData.getString(relationalIdKey) : entity_id;
                    //String id = subFormData.has(databaseIdKey) ? subFormData.getString(databaseIdKey) : generateRandomUUIDString();
                    String id = generateRandomUUIDString();
                    JSONObject subFormInstance = getFieldValuesForSubFormDefinition(subFormDefinition, relationalId, id, subFormData, overrides);
                    subFormInstances.put(i,subFormInstance);
                }
                subFormDefinition.put("instances", subFormInstances);
                subFormDefinition.put("fields", subFormFields);
                subForms.put(0, subFormDefinition);
            }

            // replace the subforms field with real data
            formDefinition.getJSONObject("form").put("sub_forms", subForms);
        }

        String instanceId = generateRandomUUIDString();
        String entityId = retrieveIdForSubmission(formDefinition);
        String formDefinitionVersionString = formDefinition.getString("form_data_definition_version");

        String clientVersion = String.valueOf(new Date().getTime());
        String instance = formDefinition.toString();
        FormSubmission fs = new FormSubmission(instanceId, entityId, formName, instance, clientVersion, SyncStatus.PENDING, formDefinitionVersionString);
        return fs;
    }

    public String generateXMLInputForFormWithEntityId(String entityId, String formName, String overrides){
        try
        {
            //get the field overrides map
            JSONObject fieldOverrides = new JSONObject();
            if (overrides != null){
                fieldOverrides = new JSONObject(overrides);
                String overridesStr = fieldOverrides.getString("fieldOverrides");
                fieldOverrides = new JSONObject(overridesStr);
            }

            // use the form_definition.json to get the form mappings
            String formDefinitionJson = readFileFromAssetsFolder("www/form/" + formName + "/form_definition.json");
            JSONObject formDefinition = new JSONObject(formDefinitionJson);
            String bindPath = formDefinition.getJSONObject("form").getString("bind_type");

            String sql = "select * from " + bindPath + " where id='" + entityId + "'";
            String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql);

            JSONObject entityJson = new JSONObject();
            if (dbEntity != null && !dbEntity.isEmpty()){
                entityJson = new JSONObject(dbEntity);
            }

            //read the xml form model, the expected form model that is passed to the form mirrors it
            String formModelString = readFileFromAssetsFolder("www/form/" + formName + "/model.xml").replaceAll("\n"," ").replaceAll("\r", " ");
            InputStream is = new ByteArrayInputStream(formModelString.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            //skip processing <model><instance>
            NodeList els = ((Element)document.getElementsByTagName("model").item(0)).getElementsByTagName("instance");
            Element el = (Element)els.item(0);
            NodeList entries = el.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                Node n = entries.item(i);
                if (n instanceof Element){
                    Element node = (Element)n;
                    writeXML(node, serializer, fieldOverrides, formDefinition, entityJson, null);
                }
            }

            serializer.endDocument();

            String xml = writer.toString();
            //xml = xml.replaceAll("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>","");//56 !!!this ain't working
            //Add model and instance tags
            xml = xml.substring(56);
            System.out.println(xml);

            Logger.largeLog("xmlform",xml);

            return xml;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private void writeXML(Element node, XmlSerializer serializer, JSONObject fieldOverrides, JSONObject formDefinition, JSONObject entityJson, String parentId){
        try {
            String nodeName = node.getNodeName();
            String entityId = entityJson.has("id") ? entityJson.getString("id") : generateRandomUUIDString();
            String relationalId = entityJson.has(relationalIdKey) ? entityJson.getString(relationalIdKey) : parentId;

            serializer.startTag("", nodeName);

            // write the xml attributes
            writeXMLAttributes(node, serializer, entityId, relationalId);

            String nodeValue = retrieveValueForNodeName(nodeName, entityJson, formDefinition);
            //overwrite the node value with contents from overrides map
            if (fieldOverrides.has(nodeName)){
                nodeValue = fieldOverrides.getString(nodeName);
            }
            //write the node value
            if (nodeValue != null){
                serializer.text(nodeValue);
            }

            List<String> subFormNames = getSubFormNames(formDefinition);

            // get all child nodes
            NodeList entries = node.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                if (entries.item(i) instanceof Element) {
                    Element child = (Element) entries.item(i);
                    String fieldName = child.getNodeName();

                    if(!subFormNames.isEmpty() && subFormNames.contains(fieldName)) {
                        /** its a subform element process it **/
                        // get the subform definition
                        JSONArray subForms = formDefinition.getJSONObject("form").getJSONArray("sub_forms");
                        JSONObject subFormDefinition = retriveSubformDefinitionForBindPath(subForms, fieldName);
                        if (subFormDefinition != null){

                            String childTableName = subFormDefinition.getString("bind_type");
                            String sql = "select * from '" + childTableName + "' where relationalid = '" + entityId + "'";
                            String childRecordsString = theAppContext.formDataRepository().queryList(sql);
                            JSONArray childRecords = new JSONArray(childRecordsString);

                            JSONArray fieldsArray = subFormDefinition.getJSONArray("fields");
                            // check whether we are supposed to load the id of the child record
                            JSONObject idFieldDefn = getJsonFieldFromArray("id", fieldsArray);

                            // definition for id
                            boolean shouldLoadId = idFieldDefn.has(shouldLoadValueKey) && idFieldDefn.getBoolean(shouldLoadValueKey);

                            if (shouldLoadId && childRecords != null && childRecords.length() > 0){
                                for (int k = 0; k < childRecords.length(); k++){
                                    JSONObject childEntityJson = childRecords.getJSONObject(k);
                                    writeXML(child, serializer, fieldOverrides, subFormDefinition, childEntityJson, entityId);
                                }
                            }else{
                                writeXML(child, serializer, fieldOverrides, subFormDefinition, new JSONObject(), entityId);
                            }
                        }
                    } // Check if the node contains other elements
                    else if(hasChildElements(child)){
                        writeXML(child, serializer, fieldOverrides, formDefinition, new JSONObject(), entityId);
                    }else {
                        //its not a sub-form element write its value
                        serializer.startTag("", fieldName);
                        // write the xml attributes
                        writeXMLAttributes(child, serializer, null, null); // a value node doesn't have id or relationaId fields
                        //write the node value
                        String value = retrieveValueForNodeName(fieldName, entityJson, formDefinition);
                        //write the node value
                        if (value != null){
                            serializer.text(value);
                        }

                        //overwrite the node value with contents from overrides map
                        if (fieldOverrides.has(fieldName)) {
                            serializer.text(fieldOverrides.getString(fieldName));
                        }

                        serializer.endTag("", fieldName);
                    }
                }
            }

            serializer.endTag("", node.getNodeName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the provided node has Child elements
     * @param element
     * @return
     */
    public static boolean hasChildElements(Node element) {
        NodeList children = element.getChildNodes();
        for (int i = 0;i < children.getLength();i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate through the provided array and retrieve a json object whose name attribute matches the name supplied
     *
     * @param fieldName
     * @param array
     * @return
     */
    private JSONObject getJsonFieldFromArray(String fieldName, JSONArray array){
        try{
            if (array != null){
                for (int i = 0; i < array.length(); i++){
                    JSONObject field = array.getJSONObject(i);
                    String name = field.has("name") ? field.getString("name") : null;
                    if (name.equals(fieldName)){
                        return field;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * retrieves node value for cases which the nodename don't match the name of the xml element
     * @param nodeName
     * @param entityJson
     * @param formDefinition
     * @return
     */
    private String retrieveValueForNodeName(String nodeName, JSONObject entityJson, JSONObject formDefinition){
        try{
            if (entityJson != null && entityJson.length() > 0){
                JSONObject fieldsObject = formDefinition.has("form") ? formDefinition.getJSONObject("form") :
                        formDefinition.has("sub_forms") ? formDefinition.getJSONObject("sub_forms") : formDefinition;
                if (fieldsObject.has("fields")){
                    JSONArray fields = fieldsObject.getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++){
                        JSONObject field = fields.getJSONObject(i);
                        String bindPath = field.has("bind") ? field.getString("bind") : null;
                        String name = field.has("name") ? field.getString("name") : null;

                        boolean matchingNodeFound = bindPath != null && name != null && bindPath.endsWith(nodeName)
                                || name != null && name.equals(nodeName);

                        if (matchingNodeFound){
                            if (field.has("shouldLoadValue") && field.getBoolean("shouldLoadValue")){
                                String keyName = entityJson.has(nodeName) ? nodeName : name;
                                if (entityJson.has(keyName)){
                                    return entityJson.getString(keyName);
                                }else{
                                    return "";
                                }
                            }else{
                                // the shouldLoadValue flag isnt set
                                return "";
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Currently not used but, the method should retrieve the path of a given node,
     * useful when confirming if the current node has been properly mapped to its bind_path
     **/
    private String getXPath(Node node)
    {
        Node parent = node.getParentNode();
        if (parent == null) {
            return "/" + node.getNodeName();
        }
        return getXPath(parent) + "/";
    }

    private List<String> getSubFormNames(JSONObject formDefinition) throws Exception{
        List<String> subFormNames = new ArrayList<String>();
        if (formDefinition.has("form") && formDefinition.getJSONObject("form").has("sub_forms")){
            JSONArray subForms = formDefinition.getJSONObject("form").getJSONArray("sub_forms");
            for (int i = 0; i< subForms.length() ; i++){
                JSONObject subForm = subForms.getJSONObject(i);
                String subFormNameStr = subForm.getString("default_bind_path");
                String[] path = subFormNameStr.split("/");
                String subFormName = path[path.length - 1]; // the last token
                subFormNames.add(subFormName);
            }
        }
        return subFormNames;
    }

    private JSONObject retriveSubformDefinitionForBindPath(JSONArray subForms, String fieldName) throws Exception{
        for (int i = 0; i< subForms.length() ; i++){
            JSONObject subForm = subForms.getJSONObject(i);
            String subFormNameStr = subForm.getString("default_bind_path");
            String[] path = subFormNameStr.split("/");
            String subFormName = path[path.length - 1]; // the last token
            if (fieldName.equalsIgnoreCase(subFormName)){
                return subForm;
            }
        }
        return null;
    }

    private void writeXMLAttributes(Element node, XmlSerializer serializer, String id, String relationalId){
        try {
            // get a map containing the attributes of this node
            NamedNodeMap attributes = node.getAttributes();

            // get the number of nodes in this map
            int numAttrs = attributes.getLength();

            if (id != null){
                serializer.attribute("", databaseIdKey, id);
            }

            if (relationalId != null){
                serializer.attribute("", relationalIdKey, relationalId);
            }

            for (int i = 0; i < numAttrs; i++) {
                Attr attr = (Attr) attributes.item(i);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                serializer.attribute("", attrName, attrValue);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRandomUUIDString(){
        return UUID.randomUUID().toString();
    }

    private String retrieveIdForSubmission(JSONObject jsonObject) throws Exception{
        JSONArray fields = jsonObject.getJSONObject("form").getJSONArray("fields");
        for (int i = 0; i < fields.length(); i++){
            JSONObject field = fields.getJSONObject(i);
            if (field.has("name") && field.getString("name").equalsIgnoreCase("id")){
                return field.getString("value");
            }
        }
        return null;
    }

    public Object getObjectAtPath(String[] path, JSONObject jsonObject) throws Exception{
        JSONObject object = jsonObject;
        int i = 0;
        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject){
                    object = object.getJSONObject(path[i]);
                }
                else if (o instanceof JSONArray){
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }
            i++;
        }
        return object.has(path[i]) ? object.get(path[i]) : null;
    }

    public JSONArray getPopulatedFieldsForArray(JSONObject fieldsDefinition, String entityId, JSONObject jsonObject, JSONObject overrides) throws  Exception{
        String bindPath = fieldsDefinition.getString("bind_type");
        String sql = "select * from " + bindPath + " where id='" + entityId + "'";
        String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql);

        JSONObject entityJson = new JSONObject();
        if (dbEntity != null && !dbEntity.isEmpty()){
            entityJson = new JSONObject(dbEntity);
        }

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        for (int i = 0; i < fieldsArray.length(); i++){
            JSONObject item = fieldsArray.getJSONObject(i);
            if (!item.has("name"))
                continue; // skip elements without name

            String itemName = item.getString("name");
            boolean shouldLoadValue = item.has("shouldLoadValue") && item.getBoolean("shouldLoadValue");

            if (item.has("bind")){
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");
                String value = getValueForPath(path, jsonObject);
                item.put("value", value);
            }

            if (shouldLoadValue && overrides.has(item.getString("name"))){
                if (!item.has("value")) // if the value is not set use the value in the overrides filed
                    item.put("value", overrides.getString(item.getString("name")));
            }

            // map the id field for child elements
            if (isForeignIdPath(item)){
                String value = null;
                if (entityJson.length() > 0 && shouldLoadValue){
                    //retrieve the child attributes
                    value = retrieveValueForLinkedRecord(item.getString("source"), entityJson);
                }
                // generate uuid if its still not available
                if (item.getString("source").endsWith(".id") && value == null){
                    value = generateRandomUUIDString();
                }

                if (value != null && !item.has("value"))
                    item.put("value", value);
            }

            // add source property if not available
            if (!item.has("source")){
                item.put("source", bindPath + "." +  item.getString("name"));
            }

            if (itemName.equalsIgnoreCase("id") && !isForeignIdPath(item)){
                assert entityId != null;
                item.put("value", entityId);
            }

            if(itemName.equalsIgnoreCase("start") || itemName.equalsIgnoreCase("end")){
                try {
                    boolean isEndTime = itemName.equalsIgnoreCase("end");
                    String val = item.has("value") ? item.getString("value") : sdf.format(new Date());
                    if (isEndTime){
                        val = formatter.format(new Date());
                    }else{
                        Date d = sdf.parse(val);
                        //parse the date to match OpenMRS format
                        val = formatter.format(d);
                    }
                    item.put("value", val);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return fieldsArray;
    }

    private boolean isForeignIdPath(JSONObject item) throws Exception{
        return item.has("source") && item.getString("source").split("\\.").length > 2;  // e.g ibu.anak.id
    }

    public String retrieveValueForLinkedRecord(String link, JSONObject entityJson) {
        try {
            String entityRelationships = readFileFromAssetsFolder("www/form/entity_relationship.json");
            JSONArray json = new JSONArray(entityRelationships);
            Log.logInfo(json.toString());

            JSONObject rJson = null;
            if ((rJson = retrieveRelationshipJsonForLink(link, json)) != null) {
                String[] path = link.split("\\.");
                String parentTable = path[0];
                String childTable = path[1];

                String joinValueKey = parentTable.equals(rJson.getString("parent")) ? rJson.getString("from") : rJson.getString("to");
                joinValueKey = joinValueKey.contains(".") ? joinValueKey.substring(joinValueKey.lastIndexOf(".") + 1) : joinValueKey;

                String val = entityJson.getString(joinValueKey);

                String joinField = parentTable.equals(rJson.getString("parent")) ? rJson.getString("to") : rJson.getString("from");
                String sql = "select * from " + childTable + " where " + joinField + "='" + val + "'";
                Log.logInfo(sql);
                String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql);
                JSONObject linkedEntityJson = new JSONObject();
                if (dbEntity != null && !dbEntity.isEmpty()){
                    linkedEntityJson = new JSONObject(dbEntity);
                }

                //finally retrieve the value from the child entity, need to improve or remove entirely these hacks
                String sourceKey = link.substring(link.lastIndexOf(".") + 1);
                if (linkedEntityJson.has(sourceKey)){
                    return linkedEntityJson.getString(sourceKey);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject retrieveRelationshipJsonForLink(String link,JSONArray array) throws Exception{
        for(int i = 0; i < array.length(); i++){
            JSONObject object = array.getJSONObject(i);
            if (relationShipExist(link, object)) {
                System.out.println("Relationship found ##");
                return object;
            }
        }
        return null;
    }

    private static boolean relationShipExist(String link, JSONObject json){
        try {
            String[] path = link.split("\\.");
            String parentTable = path[0];
            String childTable = path[1];

            String jsonParentTableString = json.getString("parent");
            String jsonChildTableString = json.getString("child");

            boolean parentToChildExist = jsonParentTableString.equals(parentTable) && jsonChildTableString.equals(childTable);
            boolean childToParentExist = jsonParentTableString.equals(childTable) && jsonChildTableString.equals(parentTable);

            if ( parentToChildExist || childToParentExist) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONArray getFieldsArrayForSubFormDefinition(JSONObject fieldsDefinition) throws  Exception{
        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");
        String bindPath = fieldsDefinition.getString("bind_type");

        JSONArray subFormFieldsArray = new JSONArray();

        for (int i = 0; i < fieldsArray.length(); i++){
            JSONObject field = new JSONObject();
            JSONObject item = fieldsArray.getJSONObject(i);
            if (!item.has("name"))
                continue; // skip elements without name
            field.put("name", item.getString("name"));

            if (!item.has("source")){
                field.put("source", bindPath + "." +  item.getString("name"));
            }else{
                field.put("source", bindPath + "." +  item.getString("source"));
            }

            subFormFieldsArray.put(i, field);
        }

        return subFormFieldsArray;
    }

    public JSONObject getFieldValuesForSubFormDefinition(JSONObject fieldsDefinition,String relationalId, String entityId, JSONObject jsonObject, JSONObject overrides) throws  Exception{

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        JSONObject fieldsValues = new JSONObject();

        for (int i = 0; i < fieldsArray.length(); i++){
            JSONObject item = fieldsArray.getJSONObject(i);
            if (!item.has("name"))
                continue; // skip elements without name
            if (item.has("bind")){
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");

                //check if we need to override this val
                if (overrides.has(item.getString("name"))){
                    fieldsValues.put(item.getString("name"), overrides.getString(item.getString("name")));
                }
                else{
                    String value = getValueForPath(path, jsonObject);
                    fieldsValues.put(item.getString("name"), value);
                }
            }

            //TODO: generate the id for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase("id")){
                String id = entityId != null ? entityId : generateRandomUUIDString();
                fieldsValues.put(item.getString("name"), id);
            }

            //TODO: generate the relational for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase(relationalIdKey)){
                fieldsValues.put(item.getString("name"), relationalId);
            }
        }
        return fieldsValues;
    }

    public String getValueForPath(String[] path, JSONObject jsonObject) throws Exception{
        JSONObject object = jsonObject;
        String value = null;
        int i = 0;
        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject){
                    object = object.getJSONObject(path[i]);
                }
                else if (o instanceof JSONArray){
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }
            i++;
        }
        Object valueObject = object.has(path[i]) ? object.get(path[i]) : null;

        if (valueObject == null)
            return value;
        if(valueObject instanceof JSONObject && ((JSONObject) valueObject).has("content")){
            value = ((JSONObject) object.get(path[i])).getString("content");
        }
        else if(valueObject instanceof JSONArray){
            value = ((JSONArray)valueObject).get(0).toString();
        }
        else if(!(valueObject instanceof JSONObject) ){
            value = valueObject.toString();
        }
        return value;
    }

    private String readFileFromAssetsFolder(String fileName){
        String fileContents = null;
        try {
            InputStream is = mContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        //Log.d("File", fileContents);
        return fileContents;
    }

    public static int getIndexForFormName(String formName, String[] formNames){
        for (int i = 0; i < formNames.length; i++){
            if (formName.equalsIgnoreCase(formNames[i])){
                return i;
            }
        }
        return -1;
    }

}