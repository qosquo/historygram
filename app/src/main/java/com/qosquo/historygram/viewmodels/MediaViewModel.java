package com.qosquo.historygram.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.qosquo.historygram.models.Media;
import com.qosquo.historygram.repositories.CollectionsRepository;
import com.qosquo.historygram.repositories.MediaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MediaViewModel extends ViewModel {
    private MutableLiveData<Boolean> reloadTrigger = new MutableLiveData<>();
    private LiveData<List<Media>> mMedia;
    private LiveData<List<String>> mChildren;
    private MediaRepository mRepo;

    /**
     * Initializes Media ViewModel. Breaks if Media is not null
     */
    public void init() {
        if (mMedia != null) {
            return;
        }

        refreshMedia();

        mRepo = MediaRepository.getInstance();
        //mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getMedia(context));
    }

    /**
     * Calls repository method to get User media objects. Updates Media LiveData
     * <p>
     * See {@link MediaRepository#getUserMedia(int) repository implementation}
     * </p>
     *
     * @param userId  User ID
     */
    public void userMedia(int userId) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getUserMedia(userId));
    }

    /**
     * Calls repository method to get User media objects with specified fields
     * <p>
     * See {@link MediaRepository#getUserMedia(int, List) repository implementation}
     * </p>
     *
     * @param userId  User ID
     * @param fields  Specified fields (for example: id, mediaUrl, etc), not null
     */
    public void userMedia(int userId, @NonNull List<String> fields) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getUserMedia(userId, fields));
    }

    /**
     * Updates Media LiveData with requested from repository Media objects
     * <p>
     * See {@link MediaRepository#getFeed() repository implementation}
     * </p>
     */
    public void feed() {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getFeed());
    }

    /**
     * Calls repository method to get hashtag's top media
     * <p>
     * See {@link MediaRepository#getHashtagTop(int) repository implementation}
     * </p>
     *
     * @param hashtagId Hashtag ID
     */
    public void hashtagTop(final int hashtagId) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getHashtagTop(hashtagId));
    }

    /**
     * Calls repository method to get hashtag's top media with specified fields
     * <p>
     * See {@link MediaRepository#getHashtagTop(int, List)} repository implementation}
     * </p>
     *
     * @param hashtagId  Hashtag ID
     * @param fields  Specified fields to get (for example: id, mediaUrl, etc), not null
     */
    public void hashtagTop(final int hashtagId, @NonNull final List<String> fields) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getHashtagTop(hashtagId, fields));
    }

    /**
     * Calls repository method to get hashtag's recent media
     * <p>
     * See {@link MediaRepository#getHashtagRecent(int) repository implementation}
     * </p>
     *
     * @param hashtagId Hashtag ID
     */
    public void hashtagRecent(final int hashtagId) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getHashtagRecent(hashtagId));
    }

    /**
     * Calls repository method to get hashtag's recent media with specified fields
     * <p>
     * See {@link MediaRepository#getHashtagTop(int, List)} repository implementation}
     * </p>
     *
     * @param hashtagId  Hashtag ID
     * @param fields  Specified fields to get (for example: id, mediaUrl, etc), not null
     */
    public void hashtagRecent(final int hashtagId, @NonNull final List<String> fields) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getHashtagRecent(hashtagId, fields));
    }

    /**
     * Calls repository method to get all Media objects existed in database
     * <p>
     * See {@link MediaRepository#getMedia() repository implementation}
     */
    public void media() {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input -> mRepo.getMedia());
    }

    public void collection(final int collectionId) {
        refreshMedia();
        mMedia = Transformations.switchMap(reloadTrigger, input ->
                CollectionsRepository.getInstance().getCollection(collectionId));
    }

    /**
     * Calls repository method to get all Media object's children
     * <p>
     * See {@link MediaRepository#getChildren(Set) repository implementation}
     * </p>
     *
     * @param children  Set of children IDs, not null
     */
    public void children(@NonNull final Set<Integer> children) {
        mChildren = mRepo.getChildren(children);
    }

    /**
     * See {@link MediaRepository#like(int, int) repository implementation}
     *
     * @param ownerId  Item object's owner ID
     * @param itemId  Item ID
     */
    public void like(final int ownerId, final int itemId) {
        mRepo.like(ownerId, itemId);
    }

    /**
     * Saves passed item to a collection.
     * If Collection ID is null, then it's added to a default collection.
     *
     * <p>
     * See {@link CollectionsRepository#addItem(Integer, Integer) repository implementation}
     *
     * @param collectionId  Collection ID, nullable
     * @param itemId  Item ID to add
     */
    public void save(@Nullable final Integer collectionId, final int itemId) {
        CollectionsRepository collectionsRepository = CollectionsRepository.getInstance();
        collectionsRepository.addItem(collectionId, itemId);
    }

    /**
     * Removes passed item from a collection
     * If Collection ID is null, then it's removes from all User's collections
     *
     * <p>
     * See {@link CollectionsRepository#removeItem(Integer, Integer) repository implementation}
     *
     * @param collectionId  Collection ID, nullable
     * @param itemId  Item ID to add
     */
    public void remove(@Nullable final Integer collectionId, final int itemId) {
        CollectionsRepository collectionsRepository = CollectionsRepository.getInstance();
        collectionsRepository.removeItem(collectionId, itemId);
    }

    /**
     * Calls repository request to edit Media object's caption
     * <p>
     * See {@link MediaRepository#edit(int, String) repository implementation}
     *
     * @param id Media ID
     * @param caption New caption for the Media, not null
     */
    public void edit(final int id, @NonNull final String caption) {
        refreshMedia();
        mRepo.edit(id, caption);
    }

    /**
     * Calls repository request to add an Article to a Media object
     * <p>
     * See {@link MediaRepository#addArticle(int, int) repository implementation}
     *
     * @param id Media ID
     * @param articleId Article ID to add
     */
    public void addArticle(final int id, final int articleId) {
        refreshMedia();
        mRepo.addArticle(id, articleId);
    }

    /**
     * Calls repository request to remove an Article from a Media object
     * <p>
     * See {@link MediaRepository#removeArticle(int) repository implementation}
     *
     * @param id Media ID
     */
    public void removeArticle(final int id) {
        refreshMedia();
        mRepo.removeArticle(id);
    }

    /**
     * Sends a DELETE request to delete Media object from the database
     * <p>
     * See {@link MediaRepository#delete(int) repository implementation}
     *
     * @param id Media ID
     */
    public void delete(final int id) {
        refreshMedia();
        mRepo.delete(id);
    }

    /**
     * Calls Media LiveData trigger to update Media objects
     */
    public void refreshMedia() {
        reloadTrigger.setValue(true);
    }

    public LiveData<List<Media>> getMedia() {
        return mMedia;
    }

    public LiveData<List<String>> getChildren() {
        return mChildren;
    }
}
