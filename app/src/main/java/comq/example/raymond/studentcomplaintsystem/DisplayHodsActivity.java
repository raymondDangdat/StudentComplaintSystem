package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import comq.example.raymond.Utils.InsuranceUtils;
import comq.example.raymond.studentcomplaintsystem.Interface.ItemClickListener;
import comq.example.raymond.studentcomplaintsystem.Model.ScomplaintModel;

public class DisplayHodsActivity extends AppCompatActivity {
    private Toolbar displayHodsToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference user, complaints;

    private FloatingActionButton fab;
    private ProgressDialog dialog;

    private RecyclerView recycler_complaints;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<ScomplaintModel, ViewHolder>adapter;

    private String name, matNo, faculty, department, respondent, phone, level = "";



    private ScomplaintModel newComplaint;

    private String uId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hods);


        //toolbar
        //initialize our toolBar
        displayHodsToolBar = findViewById(R.id.faculty_complaint_toolbar);
        setSupportActionBar(displayHodsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Faculty Complaint");

        dialog = new ProgressDialog(this);

        fab = findViewById(R.id.fab);

        recycler_complaints = findViewById(R.id.recycler_faculty_complaint);
        recycler_complaints.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_complaints.setLayoutManager(layoutManager);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");
        complaints = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("complaints").child("faculty");



        uId = mAuth.getCurrentUser().getUid();


        getStudentDetails();

        loadComplaints();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaint_dialog();
            }
        });

    }


    private void loadComplaints() {
        FirebaseRecyclerOptions<ScomplaintModel> options = new FirebaseRecyclerOptions.Builder<ScomplaintModel>()
                .setQuery(complaints.orderByChild("uId").equalTo(uId), ScomplaintModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ScomplaintModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ScomplaintModel model) {
                holder.message.setText(model.getComplaint());
                holder.subject.setText(model.getSubject());
                holder.date.setText(InsuranceUtils.dateFromLong(model.getDate()));

                String status = model.getStatus().toString();

                if (status.equals("Sent")){
                    holder.receiver.setText(model.getStatus());
                }else if (status.equals("Replied")){
                    holder.receiver.setText("Registry Replied");
                }


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.complaint_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_complaints.setAdapter(adapter);
        adapter.startListening();
    }

    private void complaint_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayHodsActivity.this);
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
                    Toast.makeText(DisplayHodsActivity.this, "Please type the subject of your complaint", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(complaint)){
                    Toast.makeText(DisplayHodsActivity.this, "Please type your complaint", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setMessage("Sending Complaint...");
                    dialog.show();
                    newComplaint = new ScomplaintModel(name, uId, matNo, faculty, department, subject, complaint,faculty, phone, level, status, complaintDate);
                    complaints.push().setValue(newComplaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText(DisplayHodsActivity.this, "Complaint sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DisplayHodsActivity.this, DisplayHodsActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(DisplayHodsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView subject, message, receiver, date;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subject = itemView.findViewById(R.id.txt_subject);
            message = itemView.findViewById(R.id.txt_message);
            date = itemView.findViewById(R.id.txt_date);
            receiver = itemView.findViewById(R.id.txt_receiver);

            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
