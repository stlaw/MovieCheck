package com.entko.moviecheck;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Movie {
    private String title;
    private String year;
    private String poster;
    private Bitmap posterThumb;

    public Movie (String title, String year, String poster) {
        this.title = title;
        this.year = year;
        this.poster = poster;
        posterThumb = getImage(poster);
    }

    private Bitmap getImage(String url) {
        HttpURLConnection connection;
        InputStream in = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            in = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(in);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Bitmap getPosterThumb() {
        return posterThumb;
    }

    public void setPosterThumb(Bitmap posterThumb) {
        this.posterThumb = posterThumb;
    }
}
