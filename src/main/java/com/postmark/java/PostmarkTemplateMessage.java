package com.postmark.java;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by marcus on 2016-02-02.
 */
public class PostmarkTemplateMessage {
    @SkipMe
    private HashMap<String,String> modelParameters = new HashMap<>();
    @SkipMe
    private HashMap<String,HashMap<String,String>> modelInnerParameters = new HashMap<>();

    @SerializedName("TemplateId")
    private int templateId;

    @SerializedName("TemplateModel")
    private JsonElement templateModel;

    @SerializedName("InlineCss")
    private String InlineCss;

    @SerializedName("From")
    private String from;

    @SerializedName("To")
    private String to;

    @SerializedName("Cc")
    private String cc;

    @SerializedName("Bcc")
    private String bcc;

    @SerializedName("Tag")
    private String tag;

    @SerializedName("ReplyTo")
    private String replyTo;

    @SerializedName("Headers")
    private String headers;

    @SerializedName("TrackOpens")
    private boolean trackOpen;

    @SerializedName("Attachments")
    private String attachments;


    public PostmarkTemplateMessage(String to, String from, int templateId, boolean track){
        this.to = to;
        this.from = from;
        this.templateId = templateId;
        this.trackOpen = track;

    }

    public void clean() {
        this.from = this.from.trim();
        this.to = this.to.trim();
    }

    public void validate() throws PostmarkException {

        if ((this.from == null) || (this.from.equals(""))) {
            throw new PostmarkException("You must specify a valid 'From' email address.");
        }
        if ((this.to == null) || (this.to.equals(""))) {
            throw new PostmarkException("You must specify a valid 'To' email address.");
        }


        String test = generateTempleteModel();
        templateModel = new JsonParser().parse(test);

        if (this.templateModel == null) {
            throw new PostmarkException("You must specify a valid 'To' email address.");
        }



        // TODO: add more validation using regex
    }



    private String generateTempleteModel(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Iterator<Map.Entry<String,String>> set = modelParameters.entrySet().iterator();

        boolean loop = !modelParameters.isEmpty();

        while(loop){
            Map.Entry<String,String> entry = set.next();
            sb.append("'" + entry.getKey() + "': '"  + entry.getValue() +"'");
            if(set.hasNext()){
                sb.append(",");
            }else{
                loop = false;
            }
        }
        if(loop){
            sb.append(",");
        }

        loop = !modelInnerParameters.isEmpty();

        Iterator<Map.Entry<String,HashMap<String,String>>> innerSet = modelInnerParameters.entrySet().iterator();
        while(loop){
            Map.Entry<String,HashMap<String,String>> entry = innerSet.next();

            sb.append("'" + entry.getKey() + "': {" );

            boolean innerLoop= true;

            Iterator<Map.Entry<String,String>> values = entry.getValue().entrySet().iterator();

            while(innerLoop){

                Map.Entry<String,String> innerEntry = values.next();
                sb.append(" '" + innerEntry.getKey() + "': '"  + innerEntry.getValue() +"'");

                if(values.hasNext()){
                    sb.append(",");
                }else{
                    innerLoop = false;
                }
            }
            sb.append("}");
            if(innerSet.hasNext()){
                sb.append(",");
            }else{

                loop = false;
            }
        }


        sb.append("}");

        return sb.toString();
    }


    public void addInnerParameter(String outer, String parameter, String value){
    HashMap<String,String> map = modelInnerParameters.get(outer);
        if(map != null){
            map.put(parameter,value);
        }else{
            map = new HashMap<>();
            map.put(parameter,value);
            modelInnerParameters.put(outer,map);
        }
    }

    public void addParameter(String parameter, String value){
        modelParameters.put(parameter,value);
    }

}
