package com.practice.twiliotest.activities;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.practice.twiliotest.R;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoView;

import java.util.Collections;

public class VideoCallActivity extends AppCompatActivity {

    private static final String TAG = "VideoCallActivity";
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";

    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;
    private TextView videoStatusTextView;

    private Room room;
    private LocalParticipant localParticipant;

    private String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2Q1MjM0MWM5YmU1MDU1N2RlOTQ2NWFhY2ExNGZiZTBjLTE1MjY1NTEwOTIiLCJncmFudHMiOnsiaWRlbnRpdHkiOiIwMzMxMzU2NjYxMCIsInZpZGVvIjp7fX0sImlhdCI6MTUyNjU1MTA5MiwiZXhwIjoxNTI2NTU0NjkyLCJpc3MiOiJTS2Q1MjM0MWM5YmU1MDU1N2RlOTQ2NWFhY2ExNGZiZTBjIiwic3ViIjoiQUM5ODIyY2E1MjVhMDJiNDUwYmE5YTQ0MTU2NWI3YTZkZiJ9.zlR31HoMgXkDS0wYfrn98xJE9lFFTB3w3EIlFJwXWNo";
//    private String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2Q1MjM0MWM5YmU1MDU1N2RlOTQ2NWFhY2ExNGZiZTBjLTE1MjY1NTE3MjciLCJncmFudHMiOnsiaWRlbnRpdHkiOiIwMzMxMzU2NjYxMDEiLCJ2aWRlbyI6e319LCJpYXQiOjE1MjY1NTE3MjcsImV4cCI6MTUyNjU1NTMyNywiaXNzIjoiU0tkNTIzNDFjOWJlNTA1NTdkZTk0NjVhYWNhMTRmYmUwYyIsInN1YiI6IkFDOTgyMmNhNTI1YTAyYjQ1MGJhOWE0NDE1NjViN2E2ZGYifQ.3BqfYuaWWyTsTs4XB_P0e91CbZ2ReNBeDhbgC9G9SuY";

    private EncodingParameters encodingParameters;


    private LocalVideoTrack localVideoTrack;
    private LocalAudioTrack localAudioTrack;
    private AudioManager audioManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);



        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);
        videoStatusTextView = findViewById(R.id.video_status_textview);


        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        createAudioAndVideoTracks();
        connectToRoom();
    }


    private void createAudioAndVideoTracks() {
        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);

        CameraCapturer cameraCapturer = new CameraCapturer(this, CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA)?CameraCapturer.CameraSource.FRONT_CAMERA:CameraCapturer.CameraSource.BACK_CAMERA);
        localVideoTrack = LocalVideoTrack.create(this,true, cameraCapturer,LOCAL_VIDEO_TRACK_NAME);
        localVideoTrack.addRenderer(thumbnailVideoView);
    }


    private void connectToRoom() {
        ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                .roomName("abc")
                .audioTracks(Collections.singletonList(localAudioTrack))
                .videoTracks(Collections.singletonList(localVideoTrack))
                .build();
        room = Video.connect(this, connectOptions, new RoomListener());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (room!=null)
            room.disconnect();
    }

    private class RoomListener implements Room.Listener {
        @Override
        public void onConnected(Room room) {
            localParticipant = room.getLocalParticipant();
            videoStatusTextView.setText("Connected to " + room.getName());
            if (room.getRemoteParticipants().size()>0) {
                room.getRemoteParticipants().get(0).setListener(new ParticipantListener());
            }
        }

        @Override
        public void onConnectFailure(Room room, TwilioException e) {
            videoStatusTextView.setText("Failed to connect");
        }

        @Override
        public void onDisconnected(Room room, TwilioException e) {
            localParticipant = null;
            videoStatusTextView.setText("Disconnected from " + room.getName());
        }

        @Override
        public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
            remoteParticipant.setListener(new ParticipantListener());

        }

        @Override
        public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
        }

        @Override
        public void onRecordingStarted(Room room) {}

        @Override
        public void onRecordingStopped(Room room) {}
    }

    private class ParticipantListener implements RemoteParticipant.Listener {

        @Override
        public void onAudioTrackPublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

        }

        @Override
        public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, TwilioException twilioException) {

        }

        @Override
        public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication, RemoteAudioTrack remoteAudioTrack) {

        }

        @Override
        public void onVideoTrackPublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {
            remoteVideoTrack.addRenderer(primaryVideoView);
        }

        @Override
        public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, TwilioException twilioException) {

        }

        @Override
        public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication, RemoteVideoTrack remoteVideoTrack) {

        }

        @Override
        public void onDataTrackPublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

        }

        @Override
        public void onDataTrackUnpublished(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication) {

        }

        @Override
        public void onDataTrackSubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

        }

        @Override
        public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, TwilioException twilioException) {

        }

        @Override
        public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant, RemoteDataTrackPublication remoteDataTrackPublication, RemoteDataTrack remoteDataTrack) {

        }

        @Override
        public void onAudioTrackEnabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackDisabled(RemoteParticipant remoteParticipant, RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onVideoTrackEnabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackDisabled(RemoteParticipant remoteParticipant, RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }
    }
}
