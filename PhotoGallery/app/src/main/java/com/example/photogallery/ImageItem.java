package com.example.photogallery;

import android.net.Uri;

public class ImageItem {
    private Uri uri;
    private String title;

    public ImageItem(Uri uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
