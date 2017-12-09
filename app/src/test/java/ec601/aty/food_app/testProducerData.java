package ec601.aty.food_app;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Dennis on 12/9/2017.
 */

public class testProducerData {
    public static final String PRODUCER_DATA_NODE_PATH = "producerDataTest";

    private static void testProducer(String dbName, String name){
        if (dbName.equals(name))
            System.out.println("Names match");
        else
            System.out.println("Names don't match");
    };

    private static void checkProducerPoint(String uid, String name){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(PRODUCER_DATA_NODE_PATH);

        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                testProducerData.testProducer(dataSnapshot.getValue().toString(), name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
