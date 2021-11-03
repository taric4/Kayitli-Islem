package com.tarikalperen.registertransaction;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.contentcapture.DataShareWriteAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.tarikalperen.registertransaction.databinding.ActivityDetailsBinding;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        database = this.openOrCreateDatabase("Register",MODE_PRIVATE,null);


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.equals("new")){

            binding.corporatetext.setText("");
            binding.agreementtext.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimage);
        } else {
            int RegisterId = intent.getIntExtra("RegisterId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try {

                Cursor cursor = database.rawQuery("SELECT * FROM register WHERE id= ?",new String[] {String.valueOf(RegisterId)});
                int RegistercorporateIx = cursor.getColumnIndex("corporate");
                int agreementIx = cursor.getColumnIndex("agreement");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.corporatetext.setText(cursor.getString(RegistercorporateIx));
                    binding.agreementtext.setText(cursor.getString(agreementIx));

                    byte [] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }

                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void save(View view) {

        String corporatetext = binding.corporatetext.getText().toString();
        String agreementtext = binding.agreementtext.getText().toString();

        Bitmap smallImage = makesmallerImage(selectedImage, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS register (id INTEGER PRIMARY KEY, corporate VARCHAR, agreement VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO register (corporate, agreement, image) VALUES (?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,corporatetext);
            sqLiteStatement.bindString(2,agreementtext);
            sqLiteStatement.bindBlob(3,byteArray);
            sqLiteStatement.execute();



        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public Bitmap makesmallerImage(Bitmap image, int maximumSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1){

            width = maximumSize;
            height = (int) (width / bitmapRatio);

        } else {

            height= maximumSize;
            width = (int) (height * bitmapRatio);

        }

        return image.createScaledBitmap (image,width,height, true);
    }

    public void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye gitmek için izniniz lazım",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        } else {

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }


    }

    private void registerLauncher(){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);

                        try {
                            if (Build.VERSION.SDK_INT >=28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(DetailsActivity.this.getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            } else  {
                                selectedImage = MediaStore.Images.Media.getBitmap(DetailsActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else {
                    Toast.makeText(DetailsActivity.this,"İzin Gerekli",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}