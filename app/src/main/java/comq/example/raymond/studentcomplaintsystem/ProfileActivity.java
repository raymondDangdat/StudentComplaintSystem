package comq.example.raymond.studentcomplaintsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar profileToolBar;
    private EditText editTextName, editTextMatNo, editTextEmail, editTextFaculty, editTextPhone, editTextDepartment;
    private CircleImageView profilePix;

    private Button buttonUpdate;

    private FirebaseAuth mAuth;
    private DatabaseReference students;
    private StorageReference mStorageImage;
    private String uId;

    private Uri mImageUri = null;
    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgress = new ProgressDialog(this);

        //toolbar
        //initialize our toolBar
        profileToolBar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profileToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Details");

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        students = FirebaseDatabase.getInstance().getReference().child("complaintSystem").child("students");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("complaintSystem").child("profilePix");


        editTextEmail = findViewById(R.id.edt_email);
        editTextFaculty = findViewById(R.id.edt_faculty);
        editTextMatNo = findViewById(R.id.edt_mat_number);
        editTextDepartment = findViewById(R.id.edt_department);
        editTextName = findViewById(R.id.edt_full_name);
        editTextPhone = findViewById(R.id.edt_phone);
        profilePix = findViewById(R.id.profile_pix);

        buttonUpdate = findViewById(R.id.btn_update_profile);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfilePix();
            }
        });


        profilePix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePix();
            }
        });



        getUserDetails();

    }

    private void updateProfilePix() {
        if (mImageUri != null){
            mProgress.setMessage("Updating profile picture...");
            mProgress.show();
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String profile_url = taskSnapshot.getDownloadUrl().toString();

                    students.child(uId).child("profile_pix").setValue(profile_url).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgress.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProfileActivity.this, StudentHome.class));
                                    finish();
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadProfilePix() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void getUserDetails() {
        students.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name_retrieved = dataSnapshot.child(uId).child("name").getValue(String.class);
                final String email_retrieved = dataSnapshot.child(uId).child("email").getValue(String.class);
                final String phone_retrieved = dataSnapshot.child(uId).child("phone").getValue(String.class);
                final String department_retrieved = dataSnapshot.child(uId).child("department").getValue(String.class);
                final String faculty_retrieved = dataSnapshot.child(uId).child("faculty").getValue(String.class);
                final String mat_no_retrieved = dataSnapshot.child(uId).child("department").getValue(String.class);
                final String profile_pix_data = dataSnapshot.child(uId).child("profile_pix").getValue(String.class);

                editTextPhone.setText(phone_retrieved);
                editTextName.setText(name_retrieved);
                editTextEmail.setText(email_retrieved);
                editTextMatNo.setText(mat_no_retrieved);
                editTextDepartment.setText(department_retrieved);
                editTextFaculty.setText(faculty_retrieved);
                Picasso.get().load(profile_pix_data).placeholder(R.drawable.images).into(profilePix);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(4,4)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                profilePix.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
