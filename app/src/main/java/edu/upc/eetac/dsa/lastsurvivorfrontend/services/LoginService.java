package edu.upc.eetac.dsa.lastsurvivorfrontend.services;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LoginService {
    /*
    @GET("{location}/")
    Call<List<Player>> listTracks(@Path("location") String location);
    //Posts a new Track to the server using Track object inside the body
    @POST("tracks/")
    Call<Track> addTrack(@Body Track track);
    //Edits a existing Track to the server
    @PUT("tracks/")
    Call<Track> editTrack(@Body Track track);
    //Delete the selected Track given the Id
    @DELETE("tracks/{id}")
    Call<Track> deleteTrack(@Path("id") String id);
    */
}
