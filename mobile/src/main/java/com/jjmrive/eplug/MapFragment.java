package com.jjmrive.eplug;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_REQUEST_CODE = 1;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private GoogleMap map;
    private LocationManager locationManager;

    private Location currentLocation;
    private LatLng currentCameraPos;
    private float currentCameraZoom;

    private boolean mapStart = true;

    private ArrayList<Marker> markersList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                this.getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onPause(){
        super.onPause();

        if (map != null) {
            SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mapStatePrefs.edit();
            editor.putFloat("lat", (float) currentCameraPos.latitude);
            editor.putFloat("long", (float) currentCameraPos.longitude);
            editor.putFloat("zoom", currentCameraZoom);
            editor.putFloat("tilt", map.getCameraPosition().tilt);
            editor.putFloat("bearing", map.getCameraPosition().bearing);
            editor.putBoolean("mapPaused", true);
            editor.apply();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        currentCameraPos = map.getCameraPosition().target;
        currentCameraZoom = map.getCameraPosition().zoom;

        if ((getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE) != null)){
            SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE);

            boolean paused = mapStatePrefs.getBoolean("mapPaused", false);

            if (paused) {
                float latitude = mapStatePrefs.getFloat("lat", 0);
                float longitude = mapStatePrefs.getFloat("long", 0);
                float zoom = mapStatePrefs.getFloat("zoom", 0);
                float tilt = mapStatePrefs.getFloat("tilt", 0);
                float bearing = mapStatePrefs.getFloat("bearing", 0);

                mapStatePrefs.edit().clear().apply();

                mapStart = false;
                CameraPosition savedPosition = new CameraPosition(new LatLng(latitude, longitude),
                        zoom, tilt, bearing);
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(savedPosition);
                map.moveCamera(update);
                currentCameraPos = savedPosition.target;
                currentCameraZoom = savedPosition.zoom;

                if (getActivity().getSharedPreferences("currentLocation", Context.MODE_PRIVATE) != null) {
                    SharedPreferences mapLocPrefs = getActivity().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);
                    currentLocation = new Location("");
                    currentLocation.setLatitude(mapLocPrefs.getFloat("latitude", 200));
                    currentLocation.setLongitude(mapLocPrefs.getFloat("longitude", 200));
                    setDistance();
                }
            }
        }

        if (mapStart){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.452090, -3.724779), 4));
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getActivity(), "ePlug needs ACCESS_FINE_LOCATION permission", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        map.setOnCameraMoveListener(this);
        map.setOnInfoWindowClickListener(this);

        loadPlugs();

        setVisibility();

    }

    public void loadPlugs(){
        ArrayList<Plug> plugsList = DataHolder.getPlugsList();

        for (int i = 0; i < plugsList.size(); i++){
            if (plugsList.get(i).isFree()){
                markersList.add(map.addMarker(new MarkerOptions()
                        .position(new LatLng(plugsList.get(i).getLatitude(), plugsList.get(i).getLongitude()))
                        .title(plugsList.get(i).getName())
                        .visible(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                );
            } else {
                markersList.add(map.addMarker(new MarkerOptions()
                        .position(new LatLng(plugsList.get(i).getLatitude(), plugsList.get(i).getLongitude()))
                        .title(plugsList.get(i).getName())
                        .visible(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                }
            } else {
                Toast.makeText(getActivity(), "Permission error", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (mapStart) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
            map.animateCamera(cameraUpdate);

            mapStart = false;
        }
        locationManager.removeUpdates(this);

        if (getActivity() != null) {
            if (getActivity().getSharedPreferences("currentLocation", Context.MODE_PRIVATE) != null) {

                SharedPreferences mapStatePrefs = getActivity().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);

                if (mapStatePrefs.getFloat("latitude", 200) == 200) {
                    SharedPreferences.Editor editor = mapStatePrefs.edit();
                    editor.putFloat("latitude", (float) currentLocation.getLatitude());
                    editor.putFloat("longitude", (float) currentLocation.getLongitude());
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = mapStatePrefs.edit();
                    editor.clear().apply();
                    editor.putFloat("latitude", (float) currentLocation.getLatitude());
                    editor.putFloat("longitude", (float) currentLocation.getLongitude());
                    editor.apply();
                }
            }
        }

        setDistance();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onCameraMove() {
        currentCameraPos = map.getCameraPosition().target;
        currentCameraZoom = map.getCameraPosition().zoom;

        setVisibility();
    }

    public void setVisibility(){
        for (int i = 0; i < markersList.size(); i++){
            if (currentCameraZoom >= 6){
                if ((abs(markersList.get(i).getPosition().latitude - currentCameraPos.latitude) < 20)
                        && (abs(markersList.get(i).getPosition().longitude - currentCameraPos.longitude) < 20)){
                    markersList.get(i).setVisible(true);
                } else {
                    markersList.get(i).setVisible(false);
                }
            } else {
                markersList.get(i).setVisible(false);
            }
        }
    }

    public void setDistance(){
        if (currentLocation != null) {
            for (int i = 0; i < markersList.size(); i++) {
                markersList.get(i).setSnippet(getString(R.string.distance_start) + " " +
                        String.format("%.3f", distance(markersList.get(i).getPosition().latitude, markersList.get(i).getPosition().longitude,
                                currentLocation.getLatitude(), currentLocation.getLongitude(), 'K')) + " " + getString(R.string.distance_end));
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }


    //This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ArrayList<Plug> plugs = DataHolder.getPlugsList();

        Plug plg = new Plug(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle(), null, null, false);

        if (plugs.contains(plg)){
            Plug pl = plugs.get(plugs.indexOf(plg));
            Double distance = distance(pl.getLatitude(), pl.getLongitude(),
                    currentLocation.getLatitude(), currentLocation.getLongitude(), 'K');
            InfoDialog.newInstance(pl.getName(), pl.getDescription(), distance, pl.getUrlPhoto(), pl.isFree()).show(getFragmentManager(), null);
        } else {
            marker.remove();
            setVisibility();
        }
    }
}
