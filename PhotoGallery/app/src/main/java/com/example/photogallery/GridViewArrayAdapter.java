package com.example.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

public class GridViewArrayAdapter extends ArrayAdapter {
    private Context _context;
    private int _id;
    private List<ImageItem> _items;
    public GridViewArrayAdapter(@NonNull Context context, int resource,
                           @NonNull List objects) {
        super(context, resource, objects);

        _context = context;
        _id = resource;
        _items = objects;
    }

    @Override
    public int getCount() {
        return _items.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(_context);
            convertView =layoutInflater.inflate(_id, null, false);
        }
        ImageView imageView = convertView.findViewById(R.id.imageViewItem);
        ImageItem item = _items.get(position);
        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(_context.getContentResolver(),
                    item.getUri());
        }catch (IOException e){
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return convertView;
    }
}
