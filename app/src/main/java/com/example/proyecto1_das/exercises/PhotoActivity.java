package com.example.proyecto1_das.exercises;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.R;
import com.example.proyecto1_das.db.ExternalDB;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Activity that manages the photos
 */
public class PhotoActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imageCaptureLauncher;

    private ImageView imageView;

    private Integer exID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.initialize(getBaseContext());
        ThemeUtils.changeTheme(this);
        ThemeUtils.changeActionBar(this);
        ThemeUtils.setBackArrow(this);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.taken_photo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            exID = bundle.getInt("exID");
        }

        // Create the registerForActivityResult to get the data after taking the photo
        imageCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");

                // Rescale the image
                Bitmap rescaledImage = adjustImageSize(bitmap);
                imageView.setImageBitmap(rescaledImage);

                // Set a name to the photo and save it to internal storage
                String imageFileName =
                        "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss")
                                .format(new Date());
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                        imageFileName, null);

                // Transform the photo to a Base64 String and compress it
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                String photo64 = Base64.encodeToString(byteArray,Base64.DEFAULT);

                FileUtils fileUtils = new FileUtils();
                String mail = fileUtils.readFile(this, "config.txt");

                // HTTP request to save the photo to database
                String url = "http://" + ExternalDB.getIp() + ":5000/image/create";
                JSONObject requestBody = new JSONObject();

                try {
                    requestBody.put("ex_id", exID);
                    requestBody.put("image", photo64);
                    requestBody.put("mail", mail);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        url, requestBody, response -> {
                            Log.d("PA", "SUCCESS");
                        }, error -> {
                            Log.e("PA", "ERROR", error);
                        });

                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            }
        });

        // Retrieve the image from the database
        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(this, "config.txt");

        String url = "http://" + ExternalDB.getIp() + ":5000/image";
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("ex_id", exID);
            requestBody.put("mail", mail);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                requestBody, response -> {
                    try {
                        if (!response.get("result").equals("null")) {
                            // The photo exists
                            String image64 = response.getString("result");
                            byte[] b = Base64.decode(image64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,
                                    b.length);
                            Bitmap rescaledImage = adjustImageSize(bitmap);
                            imageView.setImageBitmap(rescaledImage);
                        } else {
                            // The photo doesn't exist
                            takeAPhoto();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            Log.e("PA", "ERROR", error);
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        imageView.setOnClickListener(c -> {
            takeAPhoto();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle b = new Bundle();
        b.putInt("exID", exID);
        outState.putBundle("exID", b);
    }

    // Adjust the image size to be bigger than the one taken
    private Bitmap adjustImageSize(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int length = bitmap.getHeight();

        int newSize = 800;
        float scaleWidth = ((float) newSize/width);
        float scaleLength = ((float) newSize/length);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleLength);

        return Bitmap.createBitmap(bitmap, 0,0, width, length, matrix, true);
    }

    // Starts the camera to take a photo
    private void takeAPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}