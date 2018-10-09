package qrtestapp.testapp.com.flatreportproject;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class AddBuilding extends AppCompatActivity implements View.OnClickListener {

    Button addBuildBtn;
    RadioGroup radioType;
    RadioButton radioTypeButton;
    EditText nameBuilding;
    EditText address;
    EditText noFloor;
    BuildingReportDbHelper brdb;
    String radiovalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Building"); // hide the title bar

        radioType = (RadioGroup) findViewById(R.id.radio_type);
        addBuildBtn = (Button) findViewById(R.id.addBuildingBtn);

        // find the radiobutton by returned id
        int selectedId = radioType.getCheckedRadioButtonId();
        radioTypeButton = (RadioButton) findViewById(selectedId);
        radiovalue = radioTypeButton.getText().toString();
        //radioTypeButton = (RadioButton) findViewById(selectedId);
        nameBuilding = (EditText)findViewById(R.id.nameBuilding);
        noFloor = (EditText)findViewById(R.id.editNoFloor);
        address = (EditText)findViewById(R.id.address);

        brdb = new BuildingReportDbHelper(this);

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = radioType.getCheckedRadioButtonId();
                radioTypeButton = (RadioButton) findViewById(selectedId);
                radiovalue = radioTypeButton.getText().toString();

                if(radiovalue.equals("Penthouse")){
                    noFloor.setVisibility(View.VISIBLE);
                }
                else{
                    noFloor.setVisibility(View.INVISIBLE);

                }
            }
        });

        addBuildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = noFloor.getText().toString();
                String valn;
                noFloor = (EditText)findViewById(R.id.editNoFloor);
                Context context=getApplicationContext();

                try {
                    // Execute insert function
                    if(radiovalue.equals("Penthouse")) { valn = val; }
                    else{ valn="0"; }
                    if(nameBuilding.getText().toString().equals("")){
                        Toast.makeText(context, "Error inserting record please give name of the building", Toast.LENGTH_SHORT).show();
                        nameBuilding.requestFocus();
                    }
                    else {
                        long result = brdb.insertBuildingData(nameBuilding.getText().toString()
                                , Integer.parseInt(valn), address.getText().toString(), radiovalue);

                        Intent intent1 = new Intent(getApplicationContext(), ListBuilding.class);
                        startActivity(intent1);
                    }
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(context, "Error inserting record please give number of floors", Toast.LENGTH_SHORT).show();
                    noFloor.requestFocus();
                } catch (Exception e) {
                    // Just in case the above doesn't catch it
                    Toast.makeText(context, "Error inserting record please give number of floors", Toast.LENGTH_SHORT).show();
                    noFloor.requestFocus();

                }


            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}

