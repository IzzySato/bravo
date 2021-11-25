package ca.bcit.bravo;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MapsFragment extends Fragment {
    private GoogleMap googleMap;
    HashMap<LatLng, String> locations = new HashMap<>();
    ReentrantLock lock = new ReentrantLock();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        new GetDataJsonObjects().execute();
        View view = inflater.inflate(R.layout.fragment_maps, null, false);
        MapView mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                int counter = 0;
                while(lock.isLocked()){
                    counter++;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        for (Map.Entry<LatLng, String> entry : locations.entrySet()) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(entry.getKey())
                                    .title(entry.getValue())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fire)));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(100.0f));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(entry.getKey()));
                        }
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(51.58945, -120.87633) )
                                .zoom(7)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    if(counter>5){
                        Toast.makeText(getActivity(),getResources().getString(R.string.error_message),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private class GetDataJsonObjects extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
//            lock.lock();
            HttpHandler handler = new HttpHandler();
            String jsonStr = handler.makeServiceCall(getResources().getString(R.string.serviceURL));
            if (jsonStr != null) {
                try {
                    JSONObject fire = new JSONObject(jsonStr);
                    JSONArray events = fire.getJSONArray(getResources().getString(R.string.events));
                    for (int ndx = 0; ndx < events.length(); ndx++) {
                        JSONObject proObj = events.getJSONObject(ndx);
                        String proTitle = proObj.getString(getResources().getString(R.string.nasa_title));
                        if (proTitle.endsWith(getResources().getString(R.string.canada))) {
                            JSONArray categories = proObj.getJSONArray(getResources().getString(R.string.categories));
                            JSONArray geometries = proObj.getJSONArray(getResources().getString(R.string.geometries));
                            JSONObject geoProperty = geometries.getJSONObject(0);
                            JSONArray coordinates = geoProperty.getJSONArray(getResources().getString(R.string.coordinates));
                            double latitude = coordinates.getDouble(1);
                            double longitude = coordinates.getDouble(0);
                            JSONObject categoryProperty = categories.getJSONObject(0);
                            String title = categoryProperty.getString(getResources().getString(R.string.nasa_title));
                            if (title.equals(getResources().getString(R.string.wildfires))) {
                                String locationName = proTitle.substring(11, proTitle.length()-8);
                                locations.put(new LatLng(latitude, longitude), locationName);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            } else {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Button mapChangeBtn = (Button) getView().findViewById(R.id.btnChangeMapType);
            mapChangeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeMapType(view);
                }
            });

            Button zoomInBtn = (Button) getView().findViewById(R.id.btnZoomIn);
            zoomInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onZoom(view);
                }
            });

            Button zoomOutBtn = (Button) getView().findViewById(R.id.btnZoomOut);
            zoomOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onZoom(view);
                }
            });
        }

        public void changeMapType(View v) {
            if (googleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            else
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        public void onZoom(View v) {
            if (v.getId() == R.id.btnZoomIn)
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            else
                googleMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }
}