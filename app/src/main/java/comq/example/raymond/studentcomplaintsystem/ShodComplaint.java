package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.studentcomplaintsystem.Model.ScomplaintModel;

public class ShodComplaint extends AppCompatActivity {
    private Toolbar complaintToolbar;

    private FloatingActionButton fab;

    private FirebaseAuth mAuth;
    private DatabaseReference user, staff, complaints;
    private ProgressDialog dialog;

    private String respondent, name, matNo, faculty, department, phone, level = "";



    private ScomplaintModel newComplaint;

    private String uId ="";
    String hodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shod_complaint);

        dialog = new ProgressDialog(this);

        fab = findViewById(R.id.fab);



        mAuth = FirebaseAuth.getInstance();
        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff").child("hods");
        user = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");
        complaints = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("complaints").child("department");


        uId = mAuth.getCurrentUser().getUid();
        //get crime id from Intent
        if (getIntent() != null){
            hodId = getIntent().getStringExtra("hodId");

                getHodDetails(hodId);

        }else if (getIntent() == null){
            loadComplaints(uId);
        }




        getStudentDetails();




        //toolbar
        //initialize our toolBar
        complaintToolbar = findViewById(R.id.hods_complaint_toolbar);
        setSupportActionBar(complaintToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Department Complaint");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaint_dialog();
            }
        });
    }

    private void loadComplaints(String uId) {
    }

    private void getHodDetails(String hodId) {
        staff.child(hodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                respondent = dataSnapshot.child("department").getValue(String.class);

                //Toast.makeText(ShodComplaint.this, ""+respondent, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void complaint_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShodComplaint.this);
        View view = getLayoutInflater().inflate(R.layout.complaint_dialong, null);

        final EditText editTexSubject = view.findViewById(R.id.edit_subject);
        final EditText editTextComplaint = view.findViewById(R.id.edit_complaint);
        final Button btn_send = view.findViewById(R.id.btn_send);

        //set onclick listener on login button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTexSubject.getText().toString().trim();
                String complaint = editTextComplaint.getText().toString().trim();



                String status = "Sent";

                final long complaintDate = new Date().getTime();

                if (TextUtils.isEmpty(subject)){
                    Toast.makeText(ShodComplaint.this, "Please type the subject of your complaint", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(complaint)){
                    Toast.makeText(ShodComplaint.this, "Please type your complaint", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setMessage("Sending Complaint...");
                    dialog.show();
                    newComplaint = new ScomplaintModel(name, uId, matNo, faculty, department, subject, complaint,respondent, phone, level, status, complaintDate);
                    complaints.push().setValue(newComplaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText(ShodComplaint.this, "Complaint sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ShodComplaint.this, DisplayDepartmentCOmplaints.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ShodComplaint.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getStudentDetails() {
        user.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                department = dataSnapshot.child("department").getValue(String.class);
                phone = dataSnapshot.child("phone").getValue(String.class);
                level = dataSnapshot.child("level").getValue(String.class);
                faculty = dataSnapshot.child("faculty").getValue(String.class);
                matNo = dataSnapshot.child("matNo").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
