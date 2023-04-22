package com.example.proyecto1_das;

import static android.Manifest.permission.CAMERA;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto1_das.utils.FileUtils;
import com.example.proyecto1_das.utils.LocaleUtils;
import com.example.proyecto1_das.utils.ThemeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.taken_photo);

        if (ContextCompat.checkSelfPermission(this, CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{CAMERA}, 33);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            exID = bundle.getInt("exID");
        }

        imageCaptureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");

                Bitmap rescaledImage = adjustImageSize(bitmap);
                imageView.setImageBitmap(rescaledImage);
                String imageFileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, imageFileName, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String photo64 = Base64.encodeToString(byteArray,Base64.DEFAULT);

                FileUtils fileUtils = new FileUtils();
                String mail = fileUtils.readFile(this, "config.txt");

                String url = "http://192.168.1.150:5000/image/create";
                JSONObject requestBody = new JSONObject();

                try {
                    requestBody.put("ex_id", exID);
                    requestBody.put("image", photo64);
                    requestBody.put("mail", mail);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                        response -> {

                        }, error -> {
                    Log.e("PA", "onCreate: ", error);
                });

                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            }
        });

        FileUtils fileUtils = new FileUtils();
        String mail = fileUtils.readFile(this, "config.txt");

        String url = "http://192.168.1.150:5000/image";
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("ex_id", exID);
            requestBody.put("mail", mail);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (!response.get("result").equals("null")) {
                            String image64 = response.getString("result");
                            byte[] b = Base64.decode(image64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b,0, b.length);
                            Bitmap rescaledImage = adjustImageSize(bitmap);
                            imageView.setImageBitmap(rescaledImage);
                        } else {
                            takeAPhoto();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            Log.e("PA", "onCreate: ", error);
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

    private void takeAPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);
        }
    }

}