package com.estgp.map;

//import static com.estgp.map.MapsActivity.EXTRA_HOUSE_MAIN;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.estgp.map.Classe.House;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class HouseForm extends AppCompatActivity {

    public static final String EXTRA_HOUSE = "extra_form";
    private EditText etHouseOwner;
    private EditText limitDate;
    private Switch swDelivered;
    private Switch swSubmitted;
    private Button btnUpdateInformation;

    private DatePickerDialog datePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_form);

        etHouseOwner = findViewById(R.id.et_house_Owner);
        limitDate = findViewById(R.id.et_date_limit);
        swDelivered = findViewById(R.id.sw_delivered);
        swSubmitted = findViewById(R.id.sw_submitted);


        initDatePicker();

        //get the house from the intent
        Intent intent = getIntent();

        House house = (House) intent.getSerializableExtra(EXTRA_HOUSE);


        swSubmitted.setClickable(false); //set the switch to false

        //Server para verificar se o está delivered, se estiver, pode alterar para submetido, se não, não pode
        swDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swDelivered.isChecked()) {
                    swSubmitted.setClickable(true);
                } else  if (!swDelivered.isChecked() && swSubmitted.isChecked()) {
                    swSubmitted.toggle();
                    swSubmitted.setClickable(false);

                } else {
                    swSubmitted.setClickable(false);
                }
            }

        });

        btnUpdateInformation = findViewById(R.id.btn_update_info);

        btnUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Server para mostrar um Toast, se o campo nome estiver vazio
                if(etHouseOwner.getText().toString().isEmpty()){
                    Toast.makeText(v.getContext(),"please insert the name", Toast.LENGTH_LONG).show();
                    return;
                }

                //Server para mostrar um Toast, se o campo data estiver vazio
                if(limitDate.getText().toString().isEmpty()){
                    Toast.makeText(v.getContext(), "please insert the date", Toast.LENGTH_LONG).show();
                    return;
                }


                house.setHouseOwner(etHouseOwner.getText().toString().isEmpty() ? null : etHouseOwner.getText().toString());
                house.setDateLimit(limitDate.getText().toString().isEmpty() ? null : stringToLocalDate(limitDate.getText().toString()));
                house.setDeliveryStatus(swDelivered.isChecked());
                house.setSubmitted(swSubmitted.isChecked());

                //Log.e("HOUSE", "1-> " + house.toString());

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_HOUSE, house);

                setResult(RESULT_OK, replyIntent);
                finish();


            }
        });


        EditCensos();

    }


    //Serve de verificar se é add ou edit
    private void EditCensos() {


        Intent intent = getIntent();
        House house = (House) intent.getSerializableExtra(EXTRA_HOUSE);


        //Guardar os dados do house, ou seja, quando voltamos para editar fica guardado os dados
        if(house !=null) {
            etHouseOwner.setText(house.getHouseOwner() == null ? "" : house.getHouseOwner());
            limitDate.setText(house.getDateLimit() == null ? "" : localdateToString(house.getDateLimit()));
            swSubmitted.setChecked(house.getSubmitted());
            swDelivered.setChecked(house.getDeliveryStatus());
            swSubmitted.setShowText(true); //Mostra Texto no switch(ON-LIGADO E OFF PARA DESLIGADO)
            swDelivered.setShowText(true); //Mostra Texto no switch(ON-LIGADO E OFF PARA DESLIGADO)


        }

    }


    //Parte do Código relacionada a Data

    private LocalDate stringToLocalDate(String date){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    private String localdateToString(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if(date != null) {
            return date.format(formatter);
        }
        return "";
    }

    private void initDatePicker(){
        limitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                if(!limitDate.getText().toString().isEmpty()){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        calendar.setTime(sdf.parse(limitDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(HouseForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String checkValidDay = dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;
                        String checkValidMonth = month >= 10 ? String.valueOf(month+1) : "0" + (month+1);

                        limitDate.setText(checkValidDay + "/" + checkValidMonth + "/" + year);
                    }
                }, year, month, day);

                datePicker.show();
            }
        });
    }
}