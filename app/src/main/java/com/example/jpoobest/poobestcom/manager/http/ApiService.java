package com.example.jpoobest.poobestcom.manager.http;

import dao.PhotoItemCollectionDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by j.poobest on 13/4/2560.
 */

public interface ApiService {
    @POST("list")
    Call<PhotoItemCollectionDao> loadPhotoList();

    @POST("list/after/{id}")
    Call<PhotoItemCollectionDao> loadPhotoListAfterId(@Path("id") int id);


    @POST("list/before/{id}")
    Call<PhotoItemCollectionDao>  loadPhotoListBeforeId(@Path("id") int id);
}
