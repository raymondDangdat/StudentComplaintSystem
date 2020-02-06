package comq.example.raymond.studentcomplaintsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.Utils.InsuranceUtils;
import comq.example.raymond.studentcomplaintsystem.Interface.ItemClickListener;
import comq.example.raymond.studentcomplaintsystem.Model.ScomplaintModel;

public class DisplayDepartmentCOmplaints extends AppCompatActivity {
    private Toolbar complaintToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference user, complaints;

    private FloatingActionButton fab;

    private RecyclerView recycler_complaints;
    RecyclerView.LayoutManager layoutManager;

    private String uId ="";

    private FirebaseRecyclerAdapter<ScomplaintModel, ViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_department_complaints);

        fab = findViewById(R.id.fab);


        mAuth = FirebaseAuth.getInstance();
        user = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");
        complaints = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("complaints").child("department");



        uId = mAuth.getCurrentUser().getUid();




        //toolbar
        //initialize our toolBar
        complaintToolbar = findViewById(R.id.department_complaint_toolbar);
        setSupportActionBar(complaintToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Department Complaints");

        recycler_complaints = findViewById(R.id.recycler_complaints);
        recycler_complaints.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_complaints.setLayoutManager(layoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayDepartmentCOmplaints.this, DisplayDeans.class));
            }
        });

        loadComplaints();
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
