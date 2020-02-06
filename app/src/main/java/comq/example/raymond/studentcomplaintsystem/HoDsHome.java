package comq.example.raymond.studentcomplaintsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comq.example.raymond.Utils.InsuranceUtils;
import comq.example.raymond.studentcomplaintsystem.Interface.ItemClickListener;
import comq.example.raymond.studentcomplaintsystem.Model.ScomplaintModel;

public class HoDsHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private String department = "";
    private String uId = "";

    private TextView txt_department_name;

    private RecyclerView recycler_complaints;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference staff, complaints;

    private FirebaseRecyclerAdapter<ScomplaintModel, ViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ho_ds_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        uId   = mAuth.getCurrentUser().getUid();

        txt_department_name = findViewById(R.id.department_name);

        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff").child("hods");

        complaints = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("complaints").child("department");


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recycler_complaints = findViewById(R.id.recycler_complaints);
        recycler_complaints.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_complaints.setLayoutManager(layoutManager);

        loadHodInfo();

    }

    private void loadHodInfo() {
        staff.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                department = dataSnapshot.child("department").getValue(String.class);
                txt_department_name.setText(department);
                //Toast.makeText(DeansHome.this, ""+faculty, Toast.LENGTH_SHORT).show();

                loadComplaints(department);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadComplaints(String department) {


        FirebaseRecyclerOptions<ScomplaintModel> options = new FirebaseRecyclerOptions.Builder<ScomplaintModel>()
                .setQuery(complaints.orderByChild("department").equalTo(department), ScomplaintModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ScomplaintModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ScomplaintModel model) {
                holder.message.setText(model.getComplaint());
                holder.subject.setText(model.getSubject());
                holder.date.setText(InsuranceUtils.dateFromLong(model.getDate()));

                String status = model.getStatus().toString();

                if (status.equals("Sent")){
                    holder.receiver.setText("Received");
                }else if (status.equals("replied")){
                    holder.receiver.setText(model.getRespondent());
                }


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get complaint id to new activity
                        Intent complaintDetail = new Intent(HoDsHome.this, HodComplaintDetails.class);
                        complaintDetail.putExtra("complaintId", adapter.getRef(position).getKey());
                        startActivity(complaintDetail);

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.registry_complaints, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_complaints.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ho_ds_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_complaints) {
            // Handle the camera action
        } else if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_exit) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(HoDsHome.this, LoginActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
