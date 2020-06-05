package edu.upc.eetac.dsa.lastsurvivorfrontend.services;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Forum;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Message;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ForumService {

    @POST("forum/createForum")
    Call<Forum> createForum(@Body Forum forum);
    @PUT("forum/updateForum")
    Call<Forum> updateForum(@Body Forum forum);
    @GET("forum/getForums")
    Call<List<Forum>> getForums();
    @POST("forum/addMessage")
    Call<Forum> addMessage(@Body Message message);
    @PUT("forum/getMessages")
    Call<List<Message>> getMessages(@Body Forum forum);

}
