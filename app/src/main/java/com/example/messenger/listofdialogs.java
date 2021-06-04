package com.example.messenger;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Comparator;


import static android.content.ContentValues.TAG;

public class listofdialogs extends ListActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth user = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_listofdialogs);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        refreshdialoguelist();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listofdialogs.this, myprofile.class);
                startActivity(intent);
            }
        });
        FloatingActionButton newDialogbutton = findViewById(R.id.newDialodButt);
        newDialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(listofdialogs.this, findnewdialogue.class);
                startActivity(intent);
            }
        });
        SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        mySwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshdialoguelist();
                        SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        ListView listView = (ListView) findViewById(android.R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("lol", "kek " +  listofdialogsclass.listofdialogs.get(0).getName());

                Messageobj selected = (Messageobj) parent.getAdapter().getItem(position);

                Intent intent = new Intent(listofdialogs.this, dialogue.class);
                intent.putExtra("name", selected.getName());
                intent.putExtra("dialogueUID", selected.getDialogueuid());
                intent.putExtra("UID", selected.getUid());
                startActivity(intent);

            }
        });




        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (! document.exists()) {
                        Intent intent = new Intent(listofdialogs.this, littleaboutyou.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(listofdialogs.this, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public void onResume() {
        refreshdialoguelist();
        super.onResume();
    }
    public class TimeSorter implements Comparator<Messageobj>
    {
        @Override
        public int compare(Messageobj o1, Messageobj o2) {
            return (int)(o2.getTime() - o1.getTime());
        }
    }
    public void refreshdialoguelist(){
        ArrayList<Messageobj> listofdialogs1 = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid()).collection("dialogs").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String[] message = {""};
                                final String[] name = {""};
                                final String[] UID = {""};
                                final String[] dialogueUID = {document.getId()};
                                final long[] timestamp = {0};
                                UID[0] = document.getData().get("oth_uid").toString();
                                db.collection("messages").document(document.getId()).collection("dialogue").orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document1 = task.getResult().getDocuments().get(0);
                                                    message[0] = document1.getData().get("message").toString();
                                                    timestamp[0] = document1.getTimestamp("timestamp").getSeconds();
                                                    db.collection("users").document(UID[0]).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            name[0] = document.getData().get("nickname").toString();
                                                                        } else {
                                                                            name[0] = "Profile deleted";
                                                                        }

                                                                        listofdialogs1.add(new Messageobj(name[0], message[0], UID[0], dialogueUID[0], timestamp[0]));
                                                                        listofdialogs1.sort(new TimeSorter());
                                                                        ListView listView = (ListView) findViewById(android.R.id.list);
                                                                        listView.setAdapter(new listofdialogsadapter(listofdialogs.this, listofdialogs1));
                                                                    } else {
                                                                        Toast.makeText(listofdialogs.this, "Something went wrong",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                } else {
                                                    Toast.makeText(listofdialogs.this, "Something went wrong",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        });


                            }


                        } else {
                            Toast.makeText(listofdialogs.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}