package edu.upc.eetac.dsa.lastsurvivorfrontend.services;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlayerService {

   /*@HTTP(method = "GET", path = "/access/signIn", hasBody = true)
    Call<Player> signIn(@Body Player player);*/
    @POST("access/signIn")
    Call<Player> signIn(@Body Player player);
    //Posts a new Player as String value to server and gets the result if the user was registered
    @POST("access/signUp/{username}/{password}")
    Call<Integer> signUp(@Path("username") String username , @Path("password") String password);
    //Edits a existing Track to the server
    @PUT("access/")
    Call<Player> updatePlayer(@Body Player player);
    //Delete the selected Track given the Id
    @DELETE("access/{playerId}")
    Call<Player> deletePlayer(@Path("playerId") String playerId);

}
