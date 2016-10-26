package com.postmark.java;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.*;

/**
 * Created by marcus on 2016-02-02.
 */
public class PostmarkTemplateMessage extends AbstractPostMarkMessage {
    @SkipMe
    private HashMap<String,HashMap<String,String>> modelParameters = new HashMap<>();

    @SerializedName("TemplateId")
    private int templateId;

    @SerializedName("TemplateModel")
    private JsonElement templateModel;

    public PostmarkTemplateMessage(int templateId, String fromAddress, String toAddress, String replyToAddress, String ccAddress, String tag, boolean tracking) {
        this(templateId,fromAddress,toAddress,replyToAddress,ccAddress,tag,null,tracking);
    }

    public PostmarkTemplateMessage(int templateId, String fromAddress, String toAddress, String replyToAddress, String ccAddress, String tag, List<NameValuePair> headers, boolean tracking) {
        this.templateId = templateId;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.replyToAddress = replyToAddress;
        this.ccAddress = ccAddress;
        this.tag = tag;
        this.headers = (headers == null) ? new ArrayList<NameValuePair>() : headers;
        this.tracking = tracking;
    }


    public void validate() throws PostmarkException {

        super.validate();

        templateModel = new JsonParser().parse(generateTempleteModel());

        if (this.templateModel == null) {
            throw new PostmarkException("You must specify a valid 'To' email address.");
        }

    }


    private String generateTempleteModel(){
        JsonObject templeteModel = new JsonObject();
        for(Map.Entry<String,HashMap<String,String>> entry:modelParameters.entrySet()){
            JsonObject object = new JsonObject();
            HashMap<String,String> map = entry.getValue();

            for(Map.Entry<String,String> innerEntry: map.entrySet()){
                String[] entryMap = innerEntry.getKey().split("\\.");
                if(entryMap.length > 1){
                    object.add(entryMap[0],createJsonObject(removeFirstInArray(entryMap),innerEntry.getValue()));
                }else{
                    object.addProperty(entryMap[0],innerEntry.getValue());
                }
            }

            templeteModel.add(entry.getKey(),object);
        }

        return templeteModel.toString();
    }

    private JsonObject createJsonObject(String[] entries,String value){
        String currentEntery = entries[0];
        JsonObject object = new JsonObject();
        entries =removeFirstInArray(entries);
        if(entries.length > 0){
            object.add(currentEntery,createJsonObject(entries,value));
        }else{
            object.addProperty(currentEntery,value);
        }
        return object;
    }

    private String[] removeFirstInArray(String[] array){
        String[] newArray = new String[array.length-1];
        for(int i =1; i < array.length; i++){
            newArray[i-1] = array[i];
        }
        return newArray;
    }

    public void addParameter(String parameter, String value){
        String[] map = parameter.split("\\.");
        String entryKey = map[0];
        HashMap<String,String> entry;

        if(modelParameters.containsKey(entryKey)){
            entry = modelParameters.get(entryKey);
        }else{
            entry = new HashMap<>();
        }

        StringBuilder innerEntry = new StringBuilder();

        for (int i= 1; i < map.length;i ++){
            if(i != map.length){
                innerEntry.append(map[i]+".");
            }
        }

        entry.put(innerEntry.toString(),value);

        modelParameters.put(entryKey,entry);
    }


    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
