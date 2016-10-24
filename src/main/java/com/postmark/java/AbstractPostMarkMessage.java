package com.postmark.java;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ling on 2016-10-24.
 */
abstract class AbstractPostMarkMessage{
    // The sender's email address.
    @SerializedName("From")
    protected String fromAddress;

    // The recipients's email address.
    @SerializedName("To")
    protected String toAddress;

    // The email address to reply to. This is optional.
    @SerializedName("ReplyTo")
    protected String replyToAddress;

    // The email address to carbon copy to. This is optional.
    @SerializedName("Cc")
    protected String ccAddress;

    // The email address to blind carbon copy to. This is optional.
    @SerializedName("Bcc")
    protected String bccAddress;

    // An optional tag than can be associated with the email.
    @SerializedName("Tag")
    protected String tag;

    //track email
    @SerializedName("TrackOpens")
    protected boolean tracking;

    // A collection of optional message headers.
    @SerializedName("Headers")
    protected List<NameValuePair> headers;

    @SerializedName("Attachments")
    protected List<Attachment> attachments;

    public void validate() throws PostmarkException {
        if ((this.fromAddress == null) || (this.fromAddress.equals(""))) {
            throw new PostmarkException("You must specify a valid 'From' email address.");
        }
        if ((this.toAddress == null) || (this.toAddress.equals(""))) {
            throw new PostmarkException("You must specify a valid 'To' email address.");
        }
    }

    public void clean() {
        this.fromAddress = this.fromAddress.trim();
        this.toAddress = this.toAddress.trim();

    }

}
