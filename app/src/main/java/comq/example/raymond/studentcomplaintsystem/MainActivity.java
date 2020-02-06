package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.studentcomplaintsystem.Model.User;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainToolBar;

    private String uId = "";

    private FirebaseAuth mAuth;

    private ProgressDialog loginProgress;
    private DatabaseReference staff;

    private Button btnLogin, btnSignUp, btnAdminLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnAdminLogin = findViewById(R.id.btn_admin_login);

        mAuth = FirebaseAuth.getInstance();
        loginProgress = new ProgressDialog(this);
        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff");

        //toolbar
        //initialize our toolBar
        mainToolBar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mainToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student Complaint System");


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLoginDialog();
            }
        });
    }

    private void adminLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.admin_login_dialog, null);

        final EditText editTexEmail = view.findViewById(R.id.edit_email);
        final EditText editTextPassword = view.findViewById(R.id.edit_password);
        final Button btn_login = view.findViewById(R.id.btn_login);
        final Button btn_dean = view.findViewById(R.id.btn_deans_login);
        final Button btn_hod = view.findViewById(R.id.btn_hod_login);
        final Button btn_non = view.findViewById(R.id.btn_non_academics);
        final Spinner spinnerFaculty = view.findViewById(R.id.spinner_faculty);
        final Spinner spinnerDepartment = view.findViewById(R.id.spinner_department);
        final Spinner spinnerNonAcademic = view.findViewById(R.id.spinner_non_academics);


        btn_dean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_hod.setVisibility(View.GONE);
                btn_non.setVisibility(View.GONE);
                spinnerDepartment.setVisibility(View.GONE);
                spinnerNonAcademic.setVisibility(View.GONE);
                spinnerFaculty.setVisibility(View.VISIBLE);
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this, "Good", Toast.LENGTH_SHORT).show();
                        String email = editTexEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        final String faculty = spinnerFaculty.getSelectedItem().toString();

                        if (faculty.equals("Select Faculty")){
                            Toast.makeText(MainActivity.this, "Please select your faculty", Toast.LENGTH_SHORT).show();

                        }else if (TextUtils.isEmpty(email)){
                            Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        }else if (TextUtils.isEmpty(password)){
                            Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        }else {
                            loginProgress.setMessage("login in as dean ...");
                            loginProgress.show();
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        staff = staff.child("deans");
                                        uId = mAuth.getCurrentUser().getUid();
                                        //Toast.makeText(MainActivity.this, ""+uId, Toast.LENGTH_SHORT).show();
                                        staff.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child(uId).exists()){

                                                    String retrieveFaculty = dataSnapshot.child(uId).child("faculty").getValue(String.class);
                                                    String retrievedName = dataSnapshot.child(uId).child("name").getValue(String.class);

                                                    User user = dataSnapshot.getValue(User.class);
                                                    user.setFaculty(faculty);
                                                    user.setuId(uId);
                                                    user.setName(retrievedName);

                                                    if (retrieveFaculty.equals(faculty)){
                                                        loginProgress.dismiss();
                                                        Intent loginIntent = new Intent(MainActivity.this,DeansHome.class);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(loginIntent);
                                                        finish();
                                                    }else {
                                                        loginProgress.dismiss();
                                                        Toast.makeText(MainActivity.this, "Some thing went wrong, please check your credentials and try again", Toast.LENGTH_LONG).show();
                                                    }
                                                }else {
                                                    loginProgress.dismiss();
                                                    Toast.makeText(MainActivity.this, "Wrong credentials, please try again", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loginProgress.dismiss();
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                    }
                });
            }
        });

        btn_hod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_dean.setVisibility(View.GONE);
                btn_non.setVisibility(View.GONE);
                spinnerFaculty.setVisibility(View.VISIBLE);
                spinnerDepartment.setVisibility(View.VISIBLE);
                spinnerNonAcademic.setVisibility(View.GONE);

                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = editTexEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        final String faculty = spinnerFaculty.getSelectedItem().toString();
                        final String department = spinnerDepartment.getSelectedItem().toString();

                        if (faculty.equals("Select Faculty")){
                            Toast.makeText(MainActivity.this, "Please select your faculty", Toast.LENGTH_SHORT).show();

                        }else if (department.equals("Select Department")){
                            Toast.makeText(MainActivity.this, "Please select department", Toast.LENGTH_SHORT).show();
                        }else if (TextUtils.isEmpty(email)){
                            Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        }else if (TextUtils.isEmpty(password)){
                            Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        }else {
                            loginProgress.setMessage("login in as HoD ...");
                            loginProgress.show();
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        staff = staff.child("hods");
                                        uId = mAuth.getCurrentUser().getUid();
                                        //Toast.makeText(MainActivity.this, ""+uId, Toast.LENGTH_SHORT).show();
                                        staff.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child(uId).exists()){

                                                    String retrieveFaculty = dataSnapshot.child(uId).child("faculty").getValue(String.class);
                                                    String retrieveDepartment = dataSnapshot.child(uId).child("department").getValue(String.class);

                                                    if (retrieveFaculty.equals(faculty) && retrieveDepartment.equals(department)){
                                                        loginProgress.dismiss();
                                                        Intent loginIntent = new Intent(MainActivity.this,HoDsHome.class);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(loginIntent);
                                                        finish();
                                                    }else {
                                                        loginProgress.dismiss();
                                                        Toast.makeText(MainActivity.this, "Some thing went wrong, please check your credentials and try again", Toast.LENGTH_LONG).show();
                                                    }
                                                }else {
                                                    loginProgress.dismiss();
                                                    Toast.makeText(MainActivity.this, "Wrong credentials, please try again", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loginProgress.dismiss();
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                    }
                });

            }
        });

        btn_non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_dean.setVisibility(View.GONE);
                btn_hod.setVisibility(View.GONE);
                spinnerDepartment.setVisibility(View.GONE);
                spinnerFaculty.setVisibility(View.GONE);
                spinnerNonAcademic.setVisibility(View.VISIBLE);

                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = editTexEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        final String department = spinnerNonAcademic.getSelectedItem().toString();

                        if (department.equals("Select Department")){
                            Toast.makeText(MainActivity.this, "Please select your department", Toast.LENGTH_SHORT).show();

                        }else if (TextUtils.isEmpty(email)){
                            Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        }else if (TextUtils.isEmpty(password)){
                            Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        }else {
                            loginProgress.setMessage("login in as non academics ...");
                            loginProgress.show();
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        staff = staff.child("non_academics");
                                        uId = mAuth.getCurrentUser().getUid();
                                        //Toast.makeText(MainActivity.this, ""+uId, Toast.LENGTH_SHORT).show();
                                        staff.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child(uId).exists()){

                                                    String retrieveDepartment = dataSnapshot.child(uId).child("department").getValue(String.class);

                                                    if (retrieveDepartment.equals(department)){
                                                        loginProgress.dismiss();
                                                        Intent loginIntent = new Intent(MainActivity.this,NonAcademicsHome.class);
                                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(loginIntent);
                                                        finish();
                                                    }else {
                                                        loginProgress.dismiss();
                                                        Toast.makeText(MainActivity.this, "Some thing went wrong, please check your credentials and try again", Toast.LENGTH_LONG).show();
                                                    }
                                                }else {
                                                    loginProgress.dismiss();
                                                    Toast.makeText(MainActivity.this, "Wrong credentials, please try again", Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loginProgress.dismiss();
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                    }
                });
            }
        });

        //set onclick listener on login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Please select who to login as", Toast.LENGTH_LONG).show();



            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
