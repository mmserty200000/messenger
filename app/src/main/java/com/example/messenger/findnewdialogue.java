package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class findnewdialogue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findnewdialogue);

        ArrayList<Messageobj> listofdialogs = new ArrayList<>();
        /*
        long a = System.currentTimeMillis()/1000;
        listofdialogs.add(new Messageobj("Ivan", "hello", -1));
        listofdialogs.add(new Messageobj("Mike", "bye", -1));
        listofdialogs.add(new Messageobj("Mmserty", "hello bye goodbye how are you bye heheheh lol kek", -1));
        listofdialogs.add(new Messageobj("Men", "kek", -1));
        listofdialogs.add(new Messageobj("Woman", "lol", -1));
*/      FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new listofdialogsadapter(this, listofdialogs));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Messageobj selected = listofdialogs.get(position);


                Map<String, Object> data = new HashMap<>();
                data.put("users", Arrays.asList(user.getUid(), selected.getUid()));

                db.collection("messages")
                        .whereEqualTo("users", Arrays.asList(user.getUid(), selected.getUid()))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().isEmpty()){
                                        db.collection("messages")
                                                .add(data)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        String dialogue_uid = documentReference.getId();
                                                        Map<String, Object> datauser1 = new HashMap<>();
                                                        datauser1.put("oth_uid", selected.getUid());
                                                        db.collection("users").document(user.getUid()).collection("dialogs").document(dialogue_uid).set(datauser1);
                                                        Map<String, Object> datauser2 = new HashMap<>();
                                                        datauser2.put("oth_uid", user.getUid());
                                                        db.collection("users").document(selected.getUid()).collection("dialogs").document(dialogue_uid).set(datauser2);
                                                        Map<String, Object> firstmessage = new HashMap<>();

                                                        firstmessage.put("message", "Dialogue has begun");
                                                        firstmessage.put("timestamp",  FieldValue.serverTimestamp());
                                                        firstmessage.put("whoose", user.getUid());
                                                        db.collection("messages").document(dialogue_uid).collection("dialogue").add(firstmessage);
                                                        Intent intent = new Intent(findnewdialogue.this, listofdialogs.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(findnewdialogue.this, "Something went wrong",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    else{
                                        Toast.makeText(findnewdialogue.this, "You already have a dialogue with this person",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });




            }
        });

        SearchView searchview = findViewById(R.id.search);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                db.collection("users").whereEqualTo("nickname", newText).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                listofdialogs.clear();
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        listofdialogs.add(new Messageobj(document.getString("nickname"), document.getString("status"), document.getId(),  -1));
                                    }

                                } else {
                                    listofdialogs.add(new Messageobj("Not found", "", -1));

                                }
                            }
                        });
                return false;
            }
        });

    }

}
