package com.historygram.api.requests;

import com.qosquo.historygram.models.Media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * API Media Request to return List of Media objects
 */
public class MediaRequest extends ApiRequest<Media[]> {

    /**
     * Constructs user feed request
     */
    public MediaRequest() {
        super("/feed", Media[].class);
    }

    /**
     * Constructs user's media request
     * @param userId User ID
     */
    public MediaRequest(final int userId) {
        super("/users/" + userId + "/media", Media[].class);
    }

    /**
     * Constructs user's media request with specified fields to return
     *
     * Example:
     *  Media(id=5, mediaType='IMAGE', mediaUrl='example.com/picture.jpg'
     *
     * @param userId User ID
     * @param fields List of fields to return in Media
     */
    public MediaRequest(final int userId, List<String> fields) {
        super("/users/" + userId + "/media", Media[].class);
        this.addParam("fields", fields);
    }

}
