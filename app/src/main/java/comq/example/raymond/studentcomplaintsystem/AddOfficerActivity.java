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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class AddOfficerActivity extends AppCompatActivity {
    private Toolbar addOfficerToolbar;
    private Button btnAddDean, btnAddHod, btnAddNonAcademic;

    private DatabaseReference staff;
    private ProgressDialog dialog;
    private FirebaseAuth mAuth;

    private String uId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_officer);

        dialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff");



        //toolbar
        //initialize our toolBar
        addOfficerToolbar = findViewById(R.id.add_officer_toolbar);
        setSupportActionBar(addOfficerToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Officer");
        
        btnAddDean = findViewById(R.id.buttonAddDean);
        btnAddHod = findViewById(R.id.buttonAddHod);
        btnAddNonAcademic = findViewById(R.id.buttonAddNonAcademics);
        
        btnAddDean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDeanDialog();
            }
        });
        
        btnAddHod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHodDialog();
            }
        });
        
        btnAddNonAcademic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNonAcademicDialog();
            }
        });
    }

    private void addNonAcademicDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddOfficerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_non_academic_dialog, null);

        final EditText editTextName = view.findViewById(R.id.edit_name);
        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        final EditText editTextPassword = view.findViewById(R.id.edit_password);
        final EditText editTextCPassword = view.findViewById(R.id.edit_c_password);
        final Spinner spinnerDepartment = view.findViewById(R.id.spinnerDepartment);
        Button btn_add = view.findViewById(R.id.btn_add);

        //set onclick listener on login button
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString().trim();
                final String name = editTextName.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String cPassword = editTextCPassword.getText().toString().trim();
                final String department = spinnerDepartment.getSelectedItem().toString();

                String respondent = "Registry";

                final String status = "Sent";

                final long complaintDate = new Date().getTime();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(name)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(cPassword)){
                    Toast.makeText(AddOfficerActivity.this, "Please confirm password", Toast.LENGTH_SHORT).show();

                }else if (department.equals("Select Department")){
                    Toast.makeText(AddOfficerActivity.this, "Please select a valid department", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword)){

                }else {
                    dialog.setMessage("Adding Officer...");
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            uId = mAuth.getCurrentUser().getUid();
                            staff = staff.child("non_academics");
                            staff.child(uId).child("name").setValue(name);
                            staff.child(uId).child("email").setValue(email);
                            staff.child(uId).child("department").setValue(department);
                            staff.child(uId).child("uId").setValue(uId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    Toast.makeText(AddOfficerActivity.this, "Officer Added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddOfficerActivity.this, AdminHome.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddOfficerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addHodDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddOfficerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_hod_dialog, null);

        final EditText editTextName = view.findViewById(R.id.edit_name);
        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        final EditText editTextPassword = view.findViewById(R.id.edit_password);
        final EditText editTextCPassword = view.findViewById(R.id.edit_c_password);
        final Spinner spinnerDepartment = view.findViewById(R.id.spinnerDepartment);
        final Spinner spinnerFaculty = view.findViewById(R.id.spinnerFaculty);
        Button btn_add = view.findViewById(R.id.btn_add);

        //set onclick listener on login button
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString().trim();
                final String name = editTextName.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String cPassword = editTextCPassword.getText().toString().trim();
                final String department = spinnerDepartment.getSelectedItem().toString();
                final String faculty = spinnerFaculty.getSelectedItem().toString();

                String respondent = "Registry";

                final String status = "Sent";

                final long complaintDate = new Date().getTime();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(name)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(cPassword)){
                    Toast.makeText(AddOfficerActivity.this, "Please confirm password", Toast.LENGTH_SHORT).show();

                }else if (department.equals("Select Department")){
                    Toast.makeText(AddOfficerActivity.this, "Please select a valid department", Toast.LENGTH_SHORT).show();
                }else if (faculty.equals("Select Faculty")){
                    Toast.makeText(AddOfficerActivity.this, "Please select a valid faculty", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword)){

                }else {
                    dialog.setMessage("Adding HoD...");
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            uId = mAuth.getCurrentUser().getUid();
                            staff = staff.child("hods");
                            staff.child(uId).child("name").setValue(name);
                            staff.child(uId).child("email").setValue(email);
                            staff.child(uId).child("department").setValue(department);
                            staff.child(uId).child("faculty").setValue(faculty);
                            staff.child(uId).child("uId").setValue(uId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    Toast.makeText(AddOfficerActivity.this, "HoD Added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddOfficerActivity.this, AdminHome.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddOfficerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addDeanDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddOfficerActivity.this);
        View view = getLayoutInflater().inflate(R.layout.add_dean_dialog, null);

        final EditText editTextName = view.findViewById(R.id.edit_name);
        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        final EditText editTextPassword = view.findViewById(R.id.edit_password);
        final EditText editTextCPassword = view.findViewById(R.id.edit_c_password);
        final Spinner spinnerFaculty = view.findViewById(R.id.spinnerFaculty);
        Button btn_add = view.findViewById(R.id.btn_add);

        //set onclick listener on login button
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString().trim();
                final String name = editTextName.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String cPassword = editTextCPassword.getText().toString().trim();
                final String faculty = spinnerFaculty.getSelectedItem().toString();

                String respondent = "Registry";

                final String status = "Sent";

                final long complaintDate = new Date().getTime();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(name)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter officer's name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(password)){
                    Toast.makeText(AddOfficerActivity.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(cPassword)){
                    Toast.makeText(AddOfficerActivity.this, "Please confirm password", Toast.LENGTH_SHORT).show();

                }else if (faculty.equals("Select Faculty")){
                    Toast.makeText(AddOfficerActivity.this, "Please select a valid department", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword)){

                }else {
                    dialog.setMessage("Adding Dean...");
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            uId = mAuth.getCurrentUser().getUid();
                            staff = staff.child("deans");
                            staff.child(uId).child("name").setValue(name);
                            staff.child(uId).child("email").setValue(email);
                            staff.child(uId).child("faculty").setValue(faculty);
                            staff.child(uId).child("uId").setValue(uId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    Toast.makeText(AddOfficerActivity.this, "Dean Added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddOfficerActivity.this, AdminHome.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddOfficerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
