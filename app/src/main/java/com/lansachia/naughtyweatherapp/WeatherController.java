package com.lansachia.naughtyweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    //request code to check if user accepted permission to use location by location manager
    final int REQUEST_CODE = 1;

    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "9de57ba8ab41e41ce587294b456810b1";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // setting LOCATION_PROVIDER:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    TextView mTemperatureLabelFahr;
    TextView mWeatherDetails;

    //Declaring member variables for a LocationManager and a LocationListener:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.location_Text_View);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.temp_deg_Text_View);
        mTemperatureLabelFahr = (TextView) findViewById(R.id.temp_Fahr_Text_View);
        mWeatherDetails = (TextView) findViewById(R.id.weatherDetails);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        //OnClickListener to the changeCityButton:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changing views with intent(the current class for intent and the intended class to navigate to)
                Intent intent = new Intent(WeatherController.this, ChangeCityController.class);

                //firing the activity
                startActivity(intent);
            }
        });
    }


    // onResume() android Life cycle(executed after onCreate and just before user interaction with activity):
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NaughtyWeather", "onResume method Called");

        //retrieving intent from ChangeCityController by  getIntent
        Intent intent = getIntent();

        //retrieving extra by passing key of intent in ChangeCityController
        String cityName = intent.getStringExtra("cityNameExtra");

        //getting view displayed based on whether or not user entered city name

        if(cityName != null)
        {
            getWeatherForCityName(cityName);
        }else{
            Log.d("NaughtyWeather", "Getting weather for current location");

            //method returns weather for users current location
            getWeatherForCurrentLocation();
        }

    }


    //getWeatherForCityName(String city) to handle User String from ChangeCityController:
    private void getWeatherForCityName(String cityName)
    {
        //making API call to OpenWeatherMap by using city name and appId based on documentation from OpenWeather Map

        //to make http request (bundling all parameters together to query openWeatherMaps)
        //using RequestParams from external library
        RequestParams requestParams = new RequestParams();

        requestParams.put("q", cityName);
        requestParams.put("appid", APP_ID);

        //QUERY OPEN WEATHER BASED ON ABOVE PARAMS
        queryOpenWeatherMap(requestParams);
    }


    // method to  getWeatherForCurrentLocation() based on GPS coordinates:
    private void getWeatherForCurrentLocation() {

        //getting an instance of LocationManager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //checking and updates on device location
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("NaughtyWeather", "onLocationChanged method called");

                //extracting location data from location object when method is called
                //will be stored in string format
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("Naughty Weather", "Longitude is: " + longitude);
                Log.d("Naughty Weather", "Longitude is: " + latitude);

                //to make http request (bundling all parameters together to query openWeatherMaps)
                //using RequestParams from external library
                RequestParams requestParams = new RequestParams();

                //adding parameters with put method
                requestParams.put("lat", latitude);
                requestParams.put("lon", longitude);
                requestParams.put("appid", APP_ID);

                queryOpenWeatherMap(requestParams);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("NaughtyWeather", "onProviderDisabled method called");
            }
        };

        //request updates with location manager with Locationprovider, mintime between
        //updates and min distance between updates and LocationListener to be notified
        //permission check enabled for above line to work
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // CALLING ACTIVITY COMPAT TO REQUEST PERMISSION HERE..
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //requesting permission with this(for the current activity), permission being requested String[],
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    //checking if user granted permission for location manager
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //checking if request code in call back matches the code supplied in request(REQUEST_CODE)
        if (requestCode == REQUEST_CODE)
        {
            //checking if grantResults array has permission granted as 1st element (0 - index)
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Naughty Weather", "getWeatherForCurrentLocation Permission Granted!");

                //if permission is granted call getWeatherForCurrentLocation to request weather data
                getWeatherForCurrentLocation();
            }else {
                Log.d("Naughty Weather", "Permission Denied");
            }
        }
    }


    //Actual Networking call with method queryOpenWeatherMap(RequestParams params) here:

    private void queryOpenWeatherMap(RequestParams requestParams){
        //fetching data with special network object from library AsynHttpClient
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        //making get request asynchronously so app doesn't freeze to wait response
        //requires url, parameters, and response
        asyncHttpClient.get(WEATHER_URL, requestParams, new JsonHttpResponseHandler(){

            //JsonHttpClientHandler returns either of two responses, onSuccess or onFailure

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);

                //printing onSuccess message
                Log.d("Naughty Weather", "Successful: " + response.toString());

                WeatherDataModel weatherDataModel = WeatherDataModel.dataModel(response);

                //calling  updateUI to update UI based on json response
                updateUI(weatherDataModel);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);

                //getting the error when request fail in string
                Log.e("Naughty Weather", "Failed: " + throwable.toString());

                //status code for onFailure
                Log.d("Naughty Weather", "Status code: " + statusCode);

                //Toast Message for user to see failure message if request fails
                Toast.makeText(WeatherController.this, "Weather Request Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //updateUI():
    private void updateUI(WeatherDataModel weatherDataModel){
        //setting up view for Temp in Degree Celcius
        mTemperatureLabel.setText(weatherDataModel.getTemperature());

        //setting up view for temp in fahr
        mTemperatureLabelFahr.setText(weatherDataModel.getTemperatureFahr());

        mCityLabel.setText(weatherDataModel.getCity());

        //setting text for weatherDetails
        mWeatherDetails.setText(weatherDataModel.getDescription());

        //pulling images from resource and allocating to resource id to update
        //weather condition icons
        int resourceId = getResources().getIdentifier(weatherDataModel.getIconName(), "drawable", getPackageName());

        //setting image resource based on resourceId
        mWeatherImage.setImageResource(resourceId);




    }


    //onPause() to be used to free up resources consumed by locationManager:


    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationManager !=null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
