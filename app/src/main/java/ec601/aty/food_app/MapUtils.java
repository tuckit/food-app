package ec601.aty.food_app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils
{
    private static GoogleMap mMap;
    // Maps Google Marker IDs to geofireKey <-> MapPoint
    private static Map<String, Map<String, MapPoint>> markerMap = new HashMap<>();

    public static void setMap(GoogleMap gMap)
    {
        mMap = gMap;
    }

    public static void addMarkerToMap(MarkerOptions markerOption, Map<String, MapPoint> geofireKeyToPointMap)
    {
        Marker newMarker = mMap.addMarker(markerOption);
        markerMap.put(newMarker.getId(), geofireKeyToPointMap);
    }

    public static void addMarkersToMap(List<MarkerOptions> markerOptions)
    {
        markerOptions.forEach(markerOption ->
                mMap.addMarker(markerOption)
        );
    }

    public static void clearMap()
    {
        mMap.clear();
        markerMap.clear();
    }

    public static void setUpHandlerForMarkerClicks(Context maps_activity, FirebaseAuth mAuth)
    {
        mMap.setOnInfoWindowClickListener(clickedMarker ->
        {
            // Handle what happens when a user clicks the info bubble of a marker. Below is how we extract the keys from the marker
            Map<String, MapPoint> geoFireKeyToMapPointPair = markerMap.get(clickedMarker.getId());

            final Dialog consumer_dialog = new Dialog(maps_activity);
            consumer_dialog.setContentView(R.layout.consume_dialog);
            consumer_dialog.setTitle("Food Consumption details");

            geoFireKeyToMapPointPair.forEach((geoFireKey, mapPoint) ->
            {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(FirebaseUtils.POINT_DATA_NODE_PATH);

                ref.child(geoFireKey).child("quantity").addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String point_quantity = dataSnapshot.getValue().toString();
                        TextView quantity_t_box = consumer_dialog.findViewById(R.id.quantity_text_box);
                        quantity_t_box.setText(point_quantity);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                    }
                });

                ref.child(geoFireKey).child("unit").addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String unit_quantity = dataSnapshot.getValue().toString();
                        TextView unit_t_box = consumer_dialog.findViewById(R.id.units_text_box_1);
                        unit_t_box.setText(unit_quantity);
                        unit_t_box = consumer_dialog.findViewById(R.id.units_text_box_2);
                        unit_t_box.setText(unit_quantity);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                    }
                });
            });
            Button consumerDialogButton = (Button) consumer_dialog.findViewById(R.id.reserve_food);
            // if button is clicked, close the custom dialog
            consumerDialogButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    geoFireKeyToMapPointPair.forEach((geoFireKey, mapPoint) ->
                    {
                        FirebaseUtils.consumeDialogPublish(maps_activity, consumer_dialog, geoFireKey,  mapPoint);
                        UserUtils.addConsumerAsInterestedInProducerFromPoint(geoFireKey, mapPoint.getPosterID(), mAuth);
                    });
                }
            });
            consumer_dialog.show();
        });
    }
}
