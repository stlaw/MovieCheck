package com.entko.moviecheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyActivity extends Activity {

    private static final String ROTTEN_API_KEY = keys.getRottenKey();
    private static final String TMDB_API_KEY = keys.getTmdbKey();

    private static String rottenURL1 = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=";
    private static String rottenURL2 = "&page_limit=4&page=1&apikey=";
    private static String imdbURL1 = "http://www.omdbapi.com/?i=";
    private static String tmdbURL1 = "http://api.themoviedb.org/3/movie/";
    private static String tmdbURL2 = "?api_key=";
    private static String tmdbURL3 = "http://image.tmdb.org/t/p/w92";

    protected EditText entryEditText;
    protected Button enterButton;
    protected TextView rottenRatingTextView;
    protected TextView imdbRatingTextView;
    protected TextView metaRatingTextView;

    protected List<TextView> titlesTextViews;
    protected List<TextView> yearsTextViews;
    protected List<ImageButton> movieButtonsList;

    protected List<String> titlesList = new ArrayList<String>();
    protected List<String> yearsList = new ArrayList<String>();
    protected List<Bitmap> bitmapsList = new ArrayList<Bitmap>();
    protected List<Movie> movieArrayList = new ArrayList<Movie>();

    protected List<String> titles = new ArrayList<String>();
    protected List<String> years = new ArrayList<String>();
    protected List<String> posters = new ArrayList<String>();

    protected JSONObject moviesJSON;
    protected JSONObject ratingsJSON;
    protected JSONArray moviesArray;
    protected JSONObject postersJSON;
    protected JSONObject alternateJSON;

    protected TextView movieTitle0;
    protected TextView movieTitle1;
    protected TextView movieTitle2;
    protected TextView movieTitle3;
    protected TextView movieYear0;
    protected TextView movieYear1;
    protected TextView movieYear2;
    protected TextView movieYear3;
    protected TextView moviePlot;
    protected TextView movieTitleLarge;
    protected ImageView moviePoster;
    protected ImageView rottenFreshnessImage;
    protected ImageButton movieButton0;
    protected ImageButton movieButton1;
    protected ImageButton movieButton2;
    protected ImageButton movieButton3;
    protected LinearLayout resultsLayout;
    protected LinearLayout searchLayout;
    protected LinearLayout ratingsLayout;
    protected View ratingsView;
    protected View searchResultsView;
    protected String movieTitle;
    protected String rottenRating;
    protected String rottenURL;
    protected String imdbRating;
    protected String imdbURL;
    protected String metaRating;
    protected String rottenFreshness;
    protected String imdbID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initiateVars();
    }

    private void initiateVars() {
        entryEditText = (EditText) findViewById(R.id.entryEditText);
        enterButton = (Button) findViewById(R.id.enterButton);
        resultsLayout = (LinearLayout) findViewById(R.id.resultsLayout);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        ratingsLayout = (LinearLayout) findViewById(R.id.ratingsLayout);

        enterButton.setOnClickListener(enterButtonListener);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingsView = inflater.inflate(R.layout.ratings, null);
        searchResultsView = inflater.inflate(R.layout.search_results, null);

        rottenRatingTextView = (TextView) ratingsView.findViewById(R.id.rottenRatingTextView);
        imdbRatingTextView = (TextView) ratingsView.findViewById(R.id.imdbRatingTextView);
        metaRatingTextView = (TextView) ratingsView.findViewById(R.id.metaRatingTextView);

        movieTitle0 = (TextView) searchResultsView.findViewById(R.id.movieTitle0);
        movieTitle1 = (TextView) searchResultsView.findViewById(R.id.movieTitle1);
        movieTitle2 = (TextView) searchResultsView.findViewById(R.id.movieTitle2);
        movieTitle3 = (TextView) searchResultsView.findViewById(R.id.movieTitle3);

        titlesTextViews = new ArrayList<TextView>(Arrays.asList(movieTitle0, movieTitle1,
                movieTitle2, movieTitle3));

        movieYear0 = (TextView) searchResultsView.findViewById(R.id.movieYear0);
        movieYear1 = (TextView) searchResultsView.findViewById(R.id.movieYear1);
        movieYear2 = (TextView) searchResultsView.findViewById(R.id.movieYear2);
        movieYear3 = (TextView) searchResultsView.findViewById(R.id.movieYear3);

        yearsTextViews = new ArrayList<TextView>(Arrays.asList(movieYear0, movieYear1,
                movieYear2, movieYear3));

        movieButton0 = (ImageButton) searchResultsView.findViewById(R.id.movieButton0);
        movieButton1 = (ImageButton) searchResultsView.findViewById(R.id.movieButton1);
        movieButton2 = (ImageButton) searchResultsView.findViewById(R.id.movieButton2);
        movieButton3 = (ImageButton) searchResultsView.findViewById(R.id.movieButton3);

        movieButtonsList = new ArrayList<ImageButton>(Arrays.asList(movieButton0, movieButton1,
                movieButton2, movieButton3));

        for (ImageButton ib : movieButtonsList) {
            ib.setOnClickListener(movieButtonListener);
        }
        moviePoster = (ImageView) ratingsView.findViewById(R.id.moviePoster);
        movieTitleLarge = (TextView) ratingsView.findViewById(R.id.movieTitleLarge);
        moviePlot = (TextView) ratingsView.findViewById(R.id.moviePlot);

        rottenFreshnessImage = (ImageView) ratingsView.findViewById(R.id.rottenFreshnessImage);
    }

    private View.OnClickListener enterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearRatings();
            clearLists();
            searchLayout.removeAllViews();
            ratingsLayout.removeAllViews();

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            String query = entryEditText.getText().toString();
            try {
                movieTitle = URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (movieTitle.length() > 0) {
                rottenURL = rottenURL1 + movieTitle + rottenURL2 + ROTTEN_API_KEY;
                new RottenAsync().execute(rottenURL);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                builder.setTitle("Invalid entry.");
                builder.setPositiveButton(R.string.ok, null);
                builder.setMessage("Enter a movie");
                AlertDialog theAlertDialog = builder.create();
                theAlertDialog.show();
            }
        }
    };

    private View.OnClickListener movieButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clearRatings();
            ratingsLayout.removeAllViews();

            try {
                switch (view.getId()) {
                    case R.id.movieButton0:
                        moviesJSON = moviesArray.getJSONObject(0);
                        ratingsJSON = moviesJSON.getJSONObject("ratings");
                        rottenRating = ratingsJSON.getString("critics_score") + "%";
                        if (ratingsJSON.has("critics_rating")) {
                            rottenFreshness = ratingsJSON.getString("critics_rating");
                        } else {
                            rottenFreshness = "null";
                        }

                        alternateJSON = moviesJSON.getJSONObject("alternate_ids");
                        imdbID = "tt" + alternateJSON.getString("imdb");
                        break;
                    case R.id.movieButton1:
                        moviesJSON = moviesArray.getJSONObject(1);
                        ratingsJSON = moviesJSON.getJSONObject("ratings");
                        rottenRating = ratingsJSON.getString("critics_score") + "%";
                        if (ratingsJSON.has("critics_rating")) {
                            rottenFreshness = ratingsJSON.getString("critics_rating");
                        } else {
                            rottenFreshness = "null";
                        }

                        alternateJSON = moviesJSON.getJSONObject("alternate_ids");
                        imdbID = "tt" + alternateJSON.getString("imdb");
                        break;
                    case R.id.movieButton2:
                        moviesJSON = moviesArray.getJSONObject(2);
                        ratingsJSON = moviesJSON.getJSONObject("ratings");
                        rottenRating = ratingsJSON.getString("critics_score") + "%";
                        if (ratingsJSON.has("critics_rating")) {
                            rottenFreshness = ratingsJSON.getString("critics_rating");
                        } else {
                            rottenFreshness = "null";
                        }

                        alternateJSON = moviesJSON.getJSONObject("alternate_ids");
                        imdbID = "tt" + alternateJSON.getString("imdb");
                        break;
                    case R.id.movieButton3:
                        moviesJSON = moviesArray.getJSONObject(3);
                        ratingsJSON = moviesJSON.getJSONObject("ratings");
                        rottenRating = ratingsJSON.getString("critics_score") + "%";
                        if (ratingsJSON.has("critics_rating")) {
                            rottenFreshness = ratingsJSON.getString("critics_rating");
                        } else {
                            rottenFreshness = "null";
                        }

                        alternateJSON = moviesJSON.getJSONObject("alternate_ids");
                        imdbID = "tt" + alternateJSON.getString("imdb");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            imdbURL = imdbURL1 + imdbID;
            new ImdbAsync().execute(imdbURL);

            ratingsLayout.addView(ratingsView);
        }
    };

    private void clearRatings() {
        rottenRating = "N/A";
        imdbRating = "N/A";
        metaRating = "N/A";
        rottenFreshness = null;
        rottenRatingTextView.setText("");
        imdbRatingTextView.setText("");
        metaRatingTextView.setText("");

    }

    private void clearLists() {
        titles.clear();
        years.clear();
        posters.clear();
        titlesList.clear();
        yearsList.clear();
        bitmapsList.clear();
        movieArrayList.clear();

        for (int i = 0; i < movieButtonsList.size(); i++) {
            movieButtonsList.get(i).setImageResource(android.R.color.transparent);
            titlesTextViews.get(i).setText("");
            yearsTextViews.get(i).setText("");
        }
    }

    private class RottenAsync extends AsyncTask<String, String, String> {

        JSONObject jsonObject;
        String total;

        @Override
        protected String doInBackground(String... strings) {
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            InputStream in = null;
            String result = null;   //Holds all of the data
            HttpGet httpGet;
            httpGet = new HttpGet(strings[0]);

            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                in = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            getRottenResult(result);
            return result;
        }

        private void getRottenResult(String result) {
            try {
                jsonObject = new JSONObject(result);
                total = jsonObject.getString("total");

                if (total.equals("0")) {
                    rottenRating = "N/A";
                    imdbRating = "N/A";
                    metaRating = "N/A";
                } else if (total.equals("1")) {
                    moviesArray = jsonObject.getJSONArray("movies");
                    moviesJSON = moviesArray.getJSONObject(0);
                    ratingsJSON = moviesJSON.getJSONObject("ratings");
                    rottenRating = ratingsJSON.getString("critics_score") + "%";
                    if (ratingsJSON.has("critics_rating")) {
                        rottenFreshness = ratingsJSON.getString("critics_rating");
                    } else {
                        rottenFreshness = "null";
                    }

                    alternateJSON = moviesJSON.getJSONObject("alternate_ids");
                    imdbID = "tt" + alternateJSON.getString("imdb");

                    imdbURL = imdbURL1 + imdbID;
                    new ImdbAsync().execute(imdbURL);
                } else {

                    moviesArray = jsonObject.getJSONArray("movies");

                    selectMovie(moviesArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void selectMovie(JSONArray moviesArray) {
            for(int j = 0; j < moviesArray.length(); j++) {
                try {
                    moviesJSON = moviesArray.getJSONObject(j);
                    titles.add(moviesJSON.getString("title"));
                    years.add(moviesJSON.getString("year"));
                    postersJSON = moviesJSON.getJSONObject("posters");
                    posters.add(postersJSON.getString("thumbnail"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            setMovieObjects();
        }

        private void setMovieObjects() {
            String titleTemp;
            String yearTemp;
            String posterTemp;

            for (int i = 0; i < moviesArray.length(); i++) {
                titleTemp = titles.get(i);
                yearTemp = years.get(i);
                posterTemp = posters.get(i);
                movieArrayList.add(new Movie(titleTemp, yearTemp, posterTemp));

                Movie tempMovie = movieArrayList.get(i);

                bitmapsList.add(tempMovie.getPosterThumb());
                titlesList.add(tempMovie.getTitle());
                yearsList.add(tempMovie.getYear());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (total.compareTo("1") <= 0) {
                rottenRatingTextView.setText(rottenRating);
                imdbRatingTextView.setText(imdbRating);
                metaRatingTextView.setText(metaRating);
            } else {

                for (int i = 0; i < titlesList.size(); i++) {
                    movieButtonsList.get(i).setImageBitmap(bitmapsList.get(i));
                    titlesTextViews.get(i).setText(titlesList.get(i));
                    yearsTextViews.get(i).setText(yearsList.get(i));
                }

                searchLayout.removeAllViews();
                searchLayout.addView(searchResultsView);
            }
        }
    }

    private class ImdbAsync extends AsyncTask<String, String, String> {

        JSONObject imdbJSONObject;
        JSONObject tmdbJSONObject;
        DefaultHttpClient httpClient;
        InputStream in = null;
        Bitmap poster;
        String result = null;   //Holds all of the data
        String imdbId;
        String imagePath;
        String title;
        String plot;

        @Override
        protected String doInBackground(String... strings) {
            httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(strings[0]);

            retrieveJSONData(httpGet);

            getImdbResult(result);

            String tmdbURL = tmdbURL1 + imdbId + tmdbURL2 + TMDB_API_KEY;

            httpGet = new HttpGet(tmdbURL);
            retrieveJSONData(httpGet);

            getPoster(result);
            return result;
        }

        private void retrieveJSONData(HttpGet httpGet) {
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                in = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void getImdbResult(String result) {
            try {
                imdbJSONObject = new JSONObject(result);

                if (imdbJSONObject.getString("Response").equals("False")) {
                    imdbRating = "N/A";
                    metaRating = "N/A";
                } else {
                    imdbRating = imdbJSONObject.getString("imdbRating") + "/10";
                    metaRating = imdbJSONObject.getString("Metascore");
                    imdbId = imdbJSONObject.getString("imdbID");
                    title = imdbJSONObject.getString("Title");
                    plot = imdbJSONObject.getString("Plot");
                    Log.v("IMDB", imdbRating);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void getPoster(String result) {
            try {
                tmdbJSONObject = new JSONObject(result);
                imagePath = tmdbJSONObject.getString("poster_path");
                poster = getImage(tmdbURL3+imagePath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        @Override
        protected void onPostExecute(String s) {
            rottenRatingTextView.setText(rottenRating);
            imdbRatingTextView.setText(imdbRating);
            metaRatingTextView.setText(metaRating);
            moviePoster.setImageBitmap(poster);
            movieTitleLarge.setText(title);
            moviePlot.setText(plot);

            if (rottenFreshness != null) {
                if (rottenFreshness.equals("Certified Fresh")) {
                    Drawable myDrawable = getResources().getDrawable(R.drawable.certified_fresh);
                    rottenFreshnessImage.setImageDrawable(myDrawable);
                } else if (rottenFreshness.equals("Fresh")) {
                    Drawable myDrawable = getResources().getDrawable(R.drawable.tomato);
                    rottenFreshnessImage.setImageDrawable(myDrawable);
                } else if (rottenFreshness.equals("null")) {
                    rottenFreshnessImage.setImageResource(android.R.color.transparent);
                } else {
                    Drawable myDrawable = getResources().getDrawable(R.drawable.splat);
                    rottenFreshnessImage.setImageDrawable(myDrawable);
                }
            }

            ratingsLayout.removeAllViews();
            ratingsLayout.addView(ratingsView);
        }
    }
}
