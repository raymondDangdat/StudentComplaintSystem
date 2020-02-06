package comq.example.raymond.studentcomplaintsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.studentcomplaintsystem.Interface.ItemClickListener;
import comq.example.raymond.studentcomplaintsystem.Model.OfficersModel;

public class AdminHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private RecyclerView recycler_officers;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference staff;

    private FirebaseRecyclerAdapter<OfficersModel, ViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        staff = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("staff");



        recycler_officers = findViewById(R.id.recycler_officers);
        recycler_officers.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_officers.setLayoutManager(layoutManager);







        mAuth = FirebaseAuth.getInstance();


        loadHods();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHome.this, AddOfficerActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            //display alert to choose sort type
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showSortDialog() {
        //Options to display
        String[] sortOptions = {"Head of Departments", "Faculty Deans", "Non Academics"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort According Rank:")
                .setIcon(R.drawable.ic_sort_black)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the which contains the index position of the selected item
                        if (which==0){
                            loadHods();
                        }else if (which==1){
                            loadDeans();
                        }else if (which==2){
                            loadNonAcademics();

                        }
                    }
                });
        builder.show();
    }

    private void loadNonAcademics() {
        staff = staff.child("non_academics");
        FirebaseRecyclerOptions<OfficersModel>options = new FirebaseRecyclerOptions.Builder<OfficersModel>()
                .setQuery(staff, OfficersModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<OfficersModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull OfficersModel model) {
                holder.faculty.setText("Department: " + model.getDepartment());
                holder.name.setText("Name: " + model.getName());
                holder.email.setText("Email: " + model.getEmail());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deans_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_officers.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadDeans() {
        staff = staff.child("hods");
        FirebaseRecyclerOptions<OfficersModel>options = new FirebaseRecyclerOptions.Builder<OfficersModel>()
                .setQuery(staff, OfficersModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<OfficersModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull OfficersModel model) {
                holder.faculty.setText("Faculty: " + model.getFaculty());
                holder.department.setText("Department: " + model.getDepartment());
                holder.name.setText("Name: " + model.getName());
                holder.email.setText("Email: " + model.getEmail());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deans_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_officers.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadHods() {
        staff = staff.child("hods");
        FirebaseRecyclerOptions<OfficersModel>options = new FirebaseRecyclerOptions.Builder<OfficersModel>()
                .setQuery(staff, OfficersModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<OfficersModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull OfficersModel model) {
                holder.faculty.setText("Faculty: " + model.getFaculty());
                holder.department.setText("Department: " + model.getDepartment());
                holder.name.setText("Name: " + model.getName());
                holder.email.setText("Email: " + model.getEmail());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_staff) {
            // Handle the camera action
            startActivity(new Intent(AdminHome.this, AddOfficerActivity.class));
        } else if (id == R.id.nav_complaints) {
            startActivity(new Intent(AdminHome.this, AregistryComplaints.class));
        } else if (id == R.id.nav_exit) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(AdminHome.this, LoginActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
