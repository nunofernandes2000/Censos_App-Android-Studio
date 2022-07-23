package com.estgp.map;

import static com.estgp.map.HouseForm.EXTRA_HOUSE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.estgp.map.Classe.House;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.estgp.map.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "MyTag";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap map;
    private ActivityMapsBinding binding;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private ActivityResultLauncher<Intent> MapsActivityResultLauncher;
    public Marker selectedMarker;
    public static final String EXTRA_HOUSE_MAIN = "extra_form_main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initFloatingActionButton();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPS();
        }


        MapsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){

                    @Override
                    public void onActivityResult(ActivityResult result) {


                        if (result.getResultCode() == RESULT_OK) {
                            Log.d("myTag","Activtiy Result OK");

                            Intent intent = result.getData();
                            House house = (House) result.getData().getSerializableExtra(HouseForm.EXTRA_HOUSE);
                            selectedMarker.setTag(house);


                            //Server para mudar a cor do marcador dependendo do seu estado
                            if(house.getDeliveryStatus() == false && house.getSubmitted() == false){
                                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            }else if(house.getDeliveryStatus() == true && house.getSubmitted() == false){
                                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                            }else if(house.getDeliveryStatus() == true && house.getSubmitted() == true){
                                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }

                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Log.d("myTag", "Activity Result CANCELLED");
                        }

                    }
                });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(MAP_TYPE_NORMAL);
        getLocationPermission();


        // Server para mover o marker de lugar
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                        @Override
                                        public void onMarkerDragStart(Marker marker) {

                                        }

                                        @Override
                                        public void onMarkerDrag(Marker marker) {

                                        }

                                        @Override
                                        public void onMarkerDragEnd(Marker marker) {

                                            // Atualiza a latitude e longitude ao arrastar o marker
                                            House house  = (House)marker.getTag();
                                            house.setLatitude(marker.getPosition().latitude);
                                            house.setLongitude(marker.getPosition().longitude);
                                        }
                                    }

        );


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {


                selectedMarker = marker; // guarda o marker selecionado
                House house = (House) marker.getTag(); // pega o objeto house do marker
                Log.d("myTag", "Marker clicked: " + house);



                //Criação do AlertDialog, com as opções Edit,Delete e Cancel
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Censos")
                        .setMessage(house.toString())
                        .setPositiveButton("Edit",new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                if (house.getHouseOwner() == null) {
                                } else if (house.getDateLimit() == null) {
                                    house.setDateLimit(LocalDate.parse("Sem informação"));
                                }
                                Intent intent = new Intent(getApplicationContext(),HouseForm.class);
                                intent.putExtra(EXTRA_HOUSE,house);
                                MapsActivityResultLauncher.launch(intent);



                            }

                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                selectedMarker.remove(); // remove marker
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Não faz nada
                                //saimos do dialog
                            }
                        })
                        .show(); // Server para mostrar o Dialog


                return false;
            }
        });
    }


    private void initFloatingActionButton() {

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {

            setMarkerOnLocation();

        });
    }

    // Prompt User for Location Permission
    //-------------------------------------------------------------------------------------------
    private void getLocationPermission(){

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    // Handle Result of Location Permission Request
    //-------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }

        }
    }


    // Asks user to enable Gps if it's off
    //-------------------------------------------------------------------------------------------
    private void enableGPS() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    // Places marker on last know location from fusedLocationProviderClient
    //----------------------------------------------------------------------------------------------
    private void setMarkerOnLocation(){
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(latLng)
                                        .title("Marker on your current position")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .draggable(true);
                                Marker marker = map.addMarker(markerOptions);
                                //Cria o objeto casa e adiciona valores a esse mesma casa, por defeito e depois associa a casa ao marcador
                                House house = new House(null,null,false,false,marker.getPosition().latitude, marker.getPosition().longitude);
                                marker.setTag(house);

                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            }
                            else{
                                locationRequest = LocationRequest.create();
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationRequest.setInterval(20 * 1000);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(@NonNull LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult == null){
                                            return;
                                        }
                                        setMarkerOnLocation();
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "An error has ocurred", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    //###############################Menu###############################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Server para mudar o tipo de vista(Vista Normal ou Vista Satélite)
        //Change View Type (Normal View or Satellite View)
        int id = item.getItemId();
        if (id == R.id.normal_view) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (id == R.id.satellite_view){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        return super.onOptionsItemSelected(item);
    }
}