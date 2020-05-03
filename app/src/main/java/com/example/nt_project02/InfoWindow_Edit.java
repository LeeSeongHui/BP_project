package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.CustomData.MarkerModel;
import com.example.nt_project02.Fragment.PeopleFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class InfoWindow_Edit extends AppCompatActivity {

    private String Place_Name;
    private String Place_Adress;
    private String Content;
    private Place place;
    private Bitmap Place_Image;
    private DatabaseReference mDatabase;
    private String ClickMarkerAdress;

    private String TAG = "InfoWindow_Edit";

    private TextView PlaceName_TextView;
    private TextView PlaceAdress_TextView;
    private ImageView PlaceImage_ImageView;
    private EditText Content_EditText;

    private View.OnClickListener mResiterListener;
    private MarkerModel.MarkerData markerData;
    private String Key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infowindow__edit);

        PlaceName_TextView = (TextView) findViewById(R.id.activity_infowindow_edit_PlaceName);
        PlaceAdress_TextView = (TextView) findViewById(R.id.activity_infowindow_edit_PlaceAdress);
        PlaceImage_ImageView = (ImageView) findViewById(R.id.activity_infowindow_edit_PlaceImage);
        Content_EditText = (EditText) findViewById(R.id.activity_infowindow_edit_Content_EditText);
        final Button Register_Button = (Button) findViewById(R.id.activity_infowindow_edit_Register_Button);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent data = getIntent();

        //  검색 장소 정보 인텐트 가져오기
        place = (Place) data.getParcelableExtra("Place");

        // 등록된 마커 주소 가져오기
        ClickMarkerAdress = data.getStringExtra("MarkerAdress");


        // 지도 위치 검색시
        if (place != null && ClickMarkerAdress == null) {


            Place_Name = place.getName().toString();
            Place_Adress = place.getAddress().toString();

            //검색 장소 사진 인텐트로 가져와서 비트맵 형태로 변환
            byte[] arr = getIntent().getByteArrayExtra("image");
            Place_Image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            PlaceImage_ImageView.setImageBitmap(Place_Image);

            PlaceName_TextView.setText(Place_Name);
            PlaceAdress_TextView.setText(Place_Adress);


            //등록 버튼
            Register_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Content = (Content_EditText.getText()).toString();


                    Location location = new Location("");
                    location.setLatitude(place.getLatLng().latitude);
                    location.setLongitude(place.getLatLng().longitude);


                    // Get the data from an ImageView as bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Place_Image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();


                    final MarkerModel.MarkerData makrer = new MarkerModel.MarkerData();

                    //마커 정보 설정
                    makrer.Content = Content;
                    makrer.Latitude = location.getLatitude();
                    makrer.Longitude = location.getLongitude();
                    makrer.markerTitle = Place_Name;
                    makrer.markerSnippet = Place_Adress;
                    makrer.uid =FirebaseAuth.getInstance().getUid();  //어플 현재 이용자 아이디
                    makrer.timestamp = ServerValue.TIMESTAMP; //시간정보 설정;


                    //스토리지에 사진 업로드
                    final StorageReference ref = FirebaseStorage.getInstance().getReference().child("GoogleMapImages").child(Place_Adress);
                    UploadTask uploadTask = ref.putBytes(data);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                //Url 정보 가져오기
                                String ImageUrl = task.getResult().toString();
                                makrer.ImageUrl = ImageUrl;


                                //데이터베이스에 마커 정보 추가
                                mDatabase.child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker").push().setValue(makrer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "성공!", Toast.LENGTH_LONG).show();

                                        //검색한 마커 삭제
                                        if (GoogleMap_Fragment.currentMarker != null) {
                                            GoogleMap_Fragment.currentMarker.remove();
                                        }
                                    }
                                });
                            }
                        }
                    });

                    finish();

                }
            });


            //마커 정보를 서버에서 불러올 때
        } else if (place == null && ClickMarkerAdress != null) {


            Register_Button.setText("수정");

            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            // 클릭한 마커 데이터 가져오기
            Query query = mFirebaseDatabaseReference.child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker").orderByChild("markerSnippet").equalTo(ClickMarkerAdress);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        markerData = postSnapshot.getValue(MarkerModel.MarkerData.class);

                        Log.e(TAG, "QueryMarkerData:" + markerData.markerTitle);

                        Place_Name = markerData.markerTitle;
                        Place_Adress = markerData.markerSnippet;

                        PlaceName_TextView.setText(Place_Name);
                        PlaceAdress_TextView.setText(Place_Adress);
                        Content_EditText.setText(markerData.Content);

                        Glide.with
                                (getApplicationContext())
                                .load(markerData.ImageUrl)
                                .into(PlaceImage_ImageView);




                        View.OnClickListener myViewOnclicklistener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                MarkerModel.MarkerData NewMarKerData=new MarkerModel.MarkerData();

                                //마커 정보 설정
                                NewMarKerData.Content = Content_EditText.getText().toString();
                                NewMarKerData.Latitude = markerData.Latitude;
                                NewMarKerData.Longitude = markerData.Longitude;
                                NewMarKerData.markerTitle = Place_Name;
                                NewMarKerData.markerSnippet = Place_Adress;
                                NewMarKerData.uid =FirebaseAuth.getInstance().getUid();  //어플 현재 이용자 아이디
                                NewMarKerData.timestamp = ServerValue.TIMESTAMP; //시간정보 설정;
                                NewMarKerData.ImageUrl=markerData.ImageUrl;

                                Map<String,Object > postValues=NewMarKerData.toMap();


                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(postSnapshot.getKey(),postValues);


                                mDatabase.child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker").updateChildren(childUpdates);
                                Toast.makeText(getApplicationContext(),"수정 완료!",Toast.LENGTH_LONG);

                                finish();
                            }

                        };


                        Register_Button.setOnClickListener(myViewOnclicklistener);

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });






        }








    }

}
