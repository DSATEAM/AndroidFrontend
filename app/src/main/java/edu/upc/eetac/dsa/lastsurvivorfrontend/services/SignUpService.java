package edu.upc.eetac.dsa.lastsurvivorfrontend.services;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SignUpService {

    @GET("{location}/")
    Call<Player> checkPlayer(@Path("location") String location);
    //Posts a new Player (register)
    @POST("Players/")
    Call addPlayer(@Body Player player);

}
