package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class FoggottenPassword extends AppCompatActivity {
    private Toolbar forgotPasswordToolbar;


    private Button btn_submit;
    private EditText edit_textEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foggotten_password);

        //toolbar
        //initialize our toolBar
        forgotPasswordToolbar = findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(forgotPasswordToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");



        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        edit_textEmail = findViewById(R.id.edt_email);
        btn_submit = findViewById(R.id.btn_forgot_password);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }


    private void resetPassword() {
        progressDialog.setMessage("Sending password reset mail ");

        String email = edit_textEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(FoggottenPassword.this, "Please provide the email you registered with", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(FoggottenPassword.this, "Password reset successfully, please check your email", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(FoggottenPassword.this, LoginActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FoggottenPassword.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



    }
}
