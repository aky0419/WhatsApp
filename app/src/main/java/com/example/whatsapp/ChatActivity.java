package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private Button sendChatButton;
    private EditText messageEt;
    private ArrayList<String> myMessage = new ArrayList<>();
    private ArrayList<String> friendMessage = new ArrayList<>();
    private ArrayAdapter adapter;
    private String activeUsername = "";
    private DatabaseReference reference;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        activeUsername = intent.getStringExtra("username");

        setTitle("Chat with " + activeUsername);
        myMessage.clear();


        messageEt = findViewById(R.id.chatET);
        chatListView = findViewById(R.id.chatListView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, myMessage);
        chatListView.setAdapter(adapter);

        Query query = reference.child("users").child(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sender = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendChatButton = findViewById(R.id.sendChatButton);
        sendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> chatMap = new HashMap<>();
                chatMap.put("sender",  sender);
                chatMap.put("recipient", activeUsername);
                chatMap.put("message", messageEt.getText().toString());
                chatMap.put("userId", mAuth.getCurrentUser().getUid());
                chatMap.put("timeStamp", getTimeStamp());
                reference.child("message").push().setValue(chatMap);
                messageEt.setText("");

            }
        });

        FirebaseDatabase.getInstance().getReference().child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.child("recipient").getValue().toString().equals(activeUsername)
                        && dataSnapshot.child("sender").getValue().toString().equals(sender)) {
                    myMessage.add(dataSnapshot.child("message").getValue().toString());
                } else if (dataSnapshot.child("recipient").getValue().toString().equals(sender)
                        && dataSnapshot.child("sender").getValue().toString().equals(activeUsername)) {
                    myMessage.add("> " + dataSnapshot.child("message").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        return simpleDateFormat.format(new Date());

    }
}
