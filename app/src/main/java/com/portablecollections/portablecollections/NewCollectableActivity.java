package com.portablecollections.portablecollections;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class NewCollectableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_collectable);

        Intent intent = getIntent();
        byte[] takenPictureArray = intent.getByteArrayExtra(MainActivity.EXTRA_TAKENPICTURE);
        Bitmap takenPicture = BitmapFactory.decodeByteArray(takenPictureArray, 0, takenPictureArray.length);
        ImageView newImageView = findViewById(R.id.new_imageview);
        newImageView.setImageBitmap(Bitmap.createScaledBitmap(takenPicture, newImageView.getWidth(),
                newImageView.getHeight(), false));

        Button done = this.findViewById(R.id.new_done);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newNameText = view.findViewById(R.id.new_name);
                String newName = newNameText.getText().toString();
                CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper();
                File takenPictureFile = pictureHelper.createImageFile(getApplicationContext(), newName);
                // todo: create the object in Room
                // todo: navigate back to MainActivity's layout
                // todo: refresh the RecycleView
            }
        });
    }
}
