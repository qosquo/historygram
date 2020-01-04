package com.historygram.api.utils;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JsonUtils {

    public static boolean containsElement(String jsonString, String name) throws IOException {
        return containsElementImpl(jsonString, name);
    }

    private static boolean containsElementImpl(String jsonString, String name) throws IOException {
        if (jsonString.isEmpty()) {
            return false;
        }
        JsonReader reader = new JsonReader(new StringReader(jsonString));
        if (reader.hasNext() && reader.peek() == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
        } else {
            return false;
        }

        while (reader.hasNext()) {
            JsonToken nextToken = reader.peek();
            if (nextToken == JsonToken.END_DOCUMENT) {
                break;
            } else if (nextToken != JsonToken.NAME) {
                reader.skipValue();
            } else if (name.equals(reader.nextName())) {
                return true;
            }
        }

        return false;
    }

}
