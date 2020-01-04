package com.historygram.api.requests;

import java.util.ArrayList;
import java.util.Set;

public class PublishRequest extends ApiRequest<Integer> {
    public PublishRequest(final int userId,
                          final String imageUrl,
                          final String caption,
                          final Integer article) {
        super("/users/" + userId + "/media", Integer.class);
        this.setMapping(Mapping.POST);
        this.addParam("imageUrl", imageUrl);
        this.addParam("caption", caption);
        if (article != null) {
            this.addParam("article", article);
        }
    }

    public PublishRequest(final int userId,
                          final String imageUrl,
                          final String caption,
                          final Long timestamp,
                          final Integer article) {
        super("/users/" + userId + "/media", Integer.class);
        this.setMapping(Mapping.POST);
        this.addParam("imageUrl", imageUrl);
        this.addParam("caption", caption);
        this.addParam("timestamp", timestamp);
        if (article != null) {
            this.addParam("article", article);
        }
    }

    public PublishRequest(final int userId, final Set<Integer> creationId) {
        super("/users/" + userId + "/publish", Integer.class);
        this.setMapping(Mapping.POST);
        this.addParam("creationId", creationId);
    }
}
