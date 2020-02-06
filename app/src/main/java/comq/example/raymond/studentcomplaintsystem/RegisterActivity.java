package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar registerToolBar;
    private TextView txtHaveAccountAlready;
    private Spinner spinnerFaculty, spinnerLevel;
    private Spinner spinnerFns, spinnerFss,spinnerFms;
    private EditText editTextName, editTextMatNo, editTextEmail, editTextPhone, editTextPassword, editTextCPassword;
    private Button  btnSignUp;

    private Spinner spinnerDepartment;
    private FirebaseAuth mAuth;
    private DatabaseReference students;
    private String uId = "";

    private Spinner spinnerArts;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        students = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");

        mAuth = FirebaseAuth.getInstance();

        //toolbar
        //initialize our toolBar
        registerToolBar = findViewById(R.id.register_toolbar);
        setSupportActionBar(registerToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Account");

        txtHaveAccountAlready = findViewById(R.id.textviewLogin);
        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        //spinnerArts = findViewById(R.id.spinnerArts);
        //spinnerFms = findViewById(R.id.spinnerFms);
       // spinnerFns = findViewById(R.id.spinnerFns);
        //spinnerFss = findViewById(R.id.spinnerFss);
        spinnerLevel = findViewById(R.id.spinnerLevel);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        editTextCPassword = findViewById(R.id.editTextCPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextMatNo = findViewById(R.id.editTextMatNo);
        btnSignUp = findViewById(R.id.buttonSignUp);
//
//        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String faculty = spinnerFaculty.getSelectedItem().toString();
//                if (faculty.equals("Select your faculty")){
//                    spinnerFss.setVisibility(View.INVISIBLE);
//                    spinnerFns.setVisibility(View.INVISIBLE);
//                    spinnerFms.setVisibility(View.INVISIBLE);
//                    spinnerArts.setVisibility(View.INVISIBLE);
//                    //department empty
//                    //department = "";
//                    Toast.makeText(RegisterActivity.this, "Please select your faculty", Toast.LENGTH_SHORT).show();
//                }else if (faculty.equals("Arts and Humanity")){
//                    spinnerArts.setVisibility(View.VISIBLE);
//                    spinnerFss.setVisibility(View.INVISIBLE);
//                    spinnerFns.setVisibility(View.INVISIBLE);
//                    spinnerFms.setVisibility(View.INVISIBLE);
//                    //get selected department from faculty of arts
//                    //department = spinnerArts.getSelectedItem().toString();
//                }else if (faculty.equals("Natural and Applied Sciences")){
//                    spinnerFns.setVisibility(View.VISIBLE);
//                    spinnerFss.setVisibility(View.INVISIBLE);
//                    spinnerFms.setVisibility(View.INVISIBLE);
//                    spinnerArts.setVisibility(View.INVISIBLE);
//                    //get selected department from faculty of FNS
//                    //department = spinnerFns.getSelectedItem().toString();
//                }else if (faculty.equals("Management Sciences")){
//                    spinnerFms.setVisibility(View.VISIBLE);
//                    spinnerFss.setVisibility(View.INVISIBLE);
//                    spinnerFns.setVisibility(View.INVISIBLE);
//                    spinnerArts.setVisibility(View.INVISIBLE);
//                    //get selected department from faculty of management sciences
//                    //department = spinnerFms.getSelectedItem().toString();
//                }else if (faculty.equals("Social Sciences")){
//                    spinnerFss.setVisibility(View.VISIBLE);
//                    spinnerFns.setVisibility(View.INVISIBLE);
//                    spinnerFms.setVisibility(View.INVISIBLE);
//                    spinnerArts.setVisibility(View.INVISIBLE);
//                    //get selected department from faculty of social sciences
//                    //department = spinnerArts.getSelectedItem().toString();
//                    //Toast.makeText(RegisterActivity.this, ""+department, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        txtHaveAccountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        //get information to string
        final String fullName = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String faculty = spinnerFaculty.getSelectedItem().toString();
        final String department = spinnerDepartment.getSelectedItem().toString();
        final String level = spinnerLevel.getSelectedItem().toString();
        final String matNo = editTextMatNo.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();
        String cPassword = editTextCPassword.getText().toString().trim();

        if (faculty.equals("Select Faculty")){
            Toast.makeText(this, "Please select your faculty", Toast.LENGTH_SHORT).show();

        }else if (department.equals("Select Department")){
            Toast.makeText(this, "Please select your department", Toast.LENGTH_SHORT).show();

        }if (TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();

        }else if (level.equals("Select your level")){
            Toast.makeText(this, "Please select your level", Toast.LENGTH_SHORT).show();

        }if (!password.equals(cPassword)){
            Toast.makeText(this, "Your password does not match confirm password", Toast.LENGTH_SHORT).show();

        }else if (phone.length() <11){
            Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();

        }else{
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    uId = mAuth.getCurrentUser().getUid();
                    students.child(uId).child("name").setValue(fullName);
                    students.child(uId).child("email").setValue(email);
                    students.child(uId).child("phone").setValue(phone);
                    students.child(uId).child("faculty").setValue(faculty);
                    students.child(uId).child("department").setValue(department);
                    students.child(uId).child("level").setValue(level);
                    students.child(uId).child("matNo").setValue(matNo);
                    students.child(uId).child("uId").setValue(uId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



    }
}
