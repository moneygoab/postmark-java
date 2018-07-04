package com.postmark.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marcus on 2016-02-02.
 */
public class PostmarkTemplateMessage extends AbstractPostMarkMessage {

    @SkipMe
    private HashMap<String, Object> modelParameters = new HashMap<>();

    @SerializedName("TemplateId")
    private int templateId;

    @SerializedName("TemplateModel")
    private JsonElement templateModel;

    public PostmarkTemplateMessage(int templateId, String fromAddress, String toAddress, String replyToAddress, String ccAddress, String tag, boolean tracking) {
        this(templateId, fromAddress, toAddress, replyToAddress, ccAddress, tag, null, tracking);
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

    private String generateTempleteModel() {
        JsonObject entryObject = new JsonObject();
        generateDataForLevel(entryObject, modelParameters);
        return entryObject.toString();
    }

    private void generateDataForLevel(JsonObject topObject, HashMap<String, Object> objects) {
        HashMap<String, HashMap<String, Object>> tree = new HashMap<>();

        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            String[] map = entry.getKey().split("\\.");

            if (map.length > 1) {

                if (!tree.containsKey(map[0])) {
                    tree.put(map[0], new HashMap<String, Object>());
                }

                tree.get(map[0]).put(reformKey(map), entry.getValue());

            } else {
                if (entry.getValue() instanceof String) {
                    topObject.addProperty(map[0], (String) entry.getValue());
                } else {
                    topObject.add(map[0], (JsonArray) entry.getValue());
                }
            }
        }

        for (Map.Entry<String, HashMap<String, Object>> entry : tree.entrySet()) {

            if (!topObject.has(entry.getKey())) {
                topObject.add(entry.getKey(), new JsonObject());
            }
            generateDataForLevel(topObject.getAsJsonObject(entry.getKey()), entry.getValue());
        }
    }

    private String reformKey(String[] array) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < array.length; i++) {
            builder.append(array[i]);

            if ((i + 1 != array.length)) {
                builder.append(".");
            }
        }

        return builder.toString();
    }

    public void addParameter(String parameter, String value) {
        modelParameters.put(parameter, value);
    }

    public void addParameter(String parameter, JsonArray value) {
        modelParameters.put(parameter, value);
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
