package com.example.android.quakereport;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;
import static com.example.android.quakereport.R.id.magnitude;

/**
 * Created by luisp on 21/06/2017.
 */

 /* Convert SAMPLE_JSON_RESPONSE String into a JSONObject
  * Extract “features” JSONArray
  * Loop through each feature in the array
  * Get earthquake JSONObject at position i
  * Get “properties” JSONObject
  * Extract “mag” for magnitude
  * Extract “place” for location
  * Extract “time” for time
  * Create Earthquake java object from magnitude, location, and time
  * Add earthquake to list of earthquakes
  */


/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    // TODO: Remove from file
    /** Sample JSON response for a USGS query */
    private static final String SAMPLE_JSON_RESPONSE = "";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     */
    public static Earthquake fetchEarthquakeData (String requestUrl) {
        //Create URL Object
        URL url = createUrl(requestUrl);

        // Perform the HTTP request to URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     *  Make a Http request to the given url and return a string as a reponse
     */
    private static String makeHttpRequest(String url) throws IOException {
        String jsonResponse = "";

        // If the URL is null then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000) /* milliseconds */  ;
            urlConnection.setConnectTimeout(15000); /* milliseconds */
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException) {
            Log.e(LOG_TAG, "Problem retreiving the earthquake JSON results");
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (urlConnection != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes() {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

            // STEP1: Convert SAMPLE_JSON_RESPONSE String into a JSONObject
            JSONObject baseJsonObject = new JSONObject(SAMPLE_JSON_RESPONSE);

            // STEP2: Extract “features” JSONArray
            JSONArray earthQuakeArray = baseJsonObject.getJSONArray("features");

            // STEP3: Loop through each feature in the array
            for (int i = 0; i < earthQuakeArray.length(); i++) {
                // STEP4: Get earthquake JSONObject at position i
                JSONObject currentEarthquake = earthQuakeArray.getJSONObject(i);
                // STEP5: Get “properties” JSONObject
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                // STEP6: Extract “mag” for magnitude
                double magnitude = properties.getDouble("mag");
                // STEP7: Extract “place” for location
                String location = properties.getString("place");
                // STEP8: Extract “time” for time
                long time = properties.getLong("time");
                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // STEP9 : Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}