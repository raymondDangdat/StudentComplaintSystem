package comq.example.raymond.studentcomplaintsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comq.example.raymond.studentcomplaintsystem.Interface.ItemClickListener;
import comq.example.raymond.studentcomplaintsystem.Model.OfficersModel;

public class DisplayDeans extends AppCompatActivity {
    private android.support.v7.widget.Toolbar displayToolBar;

    private FirebaseAuth mAuth;
    private String uId = "";

    private String faculty = "";


    private RecyclerView recycler_officers;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference students, staff;

    private FirebaseRecyclerAdapter<OfficersModel, ViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_deans);

        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff").child("hods");
        students = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");
        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getCurrentUser().getUid();



        recycler_officers = findViewById(R.id.recycler_display_deans);
        recycler_officers.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_officers.setLayoutManager(layoutManager);

        retrieveStudentFaculty();






        //toolbar
        //initialize our toolBar
        displayToolBar = findViewById(R.id.display_deans_toolbar);
        setSupportActionBar(displayToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Faculty HoDs");
    }

    private void retrieveStudentFaculty() {
        students.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String faculty = dataSnapshot.child("faculty").getValue(String.class);

                displayHods(faculty);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayHods(String faculty) {
        FirebaseRecyclerOptions<OfficersModel>options = new FirebaseRecyclerOptions.Builder<OfficersModel>()
                .setQuery(staff.orderByChild("faculty").equalTo(faculty), OfficersModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<OfficersModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull OfficersModel model) {
                holder.department.setText("Department: " + model.getDepartment());
                holder.name.setText("Name: " + model.getName());
                holder.email.setText("Email: " + model.getEmail());
                holder.faculty.setText("Faculty: " + model.getFaculty());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get hod id to new activity
                        Intent hodDetail = new Intent(DisplayDeans.this, ShodComplaint.class);
                        hodDetail.putExtra("hodId", adapter.getRef(position).getKey());
                        startActivity(hodDetail);


                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hods_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_officers.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView faculty, department, email, name;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            faculty = itemView.findViewById(R.id.txt_faculty);
            department = itemView.findViewById(R.id.txt_department);
            name = itemView.findViewById(R.id.txt_name);
            email = itemView.findViewById(R.id.txt_email);

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
