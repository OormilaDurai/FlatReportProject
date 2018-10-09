package qrtestapp.testapp.com.flatreportproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static qrtestapp.testapp.com.flatreportproject.R.*;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.flatreportproject.MESSAGE";
    EditText inspName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        getSupportActionBar().hide();

        inspName = (EditText)findViewById(R.id.name);

        Button contBtn =  (Button)findViewById(id.cntnuBtn);
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inspName.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter the inspectors name", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ListBuilding.class);
                    String name = inspName.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, name);
                    startActivity(intent);
                }
            }
        });
    }
}
