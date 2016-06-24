package com.example.satchinc.nytimes;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.Calendar;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String begin_date = "-1";
    String sort = "";
    Boolean arts = false;
    Boolean fashion = false;
    Boolean sports = false;
    String query;
    @BindView(R.id.spSort)
    Spinner spSort;
    @BindView(R.id.btnPickDate)
    Button btnPickDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        query = getIntent().getStringExtra("query");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(adapter);
}

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.cbArts:
                if (checked) {
                    Toast.makeText(getApplicationContext(), "Arts Checked", Toast.LENGTH_SHORT).show();
                    arts = true;
                } else {
                    arts = false;

                }
                // Remove the meat
                break;
            case R.id.cbFashion:
                if (checked) {
                    Toast.makeText(getApplicationContext(), "Fashion Checked", Toast.LENGTH_SHORT).show();
                    fashion = true;
                } else {
                    fashion = false;

                }
                // Remove the meat
                break;
            case R.id.cbSports:
                if (checked) {
                    Toast.makeText(getApplicationContext(), "Sports Checked", Toast.LENGTH_SHORT).show();
                    sports = true;
                } else {
                    sports = false;

                }
                // Remove the meat
                break;
        }
    }

    public void onSubmit(View view) {
        Intent data = new Intent();
        data.putExtra("query", query);
        data.putExtra("fq", setupfq());
        data.putExtra("begin_date", begin_date);
        data.putExtra("sort", spSort.getSelectedItem().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public String setupfq() {
        String tempfq = "news_desk:(";
        if (arts) {
            if(!tempfq.equals("news_desk:(")){
                tempfq = tempfq + "%20";
            }
            tempfq = tempfq + "\"arts\"";
        }
        if (fashion) {
            if(!tempfq.equals("news_desk:(")){
                tempfq = tempfq + "%20";
            }
            tempfq = tempfq + "\"fashion\"";
        }
        if (sports) {
            if(!tempfq.equals("news_desk:(")){
                tempfq = tempfq + "%20";
            }
            tempfq = tempfq + "\"sports\"";
        }
        if (tempfq.equals("news_desk:(")) {
            tempfq = "-1";
        }
        else{
            tempfq = tempfq+ ")";
        }
        Log.d("fq", tempfq);
        return tempfq;
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        begin_date = getStringDate(c);
       btnPickDate.setText(Integer.toString(day) + " / " + Integer.toString((month+1)) + " / " + Integer.toString(year)  );
}
    public String getStringDate(Calendar c){
    String DATE_FORMAT = "yyyyMMdd";
    SimpleDateFormat sdf =
            new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(c.getTime());

    }

}



