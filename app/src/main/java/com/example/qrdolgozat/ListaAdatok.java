package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaAdatok extends AppCompatActivity {

    private EditText input_nev;
    private EditText input_jegy;

    private EditText input_id;
    private Button btn_modosit;
    private Button btn_megse;
    private ListView listView;
    private List<Person> people = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);
        init();

        RequestTask task = new RequestTask(getIntent().getStringExtra("url"), "GET");
        task.execute();

        btn_megse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaAdatok.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_modosit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });
    }

    public void init()
    {
        input_nev = findViewById(R.id.input_nev);
        input_jegy = findViewById(R.id.input_jegy);
        input_id = findViewById(R.id.input_id);
        btn_modosit = findViewById(R.id.btn_modosit);
        btn_megse = findViewById(R.id.btn_megse);
        listView = findViewById(R.id.ListView);
        listView.setAdapter(new PersonAdapter());
    }

    private void emberModositas() {
        String name = input_nev.getText().toString();
        String jegy = input_jegy.getText().toString();
        String idText = input_id.getText().toString();
        int id = Integer.parseInt(idText);
        Person person = new Person(id, name, jegy);
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(getIntent().getStringExtra("url") + "/" + id, "PUT", jsonConverter.toJson(person));
        task.execute();
    }

    private class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter() {
            super(ListaAdatok.this, R.layout.person_list_adapter, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);

            Person actualPerson = people.get(position);
            TextView textViewName = view.findViewById(R.id.out_nev);
            TextView textViewJegy = view.findViewById(R.id.out_jegy);
            TextView textViewModify = view.findViewById(R.id.TV_modositas);

            textViewName.setText(actualPerson.getName());
            textViewJegy.setText(actualPerson.getJegy());

            textViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input_id.setText(String.valueOf(actualPerson.getId()));
                    input_nev.setText(actualPerson.getName());
                    input_jegy.setText(actualPerson.getJegy());
                }
            });
            return view;
        }
    }


    private void urlapAlaphelyzetbe() {
        input_id.setText("");
        input_nev.setText("");
        input_jegy.setText("");
        RequestTask task = new RequestTask(getIntent().getStringExtra("url"), "GET");
        task.execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                }
            } catch (IOException e) {
                //Toast.makeText(ListaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            /*if (response.getResponseCode() >= 400) {
                Toast.makeText(ListaAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getResponseMessage());
            }*/
            switch (requestType) {
                case "GET":
                    Person[] peopleArray = converter.fromJson(response.getResponseMessage(), Person[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    Toast.makeText(ListaAdatok.this, "" + people.size(), Toast.LENGTH_SHORT).show();
                    break;
                case "PUT":
                    Person updatePerson = converter.fromJson(response.getResponseMessage(), Person.class);
                    people.replaceAll(person1 -> person1.getId() == updatePerson.getId() ? updatePerson : person1);
                    urlapAlaphelyzetbe();
                    break;
            }
        }
    }
}