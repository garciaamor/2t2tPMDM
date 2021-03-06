package com.example.jgarciaamor.a2t2p;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    public static final int LOCATION_REQUEST_CODE = 1;
    public static double lat1, lng1;
    public static double lat2 = 42.23701647941035;
    public static double lng2 = -8.71255248785019;
    public static Marker marcaT;
    private GoogleApiClient apiClient;
    private static final String LOGTAG = "android-localizacion";



    LatLng center = new LatLng(42.23701647941035, -8.71255248785019);
    //El radio del circulo de cercania está fijado en 100 metros y las coordenadas del centro son las indicadas arriba
    int radius = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(this);

        LatLng tesoro = new LatLng(lat2, lng2);

        marcaT = mMap.addMarker(new MarkerOptions().position(tesoro).title("Tesoro").snippet("Marca Tesoro"));
        //Añadir la marca de la posicion del tesoro
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tesoro));
        marcaT.setVisible(false);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }


        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));

        Circle circle = mMap.addCircle(circleOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        updateUI(lastLocation);
        calcularDistancia();



    }

    public void calcularDistancia() {


        double earthRadius = 6372.795477598;

        double dLat = Math.toRadians(lat1-lat2);
        double dLng = Math.toRadians(lng1-lng2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lat1)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        double distMet=dist*1000;
        String distancia=String.valueOf(distMet);
        //Es metodo calcula la distancia a la marca que tenemos que encontrar

        Toast.makeText(this, distancia+" metros ", Toast.LENGTH_LONG).show();


        //Si la distancia entre nuestra posición y la marca del tesoro es menor de 20 metros, dicha marca será visible
        if(distMet<=20){
            marcaT.setVisible(true);
        }else {
            //En el caso de que la distancia sea mayor de 20 metros, la marca permanecerá oculta.
            marcaT.setVisible(false);
        }

    }

    private void updateUI(Location loc) {

        if (loc != null) {
            lat1=loc.getLatitude();
            lng1=loc.getLongitude();
            //Identifica nuestra posicion y la actualiza constantemente
        } else {
            //Si no es capaz de identificar nuestra posicion, nos muestra un toast que indica que no es capaz de encontrar nuestra posicion
            Toast.makeText(this, "Latitud y Longitud no encontradas", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    //Conexion a GooglePlayServices
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Conexion a GooglePlayServicesSuspendida

        Log.e(LOGTAG, "Se ha interrumpido la conexión con GooglePlayServices");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Se intentó conectar a GooglePlayServices y no fue capaz de completarla

        Log.e(LOGTAG, "No se ha podido realizar la conexión con GooglePlayServices");
    }
}