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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.studentcomplaintsystem.Model.RegistryComplaintModel;
import comq.example.raymond.studentcomplaintsystem.Model.ScomplaintModel;

public class AregistryComplaintDetails extends AppCompatActivity {
    private Toolbar complaintDetailToolbar;
    private ProgressDialog dialog;

    private FloatingActionButton fab;

    private TextView txt_name, txt_matNo, txt_department, txt_level ,txt_subject, txt_complaint;

    String complaintId = "";
    String studentId, name, matNo, department2,  level1 = "";

    private DatabaseReference students, complaints;

    private RegistryComplaintModel newComplaint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aregistry_complaint_details);

        dialog = new ProgressDialog(this);

        complaints = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("complaints").child("registry");
        students = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");

        //toolbar
        //initialize our toolBar
        complaintDetailToolbar = findViewById(R.id.complaint_details_toolbar);
        setSupportActionBar(complaintDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student Complaint Details");

        fab = findViewById(R.id.fab);

        txt_department = findViewById(R.id.studentDepartment);
        txt_level = findViewById(R.id.studentLevel);
        txt_matNo = findViewById(R.id.studentMatNo);
        txt_name = findViewById(R.id.studentName);
        txt_subject = findViewById(R.id.complaint_subject);
        txt_complaint = findViewById(R.id.txt_complaint);


        //get crime id from Intent
        if (getIntent() != null){
            complaintId = getIntent().getStringExtra("complaintId");

            if (!complaintId.isEmpty()){
                getComplaintDetails(complaintId);

            }

        }




    }

    private void getComplaintDetails(String complaintId) {
        complaints.child(complaintId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ScomplaintModel scomplaintModel = dataSnapshot.getValue(ScomplaintModel.class);
                //studentId = scomplaintModel.getuId().toString();

                txt_department.setText("Department: " + scomplaintModel.getDepartment());
                txt_level.setText("Level: "  + scomplaintModel.getLevel());
                txt_matNo.setText("Mat. No: " + scomplaintModel.getMatNo());
                txt_name.setText("Name: " + scomplaintModel.getName());
                txt_complaint.setText(scomplaintModel.getComplaint());
                txt_subject.setText(scomplaintModel.getSubject());

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDialog();

                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AregistryComplaintDetails.this);
        View view = getLayoutInflater().inflate(R.layout.complaint_dialong, null);

        final EditText editTexSubject = view.findViewById(R.id.edit_subject);
        final EditText editTextComplaint = view.findViewById(R.id.edit_complaint);
        Button btn_send = view.findViewById(R.id.btn_send);

        //set onclick listener on login button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTexSubject.getText().toString().trim();
                String complaint = editTextComplaint.getText().toString().trim();
                String respondent = "Registry";

                String status = "Replied";

                final long complaintDate = new Date().getTime();

                if (TextUtils.isEmpty(subject)){
                    Toast.makeText(AregistryComplaintDetails.this, "Please type the subject of your complaint", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(complaint)){
                    Toast.makeText(AregistryComplaintDetails.this, "Please type your complaint", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setMessage("Sending Complaint...");
                    dialog.show();
                    newComplaint = new RegistryComplaintModel(studentId, subject, complaint, respondent, status, complaintDate);
                    complaints.push().setValue(newComplaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText(AregistryComplaintDetails.this, "Complaint sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AregistryComplaintDetails.this, AregistryComplaints.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AregistryComplaintDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void retrieveStudentDetails() {
        Toast.makeText(this, "" + studentId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+studentId, Toast.LENGTH_SHORT).show();
    }
}
