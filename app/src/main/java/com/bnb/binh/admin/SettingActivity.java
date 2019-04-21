package com.bnb.binh.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity
{

    private Button UpdateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private ProgressDialog Loading;
    private DatabaseReference RootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = mAuth.getCurrentUser().getUid();

        Begin();
        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateSetting();
            }
        });
        RetrieveUserInfo();


    }

    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String  retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String  retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String  retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String  retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String  retrieveStatus = dataSnapshot.child("status").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else
                        {
                            Toast.makeText(SettingActivity.this,"Vui lòng điền thông tin chỉnh sửa của bạn...",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void UpdateSetting()
    {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this,"Vui lòng điền tên của bạn...",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(this,"Vui lòng điền thông tin của bạn...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loading.setTitle("Đang cập nhật");
            Loading.setMessage("Vui lòng chờ......");
            Loading.setCanceledOnTouchOutside(true);
            Loading.show();

            HashMap<String,String> profileMap = new HashMap<>();
                profileMap.put("uid",currentUserId);
                profileMap.put("name",setUserName);
                profileMap.put("status",setUserStatus);
            RootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingActivity.this,"Profile Update Successfuly...",Toast.LENGTH_SHORT).show();
                                Loading.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(SettingActivity.this,"ERROR: "+message,Toast.LENGTH_SHORT).show();
                                Loading.dismiss();
                            }
                        }
                    });

        }
    }


    private void SendUserToMainActivity()
    {
        Intent MainInten = new Intent(SettingActivity.this,MainActivity.class);
        MainInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainInten);
        fileList();
    }



    private void Begin()
    {
        UpdateAccountSetting = (Button) findViewById(R.id.update_setting_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        Loading = new ProgressDialog(this);

    }
}
