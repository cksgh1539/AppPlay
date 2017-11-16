package com.example.hp.appplay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static String TAG = "MultimediaTest";

    private MediaPlayer mMediaPlayer;
    private VideoView videoView;
    private ImageView imageView;
    private MediaRecorder mMediaRecorder;
    private String mVideoFileName = null;
    private File mPhotoFile = null;
    private File destination = null;
    private String mPhotoFileName = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);

        final Button musicPlayBtn = (Button) findViewById(R.id.musicPlayBtn);
        Button videoPlayBtn = (Button) findViewById(R.id.videoPlayBtn);
        Button imageCaptureBtn = (Button) findViewById(R.id.imageCaptureBtn);
        Button videoRecBtn = (Button) findViewById(R.id.videoRecBtn);

        // MediaPlayer mMediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.gitan);

        checkDangerousPermissions();
        Music();


        musicPlayBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                if (mMediaPlayer.isPlaying()) { // 현재 재생 중인 미디어를 선택한 경우
                    mMediaPlayer.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.", Toast.LENGTH_SHORT).show();
                } else {
                    mMediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 재시작됨.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final Uri Movie = Uri.parse("file:///storage/emulated/0/Movies/twice.mp4");

        videoPlayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                MediaController mc = new MediaController(MainActivity.this);
                videoView.setMediaController(mc);
                videoView.setVideoURI(Movie);

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer player) {
                        videoView.seekTo(0);
                        videoView.start();
                    }
                });
            }
        });


        imageCaptureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });

        videoRecBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

        }





    private void Music() {
        String Path = Environment.getExternalStorageDirectory()
                + "/Music/gitan.mp3";
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(Path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //1. 카메라 앱으로 찍은 이미지를 저장할 파일 객체 생성
            mPhotoFileName = "IMG"+currentDateFormat()+".jpg";
            mPhotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPhotoFileName);

            if (mPhotoFile !=null) {
                //2. 생성된 파일 객체에 대한 Uri 객체를 얻기
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.hp.appplay", mPhotoFile);

                //3. Uri 객체를 Extras를 통해 카메라 앱으로 전달
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } else
                Toast.makeText(getApplicationContext(), "file null", Toast.LENGTH_SHORT).show();
        }
    }

    static final int REQUEST_VIDEO_CAPTURE = 2;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            //1. 카메라 앱으로 찍은 동영상을 저장할 파일 객체 생성
            mVideoFileName = "VIDEO" + currentDateFormat() + ".mp4";
            destination = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                    mVideoFileName);
            if (destination != null) {
                //2. 생성된 파일 객체에 대한 Uri 객체를 얻기
                Uri videoUri = FileProvider.getUriForFile(this,
                        "com.example.hp.appplay", destination);

                //3. Uri 객체를 Extras를 통해 카메라 앱으로 전달
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }


    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(mPhotoFile));
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        new FileOutputStream(mPhotoFile));
                imageView.setImageBitmap(imgBitmap);
                //  mAdapter.addItem(new MediaItem(MediaItem.SDCARD, mPhotoFileName, Uri.fromFile(mPhotoFile), MediaItem.IMAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri Rec = Uri.fromFile(destination);
            MediaController mc = new MediaController(MainActivity.this);
            videoView.setMediaController(mc);
            videoView.setVideoURI(Rec);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer player) {
                    videoView.seekTo(0);
                    videoView.start();

                }

            });
        }
    }

    private void dispatchPickPictureIntent() {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK);
        pickPictureIntent.setType("image/*");

        if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPictureIntent,REQUEST_IMAGE_PICK);
        }
    }


    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private static final int REQUEST_EXTERNAL_STORAGE_FOR_MULTIMEDIA = 1;
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE_FOR_MULTIMEDIA);
        }
    }



    private void killMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onStop() {
        super.onStop();
        killMediaPlayer();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }


}
