package com.lansachia.naughtyweatherapp;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    // member variables:
    private String mTemperatureFahr;
    private String mTemperature;
    private String mCity;
    private String mIconName;
    private int mCondition;
    private String mDescription;




    //WeatherDataModel from a JSON:
    public static WeatherDataModel dataModel(JSONObject jsonObject){

        //using try / catch in case of bad json

        try {
            WeatherDataModel weatherDataModel = new WeatherDataModel();

            //parsing json to member variables
            weatherDataModel.mCity = jsonObject.getString("name");

            weatherDataModel.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");

            weatherDataModel.mIconName = updateWeatherIcon(weatherDataModel.mCondition);

            weatherDataModel.mDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            //temperature in Degrees Celcius
            double celciusTemperature = (jsonObject.getJSONObject("main").getDouble("temp") - 273.15);

            //rounded value for temp in celcius
            int celciusTempRounded = (int)Math.rint(celciusTemperature);

            //making celcius temp rounded available in string format for textView
            weatherDataModel.mTemperature = Integer.toString(celciusTempRounded);

            //temperature in Degrees Fahrenheit
            double fahrenheightTemperature = (jsonObject.getJSONObject("main").getDouble("temp") - 273.15) * 9.0/5.0 + 32;

            //rounded value for temp in fahrenheit
            int FahrTempRounded = (int)Math.rint(fahrenheightTemperature);

            //making fahrenheit temp rounded available in string format for textView
            weatherDataModel.mTemperatureFahr = Integer.toString(FahrTempRounded);




            return weatherDataModel;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }


    //to get the weather image name from the condition:
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition <= 230) {
            return "tstorm1";
        } else if (condition >230  && condition < 300) {
            return "tstorm_w_heavy_rain";
        }else if (condition >= 300 && condition < 500) {
            return "light_rain";
        }
        else if (condition >= 500 && condition < 600) {
            return "shower";
        } else if (condition >= 600 && condition <= 700) {
            return "snow";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "clear_sky";
        } else if (condition >= 801 && condition <= 804) {
            return "cloud2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "cold";
        } else if (condition == 904) {
            return "sunny";
        }else if (condition == 905) {
            return "windy";
        }else if (condition == 906) {
            return "hail";
        }else if (condition >906 && condition <=956) {
            return "sunny";
        } else if (condition > 956 && condition <= 1000) {
            return "tstorm3";
        }

        return "na";
    }

    //getter methods for temperature, city, and icon name:

    public String getTemperatureFahr() {
        return mTemperatureFahr + " °F";
    }

    public String getTemperature() {
        return mTemperature + " °C";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }

    public String getDescription() {
        return mDescription;
    }
}
