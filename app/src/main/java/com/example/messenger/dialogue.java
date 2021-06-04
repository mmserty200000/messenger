package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dialogue extends AppCompatActivity {
    private RecyclerView MessagesRecycler;
    private dialogueadapter MessagesAdapter;
    private ArrayList<Messageobj> listofmessages;
    boolean isLoading = false;
    int now_limit = 30;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String dialogueuid;
    private String name;
    private boolean no_upd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_dialogue);

        Intent intent = getIntent();
        String uid = user.getUid();
        dialogueuid = intent.getStringExtra("dialogueUID");

        name = intent.getStringExtra("name");

        listofmessages = new ArrayList<Messageobj>();
        db = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(name);
        ImageButton sendMessagebutton = findViewById(R.id.button_send);
        MessagesRecycler = (RecyclerView) findViewById(R.id.recycler_gchat);
        MessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        MessagesAdapter = new dialogueadapter(this, listofmessages, uid);
        MessagesRecycler.setAdapter(MessagesAdapter);
        MessagesRecycler.scrollToPosition(listofmessages.size() - 1);
        initScrollListener();
        sendMessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messageobj in = send_message(dialogueuid, user.getUid());
                if (in != null) {
                    listofmessages.add(in);
                }
                MessagesAdapter.notifyDataSetChanged();
            }
        });

        db.collection("messages").document(dialogueuid).collection("dialogue")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(now_limit).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }


                if (!snapshots.getMetadata().hasPendingWrites()) {
                    boolean first_refresh = false;
                    if (listofmessages.isEmpty()) {
                        first_refresh = true;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot document = dc.getDocument();
                        switch (dc.getType()) {
                            case ADDED:
                                listofmessages.add(new Messageobj(name, document.getData().get("message").toString(), document.getData().get("whoose").toString(), document.getTimestamp("timestamp").getSeconds()));
                                break;

                        }

                    }
                    if (first_refresh) {
                        Collections.reverse(listofmessages);
                        if(! (MessagesAdapter.getItemCount()< now_limit)){
                            listofmessages.add(0, new Messageobj("", "", "", -1));
                        }
                        else{
                            no_upd = true;
                        }

                    }
                    MessagesRecycler.scrollToPosition(listofmessages.size() - 1);
                    MessagesAdapter.notifyDataSetChanged();

                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
        private void initScrollListener() {
            MessagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (!isLoading) {
                        if (no_upd == false) {
                            if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                                //bottom of list!
                                load_next();
                                isLoading = true;
                            }
                        }
                    }
                }
            });
        }
    private void load_next() {
        int barrier = MessagesAdapter.getItemCount() + now_limit;
        db.collection("messages").document(dialogueuid).collection("dialogue")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(barrier).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listofmessages.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listofmessages.add(new Messageobj(name, document.getData().get("message").toString(), document.getData().get("whoose").toString(), document.getTimestamp("timestamp").getSeconds()));
                            }
                            Collections.reverse(listofmessages);
                            MessagesAdapter.notifyDataSetChanged();
                            if(! (MessagesAdapter.getItemCount() < barrier)){
                                listofmessages.add(0, new Messageobj("", "", "", -1));
                                MessagesAdapter.notifyDataSetChanged();
                            }
                            else{
                                no_upd = true;
                            }
                            MessagesRecycler.scrollToPosition(MessagesAdapter.getItemCount() - (barrier - now_limit) + 1);
                            isLoading = false;
                        } else {
                            Toast.makeText(dialogue.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public Messageobj send_message(String dialogueuid, String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText fieldmessage = findViewById(R.id.type_message);
        String message = fieldmessage.getText().toString();
        Messageobj out = null;
        if(! TextUtils.isEmpty(message)){
            Map<String, Object> message_out = new HashMap<>();
            message_out.put("message", message);
            message_out.put("whoose", uid);
            message_out.put("timestamp", FieldValue.serverTimestamp());
            db.collection("messages").document(dialogueuid).collection("dialogue").add(message_out);
            out = new Messageobj("", message, uid, System.currentTimeMillis()/1000);
            fieldmessage.getText().clear();
        }
        return out;
    }
}