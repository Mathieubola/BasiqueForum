package com.example.basiqueforum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    private Button mAddImage;
    private Button mSubmit;
    private EditText mTitle;
    private EditText mContent;

    private Uri imageUri = null;

    private static final int GALLERY_REQUEST = 1;
    private static final int RANDOM_LENGHT = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage = FirebaseStorage.getInstance().getReference().child("Blog_Image");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mProgressDialog = new ProgressDialog(this);

        mAddImage = (Button) findViewById(R.id.add_image);
        mSubmit = (Button) findViewById(R.id.submit);
        mTitle = (EditText) findViewById(R.id.title);
        mContent = (EditText) findViewById(R.id.content);


        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Upload...");
                mProgressDialog.show();

                final String title = mTitle.getText().toString().trim();
                final String ctn = mContent.getText().toString().trim();

                if (title != "") {
                    final StorageReference filepath = mStorage.child(random());
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        DatabaseReference newPost = mDatabase.push();
                                        newPost.child("title").setValue(title);
                                        newPost.child("ctn").setValue(ctn);
                                        newPost.child("img").setValue(uri.toString());
                                        //Add UserID FirebaseAuth.getCurrentuser().getUID();
                                    }
                                });
                            }
                        }
                    });
                }
                mProgressDialog.dismiss();
                startActivity(new Intent(PostActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = (Uri) data.getData();

        }
    }

    protected static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < RANDOM_LENGHT; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            if (tempChar != '\\' && tempChar != '/') {
                randomStringBuilder.append(tempChar);
            } else {
                randomStringBuilder.append('@');
            }
        }
        return randomStringBuilder.toString();
    }
}
