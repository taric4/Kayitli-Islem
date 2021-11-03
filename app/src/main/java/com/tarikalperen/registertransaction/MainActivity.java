package com.tarikalperen.registertransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tarikalperen.registertransaction.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    ArrayList<CorporateClass> corporateClassArrayList;
    RegisterAdapter registerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        corporateClassArrayList = new ArrayList<>();

        binding.recylerView.setLayoutManager(new LinearLayoutManager(this));
        registerAdapter = new RegisterAdapter(corporateClassArrayList);
        binding.recylerView.setAdapter(registerAdapter);

        getData();
    }

    private void getData(){

        try {

            SQLiteDatabase database = this.openOrCreateDatabase("Register", MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM register",null);
            int corporateIx = cursor.getColumnIndex("corporate");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String corporate = cursor.getString(corporateIx);
                int id = cursor.getInt(idIx);
                CorporateClass corporateClass = new CorporateClass(corporate,id);
                corporateClassArrayList.add(corporateClass);

            }

            registerAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.register_menu,menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.add_transaction){
            Intent intent = new Intent(this,DetailsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}