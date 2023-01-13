package com.example.assignmentweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    LocationManager lm;
    TextView adress;
    TextView temperature;
    TextView Min,Max,Feelslike;
    TextView wind_speed,humidity,wind_direction;
    Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings=findViewById(R.id.setting_button);

        adress = findViewById(R.id.adress);
        temperature = findViewById(R.id.temperature);
        Min=findViewById(R.id.min);
        Max=findViewById(R.id.max);
        Feelslike=findViewById(R.id.feelslike);
        wind_speed=findViewById(R.id.wind_speed);
        wind_direction=findViewById(R.id.wind_direction);
        humidity=findViewById(R.id.humidity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String value = prefs.getString("conversion_formate", null);

        if (value == null) {
            value = "celcius";
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
                finish();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        int p = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (p != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else {
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//            Toast.makeText(getApplicationContext(), "else started", Toast.LENGTH_LONG).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            //currentlocation.setText(lat + " , " + lng);

                            Geocoder g = new Geocoder(
                                    getApplicationContext(),
                                    Locale.getDefault());
                            try {
                                List<Address> add = g.getFromLocation(lat, lng, 1);
                                Address aa = add.get(0);
                                String wholeaddress = aa.getAddressLine(0);

                                adress.setText(wholeaddress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });

            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                Toast.makeText(getApplicationContext(), "Location not found.", Toast.LENGTH_LONG).show();
            } else {

                final double lat = loc.getLatitude();
                final double lng = loc.getLongitude();

                String link = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&appid=f95589db6ab132cb3c21fd7ff16babd5";

                URL url = null;

                try {
                    url = new URL(link);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String inputLine;
                    inputLine = in.readLine();

                    JSONObject object = new JSONObject(inputLine);
                    JSONObject main = object.getJSONObject("main");
                    JSONObject wind=object.getJSONObject("wind");
                    double kelvin = main.getDouble("temp");
                    double min=main.getDouble("temp_min");
                    double max=main.getDouble("temp_max");
                    double feels_like=main.getDouble("feels_like");
                    double humi=main.getDouble("humidity");

                    final double temp_celcius = kelvin - 273.15;
                    final double min_celcius=min-273.15;
                    final double max_celcius=max-273.15;
                    final double feelslike_celcius=feels_like-273.15;

                    final double speed=wind.getDouble("speed");
                    final double direc=wind.getDouble("deg");

                    final double temp_fahrenhite = (temp_celcius * (9 / 5) + 32);
                    final double min_fahrenhite = (min_celcius * (9 / 5) + 32);
                    final double max_fahrenheit = (max_celcius * (9 / 5) + 32);
                    final double feelslike_fahrenheit = (feelslike_celcius * (9 / 5) + 32);

                    if (value.equals("celcius")) {
                        temperature.setText(String.format("%.2f", temp_celcius) + "\u2103");
                        Min.setText(String.format("%.2f", min_celcius) + "\u2103");
                        Max.setText(String.format("%.2f", max_celcius) + "\u2103");
                        Feelslike.setText(String.format("%.2f", feelslike_celcius) + "\u2103");
                    }else{
                        temperature.setText(String.format("%.2f", temp_fahrenhite) + "\u2109");
                        Min.setText(String.format("%.2f", min_fahrenhite) + "\u2109");
                        Max.setText(String.format("%.2f", max_fahrenheit) + "\u2109");
                        Feelslike.setText(String.format("%.2f", feelslike_fahrenheit) + "\u2109");
                    }
                    humidity.setText(String.valueOf(humi));
                    wind_speed.setText(speed+" m/s");
                    wind_direction.setText(direc+ "\u00B0");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Thread t = new Thread(new Runnable() {
//                    public void run() {
//                        try {
//
//                              Toast.makeText(getApplicationContext(),"Thread started",Toast.LENGTH_LONG).show();
//                            URL u = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&appid=f95589db6ab132cb3c21fd7ff16babd5");
//                            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
//                            String inputLine;
//
//
//                            inputLine = in.readLine();
//                            System.out.println(inputLine);
//
//                           // Toast.makeText(getApplicationContext(), "Thread started", Toast.LENGTH_SHORT).show();
//
//
//                            JSONObject j = new JSONObject(inputLine);
//                            JSONObject k = j.getJSONObject("main");
//                            double kelvin = k.getDouble("temp");
//
//                            final double celcius = kelvin - 273.15;
//                            Toast.makeText(getApplicationContext(), String.valueOf(celcius), Toast.LENGTH_SHORT).show();
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    temperature.setText(String.valueOf(celcius));
//                                    // Toast.makeText(getApplicationContext(), String.valueOf(celcius), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                t.start();


            }


        }

    }
}