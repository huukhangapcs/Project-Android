package com.example.photogallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int Initial_GridView_Width;
    private static final int PERMISSION_CODE = 1000;
    private GridView gridView;
    private GridViewArrayAdapter _adapter;
    private ArrayList<ImageItem> _images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        requestReadExternal();
        initComponents();
        updateGridView();
    }
    private void requestReadExternal() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                //do nothing
            }
        }
    }
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id  == R.id.zoomIn){
            zoomInImage();
        }
        else if (id == R.id.zoomOut){
            zoomOutImage();
        }
        else if (id == R.id.reload){
            reloadImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void zoomInImage() {
        int level =  convertDpToPixel(10, this);
        int size = gridView.getRequestedColumnWidth()+level;
        gridView.setColumnWidth(size);
        _adapter.notifyDataSetChanged();
    }

    private void zoomOutImage() {
        int level =  convertDpToPixel(10, this);
        int size = gridView.getRequestedColumnWidth()-level;
        if (size<20) size = 20;
       gridView.setColumnWidth(size);
        _adapter.notifyDataSetChanged();
    }
    public int convertDpToPixel(float dp, Context context){
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
    private void reloadImage() {
        updateGridView();
        gridView.setColumnWidth(Initial_GridView_Width);
        Toast.makeText(this, "Reloaded image successfully.",Toast.LENGTH_LONG).show();
    }

    private void updateGridView() {
        (new AsyncTaskLoadImage(this)).execute();
    }
    private void initComponents() {
        gridView = (GridView)findViewById(R.id.gridView_img);
        Initial_GridView_Width = gridView.getRequestedColumnWidth();
    }
    private class AsyncTaskLoadImage extends AsyncTask<Void, Void, List<ImageItem>> {
        private ProgressDialog _dialog;

        public AsyncTaskLoadImage(Activity activity) {
            _dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            _dialog.setTitle("Loading");
            _dialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected List<ImageItem> doInBackground(Void... voids) {
            ArrayList<ImageItem> results = new ArrayList<>();

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME
            };
            String sortOrder = MediaStore.Images.Media.DISPLAY_NAME + " ASC";

            try (Cursor cursor = getApplicationContext().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    //MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            )) {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    ImageItem item = new ImageItem(contentUri, name);
                    results.add(item);
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<ImageItem> imageItems) {
            _images = (ArrayList<ImageItem>) imageItems;
            _adapter = new GridViewArrayAdapter(MainActivity.this,
                    R.layout.grid_view, _images);
            gridView.setAdapter(_adapter);
            if (_dialog.isShowing()) {
                _dialog.dismiss();
            }
        }
    }
}