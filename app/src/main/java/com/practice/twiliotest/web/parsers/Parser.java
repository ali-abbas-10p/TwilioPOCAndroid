package com.practice.twiliotest.web.parsers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Parser {

    private Parser() {

    }

    public static Parser create(String json) {
        return new Gson().fromJson(json,Parser.class);
    }

    @SerializedName("meta-data")
    private MetaData metaData;

    private JsonElement data;

    public MetaData getMetaData() {
        return metaData;
    }

    public JsonElement getData() {
        return data;
    }

    public class MetaData {
        private int statusCode;
        private String message;

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }
}
