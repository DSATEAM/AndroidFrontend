package edu.upc.eetac.dsa.lastsurvivorfrontend.services;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Item;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface StoreService {
    @GET("inventory/getItems")
    Call<List<Item>> itemList();
    @POST("inventory/addItem")
    Call<Item> addItem(@Body Item item);
}
