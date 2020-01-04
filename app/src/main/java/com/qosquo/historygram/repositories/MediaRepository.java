package com.qosquo.historygram.repositories;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.ApiBooleanRequest;
import com.historygram.api.requests.ApiRequest;
import com.historygram.api.requests.MediaRequest;
import com.qosquo.historygram.BuildConfig;
import com.qosquo.historygram.models.Article;
import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MediaRepository {
    private static final String TAG = "MediaRepository";

    private static final MediaRepository ourInstance = new MediaRepository();

    public static MediaRepository getInstance() {
        return ourInstance;
    }

    /**
     * Requests for User media
     *
     * @param userId  User ID
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getUserMedia(final int userId) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        Api.execute(new MediaRequest(userId), new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
//                System.out.println("Repository: " + Arrays.toString(result));
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for User media with specified fields
     *
     * @param userId  User ID
     * @param fields  Specified fields to return (for example: id, mediaUrl, etc), not null
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getUserMedia(final int userId, @NonNull List<String> fields) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        Api.execute(new MediaRequest(userId, fields), new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
//                System.out.println("Repository: " + Arrays.toString(result));
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for user's feed. Returns all user posts that the current user is subscribed to.
     *
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getFeed() {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        Api.execute(new MediaRequest(), new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
//                System.out.println("Repository: " + Arrays.toString(result));
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for top hashtags media
     *
     * @param hashtagId  Hashtag ID
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getHashtagTop(final int hashtagId) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        ApiRequest<Media[]> hashtagTopRequest = new ApiRequest<>("/hashtags/" + hashtagId + "/top", Media[].class);
        Api.execute(hashtagTopRequest, new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for top hashtags media with specified fields
     *
     * @param hashtagId  Hashtag ID
     * @param fields  Specified fields to get (for example: id, mediaUrl, etc), not null
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getHashtagTop(final int hashtagId, @NonNull final List<String> fields) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        ApiRequest<Media[]> hashtagTopRequest = new ApiRequest<>("/hashtags/" + hashtagId + "/top", Media[].class);
        hashtagTopRequest.addParam("fields", fields);

        Api.execute(hashtagTopRequest, new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for recent hashtags media
     *
     * @param hashtagId  Hashtag ID
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getHashtagRecent(final int hashtagId) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        ApiRequest<Media[]> hashtagRecemtRequest = new ApiRequest<>("/hashtags/" + hashtagId + "/recent", Media[].class);
        Api.execute(hashtagRecemtRequest, new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for recent hashtags media with specified fields
     *
     * @param hashtagId  Hashtag ID
     * @param fields  Specified fields to get (for example: id, mediaUrl, etc), not null
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getHashtagRecent(final int hashtagId, @NonNull final List<String> fields) {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        ApiRequest<Media[]> hashtagRecentRequest = new ApiRequest<>("/hashtags/" + hashtagId + "/recent", Media[].class);
        hashtagRecentRequest.addParam("fields", fields);

        Api.execute(hashtagRecentRequest, new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for all Media objects that exist in database
     *
     * @return MutableLiveData with List of Media objects {@link Media}
     * @see MutableLiveData
     */
    public MutableLiveData<List<Media>> getMedia() {
        MutableLiveData<List<Media>> data = new MutableLiveData<>();

        Api.execute(new ApiRequest<>("/media/", Media[].class), new ApiCallback<Media[]>() {
            @Override
            public void success(Media[] result) {
                data.setValue(Arrays.asList(result));
            }

            @Override
            public void fail(Exception error) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * Requests for the actual children of a Media object
     *
     * @param children  Set of children IDs, not null
     * @return MutableLiveData with List of String
     * @see MutableLiveData
     */
    public MutableLiveData<List<String>> getChildren(@NonNull final Set<Integer> children) {
        MutableLiveData<List<String>> data = new MutableLiveData<>();
        List<String> urls = new ArrayList<>();

        for (Integer child : children) {
            Api.execute(new ApiRequest<>("/media/" + child, Media.class), new ApiCallback<Media>() {
                @Override
                public void success(Media result) {
                    urls.add(result.getMediaUrl());
                    data.setValue(urls);
                }

                @Override
                public void fail(Exception error) {
                    error.printStackTrace();
                }
            });
        }

        return data;
    }

    /**
     * Sends a POST request to add like to an item
     *
     * @param ownerId  Item's owner ID
     * @param itemId  Item ID
     */
    public void like(final int ownerId, final int itemId) {
        ApiBooleanRequest request = new ApiBooleanRequest("/like");
        request.addParam("ownerId", ownerId);
        request.addParam("itemId", itemId);
        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "like media success");
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Sends a POST request to edit Media object
     *
     * @param id  Media ID
     * @param caption  New caption for the Media, not null
     */
    public void edit(int id, @NonNull String caption) {
        ApiBooleanRequest request = new ApiBooleanRequest("/media/" + id);
        request.addParam("caption", caption);
        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "edit media success");
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Sends a POST request to add an Article to a Media object
     *
     * @param id  Media ID
     * @param articleId  Article ID to add
     */
    public void addArticle(final int id, final int articleId) {
        ApiBooleanRequest request = new ApiBooleanRequest("/media/" + id + "/article");
        request.addParam("aid", articleId);
        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "add article success");
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Sends a DELETE request to remove an Article from a Media object
     *
     * @param id  Media ID
     */
    public void removeArticle(final int id) {
        ApiRequest<Integer> request = new ApiRequest<>("/media/" + id + "/article", Integer.class);
        request.setMapping(ApiRequest.Mapping.DELETE);
        Api.execute(request, new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                Log.d(TAG, "remove article success");
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * Sends a DELETE request to delete Media object from the database
     *
     * @param id  Media ID
     */
    public void delete(final int id) {
        ApiBooleanRequest request = new ApiBooleanRequest("/media/" + id);
        request.setMapping(ApiRequest.Mapping.DELETE);
        Api.execute(request, new ApiCallback<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.d(TAG, "delete media success");
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
            }
        });
    }

}
