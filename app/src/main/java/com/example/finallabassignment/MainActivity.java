package com.example.finallabassignment;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private final int REQUEST_CODE = 1;
    Marker homeMarker;
    Marker destMarker;
    LatLng favMarker;
    String address;
    private static  final String  TAG = "MainActivity";
    private static final int FAVOURITE_ACTIVITY_REQUEST_CODE = 0;
    boolean temp;
    //get User Location

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    // latiotude and longitude

    double latitude, longitude;
    double destlat, destlng;
    final int RADIUS = 1500;
    Button save;
    Geocoder geocode;
    List<Address> addresses;

    public static boolean directionRequested;

    DatabaseHelper mDataBase;

    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataBase = new DatabaseHelper(this);

        save = findViewById(R.id.saveButton);
        save.setVisibility(View.GONE);
        initMap();
        getUserLocation();

        if(!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;

            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;

            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private  void getUserLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }

    private void setHomeMarker(){

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;
                    if (homeMarker != null)
                        homeMarker.remove();
//                    mMap.clear(); //clear the old markers

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(new LatLng(userLocation.latitude, userLocation.longitude))
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    homeMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location")
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.location_icon)));

                }
            }
        };
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId){

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return  BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    private boolean checkPermission(){
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private  void  requestPermission(){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                destlat = latLng.latitude;
                destlng = latLng.longitude;

                //set marker

                setMarker(location);

                save.setVisibility(View.VISIBLE);


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getAddress();

                        addPlace();
                       // addPlaces(destlat,destlng);

                        Toast.makeText(MainActivity.this, "Place saved"+ address, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        mMap.setOnMarkerDragListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FAVOURITE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Get String data from Intent
                double lat = data.getExtras().getDouble("latitude");
                double lng = data.getExtras().getDouble("longitude");
                String address1 = data.getExtras().getString("address");
                String date = data.getExtras().getString("date");
                int id = data.getExtras().getInt("id");
                favMarker = new LatLng(lat, lng);
                Toast.makeText(MainActivity.this, "toast for homemarker" + lat + lng, Toast.LENGTH_SHORT).show();
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(favMarker)
                        .title("You are here " + date)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                        .draggable(true)

                );

                mMap.setOnMarkerDragListener(this);

//                if(mDataBase.updatePlace(id,address1,latitude, longitude, date)){
//
//                    Toast.makeText(MainActivity.this, "location updated", Toast.LENGTH_SHORT).show();
//                }


            }
        }
    }

    private void addPlace(){

            //getAddress();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String date = sdf.format(calendar.getTime());

            if(mDataBase.add(address, destlat, destlng, date))
                Toast.makeText(this, "Place added", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Place not added", Toast.LENGTH_SHORT).show();

    }

    private void getAddress() {

        String add = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(destlat, destlng, 1);
            if (address != null && address.size() > 0) {
                Log.i(TAG, "onLocationResult" + address.get(0));

                if (address.get(0).getSubLocality() != null) {
                    add += " " + address.get(0).getSubLocality();

                }
                if (address.get(0).getLocality() != null) {
                    add += " " + address.get(0).getLocality();

                }

                if (address.get(0).getCountryName() != null) {
                    add += " " + address.get(0).getCountryName();

                }


                if (address.get(0).getPostalCode() != null) {
                    add += " " + address.get(0).getPostalCode();

                }

                Toast.makeText(MainActivity.this, add+"hi there", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
        }
        address = add;


    }


    public  void setMarker(Location location){

        LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLatlng)
                .title("Destination")
                .snippet("you are going there")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        if(destMarker == null){

            destMarker = mMap.addMarker(options);

        }
        else{
            clearMap();
            destMarker = mMap.addMarker(options);
        }

    }

    private void clearMap(){


        if (destMarker != null){
            destMarker.remove();
            destMarker = null;
        }
   }
    public void btnClick(View view){

        Object[] dataTransfer;
        String url;

        switch (view.getId()){

            case R.id.btn_restaurant:
            case R.id.btn_pharmacy:
            case R.id.btn_library:
                //mMap.clear();
                //setHomeMarker();
                if (view.getId() == R.id.btn_restaurant)
                     url = getUrl(latitude, longitude, "restaurant");
                else if(view.getId() == R.id.btn_pharmacy)
                    url = getUrl(latitude, longitude, "pharmacy");
                else
                    url = getUrl(latitude, longitude, "libraries");

                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GetNearbyPlacesData getNearbyPlacesData1 = new GetNearbyPlacesData();
                // execute asynchronously
                getNearbyPlacesData1.execute(dataTransfer);
                Toast.makeText(this, "restaurants", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_clear:
                if(destMarker != null)
                destMarker.remove();
                mMap.clear();
                initMap();
                getUserLocation();

                if(!checkPermission())
                    requestPermission();
                else
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                break;
            case R.id.btn_distance:
            case R.id.btn_direction:
                url = getDirectionUrl();
                dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(destlat, destlng);

                GetDirectionData getDirectionData = new GetDirectionData();
                getDirectionData.execute(dataTransfer);

                if (view.getId() == R.id.btn_direction)
                    directionRequested = true;
                else
                    directionRequested = false;

                break;

            case R.id.btn_favourite:
                Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                startActivityForResult(intent, FAVOURITE_ACTIVITY_REQUEST_CODE);


                break;



        }


    }

    private String getDirectionUrl(){

        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+latitude+","+longitude);
        directionUrl.append("&destination="+destlat+","+destlng);
        directionUrl.append("&key="+getString(R.string.api_key_places));
        return directionUrl.toString();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&key=" + getString(R.string.api_key_places));
        Log.d("", "getUrl: "+googlePlaceUrl);
        return googlePlaceUrl.toString();

    }



    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {



        destlat = marker.getPosition().latitude;
        destlng = marker.getPosition().longitude;

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        System.out.println("MARKERS: "+ marker.getTitle());
        android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(this);
        ad.setMessage("You want to add this place as Favourite?");
        ad.setCancelable(true);
        final Marker mMarker = marker;
        ad.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        temp = true;
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        String date = simpleDateFormat.format(calendar.getTime());
                        if (temp && mDataBase.add(mMarker.getTitle(), mMarker.getPosition().latitude, mMarker.getPosition().longitude, date)) {
                            Toast.makeText(MainActivity.this, "Place added to Favourites", Toast.LENGTH_SHORT).show();
                            temp = false;

                        }
                        //Toast.makeText(MainActivity.this, "Place Added As Favourite!", Toast.LENGTH_SHORT).show();

                    }
                });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.app.AlertDialog alert1 = ad.create();
        alert1.show();
    }
}
