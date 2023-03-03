package com.example.socialnetword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginAdminActivity extends AppCompatActivity {
    private Button btn_dn;
    private EditText edt_email, edt_pass;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference UsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        mAuth = FirebaseAuth.getInstance();
        btn_dn = (Button) findViewById(R.id.btn_dn);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_pass = (EditText) findViewById(R.id.edt_pass);



        btn_dn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();

            }
        });

        loadingBar = (ProgressDialog) new ProgressDialog(this);
    }

    private void UserLogin() {
        String email = edt_email.getText().toString();
        String password = edt_pass.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Nhập email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Nhập mật khẩu ", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Đăng nhập");
            loadingBar.setMessage("Vui lòng đợi đang lấy dữ liệu từ cơ sở dữ liệu");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {

                        Toast.makeText(LoginAdminActivity.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();


                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginAdminActivity.this, "Lỗi" +message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginAdminActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}