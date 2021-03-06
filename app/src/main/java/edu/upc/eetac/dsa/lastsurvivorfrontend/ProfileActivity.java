package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;
import edu.upc.eetac.dsa.lastsurvivorfrontend.services.PlayerService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION.SDK_INT;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 4;
    ImageView imageView;
    private Context mContext;

    // RETROFIT OBJECT
    private static Retrofit retrofit;
    private static String retrofitIpAddress;
    //Player Service Object
    PlayerService playerService;
    Player player = new Player();
    private static int widthX = 200;
    private static int heightY = 200;
    private static int qualityImage = 10;
    Uri globalImageUri;
    TextView statsText;
    TextView usernameText;
    private ProgressBar pb_circular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pb_circular = findViewById(R.id.progressBar_cyclic);
        imageView = this.findViewById(R.id.avatarImg);
        statsText = this.findViewById(R.id.statsText);
        usernameText = this.findViewById(R.id.playerNameText);

        player = getIntent().getParcelableExtra("Player");
        mContext = this.getApplicationContext();
        //Read Data from player to set the avatar
        ResourceFileReader rs =  new ResourceFileReader();
        retrofitIpAddress = ResourceFileReader.ReadResourceFileFromStringNameKey("retrofit.IpAddress",this);
        startRetrofit();
        playerService = retrofit.create(PlayerService.class);
        //Bitmap myLogo = BitmapFactory.decodeResource(this.getResources(), R.drawable.userlogo);
        //myLogo = getResizedBitmap(myLogo, widthX, heightY);
        //String avat = imageToString(myLogo);
        imageView.setImageBitmap(StringToBitmap(player.getAvatar()));
        String intro = "Profile : " +player.getUsername();
        String stats = "Credits : " +player.getCredits()+ "\nExperience : "+player.getExperience() + "\nKills : "+ player.getKills()+"\nGamesPlayed : "+player.getGamesPlayed();
        statsText.setText(stats);
        usernameText.setText(intro);
        pb_circular.setVisibility(View.GONE);
    }

    private static void startRetrofit(){
        //HTTP &
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();

        // Running Retrofit to get result from Local tracks service Interface
        //Remember when using Local host on windows the IP is 10.0.2.2 for Android
        //Also added NullOnEmptyConverterFactory when the response from server is empty
        retrofit = new Retrofit.Builder()
                .baseUrl("http://"+retrofitIpAddress+":8080/Backend/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    public void onChangeAvatarClicked(View view){
        //Choose image from gallery
        //openGallery();
        selectImage(this);
    }
    public void onChangePasswordClicked(View view){
        changePasswordDialog();
    }
    private void changePasswordDialog(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_changepassword);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LinearLayout cancel= dialog.findViewById(R.id.cancel_btn);
        LinearLayout accept =dialog.findViewById(R.id.button_accept);
        TextView currentPass = dialog.findViewById(R.id.currPassText);
        TextView newPass = dialog.findViewById(R.id.commentText);
        TextView newPassRe = dialog.findViewById(R.id.commentText);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the password is correct and new password and retype password is same
                if(newPass.getText().toString().equals(newPassRe.getText().toString()) &&!newPass.getText().toString().contains(" ")){
                    //New pass and old pass is same
                    if(currentPass.getText().toString().equals(player.getPassword())){
                        player.setPassword( newPass.getText().toString());
                        dialog.dismiss();
                    }else{
                        //Incorrect Current Password NOTIFY
                        NotifyUser("Incorrect Current Password");
                    }
                }else{
                    NotifyUser("Please type new Password without spaces");
                }
            }
        });
        dialog.show();
    }
    private void exitDialog(){
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LinearLayout cancel= dialog.findViewById(R.id.cancelbtn);
        LinearLayout exit=dialog.findViewById(R.id.button_back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });
        dialog.show();
    }
    public void onCancelClicked(View view){
        exitDialog();
    }
    public void onAcceptClicked(View view){
        //Accept Changes and update player
        updatePlayer();
    }
    private void updatePlayer(){
        pb_circular.setVisibility(View.VISIBLE);
        try {
            Call<Player> playerTmp = playerService.updatePlayer(player);
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            playerTmp.enqueue(new Callback<Player>() {
                @Override
                public void onResponse(Call<Player> call, Response<Player> response) {
                    //Update Successful
                    pb_circular.setVisibility(View.GONE);
                    if (response.code() == 201) {
                        //Successful we can get the ID, and call again to ask for PLayer
                        if(response.isSuccessful()){
                            player =  response.body();
                            Log.w("Update Player" ,"Update Player Response successful"+ player.toString());
                            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Username",player.getUsername());
                            editor.putString("Password",player.getPassword());
                            editor.putString("Id",player.getId());
                            editor.apply();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("Player",player);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }else{ Log.e("ProfileActivity","Couldn't fill player from body");}
                    } else if(response.code() == 404){ // Not Found User
                        NotifyUser("Player Not Found");
                    }else if(response.code() == 400){ //Incorrect Password
                        NotifyUser("Bad Request");
                    }else{
                        NotifyUser("Something went horribly wrong!");
                    }
                }
                @Override
                public void onFailure(Call<Player> call, Throwable t) {
                    NotifyUser("Failure to Update Profile");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void NotifyUser(String showMessage){
        Toast toast = Toast.makeText(ProfileActivity.this,showMessage,Toast.LENGTH_SHORT);
        toast.show();
    }
    private void openGallery() {
        pb_circular.setVisibility(View.VISIBLE);
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // Sets the type as image/*. This ensures only components of type image are selected
        gallery.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,"image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        gallery.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        pb_circular.setVisibility(View.GONE);
    }
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo","Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);

            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Sets the type as image/*. This ensures only components of type image are selected
                pickPhoto.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                String[] mimeTypes = {"image/jpeg", "image/png","image/jpg"};
                pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);

            }else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        if (selectedImage != null) {
                            selectedImage = getResizedBitmap(selectedImage, widthX, heightY);
                            imageView.setImageBitmap(selectedImage);
                            //Picasso.with(mContext).load(imageUri).resize(160, 160).into(imageView);
                            Log.i("Camera Request", "The image was obtained correctly");
                            player.setAvatar(imageToString(selectedImage));
                        }
                    }

                    break;
                case GALLERY_REQUEST_CODE:
                    Uri imageUri;
                    if (resultCode == RESULT_OK && data != null) {
                        globalImageUri = data.getData();
                        String TAG = "Gallery Result";
                        Log.i("Gallery Result", "Image Uri: " + globalImageUri);

                        try {
                            if(globalImageUri!=null) {
                                if (checkPermissionREAD_EXTERNAL_STORAGE(this, globalImageUri)) {
                                    if (SDK_INT < 28) {

                                        Bitmap bitSource = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), globalImageUri), widthX, heightY);
                                        if(!bitSource.isRecycled()) {
                                            player.setAvatar(imageToString(bitSource));
                                            imageView.setImageBitmap(bitSource);
                                        }else{
                                            NotifyUser("Only JPEG!");
                                        }
                                    } else {

                                        Bitmap bitmap = getResizedBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), globalImageUri)), widthX, heightY);
                                        //bitmap.recycle();
                                        if(!bitmap.isRecycled()) {
                                            player.setAvatar(imageToString(bitmap));
                                            imageView.setImageBitmap(bitmap);
                                        }else{
                                            NotifyUser("Only JPEG!");
                                        }
                                    }
                                    //Bitmap source = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    //Picasso.with(mContext).load(imageUri).resize(160, 160).into(imageView);
                                    Log.i(TAG, "The image was obtained correctly");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do your stuff
                try {
                    if (SDK_INT < 28) {

                        Bitmap bitSource = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), globalImageUri), widthX, heightY);
                        if (!bitSource.isRecycled()) {
                            player.setAvatar(imageToString(bitSource));
                            imageView.setImageBitmap(bitSource);
                        } else {
                            NotifyUser("Only JPEG!");
                        }
                    } else {

                        Bitmap bitmap = getResizedBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), globalImageUri)), widthX, heightY);
                        //bitmap.recycle();
                        if (!bitmap.isRecycled()) {
                            player.setAvatar(imageToString(bitmap));
                            imageView.setImageBitmap(bitmap);
                        } else {
                            NotifyUser("Only JPEG!");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(ProfileActivity.this, "Cannot Change ProfilePic without Permission",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context,Uri ImageURI) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    private Bitmap StringToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,qualityImage,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
       return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
