package com.arefin.jockeychat.storiesthing;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arefin.jockeychat.R;
import com.arefin.jockeychat.storiesthing.Utils.Photos;
import com.arefin.jockeychat.storiesthing.Utils.iFirebaseLoadDone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryMain extends AppCompatActivity implements iFirebaseLoadDone {

    StoriesProgressView storiesProgressView;
    ImageView imageView;
    Button btn_load, btn_pause, btn_resume, btn_reverse;
    int counter = 0;
    DatabaseReference databaseReference;
    iFirebaseLoadDone firebaseLoadDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Photos");
        firebaseLoadDone = this;

        btn_load = findViewById(R.id.btn_load);
        btn_pause = findViewById(R.id.btn_pause);
        btn_resume = findViewById(R.id.btn_resume);
        btn_reverse = findViewById(R.id.btn_reverse);

        storiesProgressView = findViewById(R.id.stories);


        imageView = findViewById(R.id.image_story);

        // Event
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });

        btn_reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });

        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.resume();
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.pause();
            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Photos> photosList = new ArrayList<>();
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren())
                        {
                            Photos photo = itemSnapshot.getValue(Photos.class);
                            photosList.add(photo);
                        }
                        Log.i("TAG", String.valueOf(photosList.size()));
                        firebaseLoadDone.onFirebaseLoadSuccess(photosList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public void onFirebaseLoadSuccess(final List<Photos> photosList) {
        storiesProgressView.setStoriesCount(photosList.size());
        storiesProgressView.setStoryDuration(2500L);  // 1.5 sec

        Picasso.get().load(photosList.get(0).getImage()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                storiesProgressView.startStories();
            }

            @Override
            public void onError(Exception e) {

            }
        });

        // Set Event stories
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                if (counter < photosList.size()){
                    counter++;
                    Picasso.get().load(photosList.get(counter).getImage()).into(imageView);
                }
            }

            @Override
            public void onPrev() {
                if (counter > 0){
                    counter-- ;
                    Picasso.get().load(photosList.get(counter).getImage()).into(imageView);
                }
            }

            @Override
            public void onComplete() {
                counter = 0;
                Toast.makeText(StoryMain.this, "Load Done !!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(StoryMain.this, message, Toast.LENGTH_SHORT).show();
    }
}
