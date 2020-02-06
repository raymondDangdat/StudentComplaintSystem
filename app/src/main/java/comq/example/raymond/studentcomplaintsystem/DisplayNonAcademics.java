package comq.example.raymond.studentcomplaintsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class DisplayNonAcademics extends AppCompatActivity {
    private Toolbar displayNonAcademicsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_non_academics);


        //toolbar
        //initialize our toolBar
        displayNonAcademicsToolbar = findViewById(R.id.display_non_toolbar);
        setSupportActionBar(displayNonAcademicsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Non Academics Staff");
    }
}
