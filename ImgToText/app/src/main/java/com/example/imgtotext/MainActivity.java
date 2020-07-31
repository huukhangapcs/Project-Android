package com.example.imgtotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    ImageView imageView;
    private final int CAMERA_REQUEST = 1000;
    private final int STORAGE_REQUEST = 1001;
    private int IMAGE_PICK_GALLERY = 1002;
    private int IMAGE_PICK_CAMERA = 1003;

    String[] camera_req;
    String[] gallery_req;
    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.editText);
        imageView = (ImageView)findViewById(R.id.ImgView);
        camera_req = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        gallery_req = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle("Click + button to add image");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.AddImage)
        {
            showImageImportDialog();
        }
        else if (id == R.id.Setting)
        {
            Toast.makeText(this, "SETTINGS", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i ==0) // Camera option
                {
                    if (!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickCamera();
                    }
                }
                else if (i ==1) //Gallery option
                {
                    if (!checkStoragePermission())
                    {
                        requestStoragePermission();
                    }
                    else
                    {
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,gallery_req,STORAGE_REQUEST);
    }


    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result1;
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,camera_req, CAMERA_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMERA_REQUEST:
                if (grantResults.length>0) {
                    boolean camera_result = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean storage_result =  (grantResults[1] == PackageManager.PERMISSION_GRANTED);
                    if (camera_result && storage_result)
                    {
                        pickCamera();
                    }
                    else
                    {
                        Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            case STORAGE_REQUEST:
                boolean storage_result =  (grantResults[1] == PackageManager.PERMISSION_GRANTED);
                if (storage_result)
                {
                    pickCamera();
                }
                else
                {
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA) {
                CropImage.activity(image_uri)
                .setGuidelines(CropImageView.Guidelines.ON).start(this);

            } else if (requestCode == IMAGE_PICK_GALLERY) {
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                assert result != null;
                Uri result_uri = result.getUri();
                imageView.setImageURI(result_uri);
// text reg
                BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational())
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    // get text from string builder until there is no text
                    for (int i =0; i<items.size(); i++){
                        TextBlock my_item = items.get(i);
                        stringBuilder.append(my_item.getValue());
                        stringBuilder.append("\n");
                    }
                    editText.setText(stringBuilder.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_LONG).show();
            }
        }
    }
}