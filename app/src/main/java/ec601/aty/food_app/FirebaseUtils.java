package ec601.aty.food_app;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FirebaseUtils
{
    private static final String POINT_DATA_NODE_PATH = "pointData";

    public static void pushPointData(String key, MapPoint value)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(POINT_DATA_NODE_PATH).child(key);
        ref.setValue(value);
    }

    public static void populateMapWithMapPointsFromGeofireKeys(List<String> keys)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(POINT_DATA_NODE_PATH);

        keys.forEach(key ->
                ref.child(key).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        displayMapPointOnMap(dataSnapshot.getValue(MapPoint.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                    }
                })
        );
    }

    private static void displayMapPointOnMap(MapPoint mapPoint)
    {
        MapUtils.addMarkerToMap(new MarkerOptions()
                .position(mapPoint.getCoordinates())
                .title("Expires at " + DateAndTimeUtils.getLocalFormattedDateFromUnixTime(mapPoint.getExpiryUnixTime()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }
}
