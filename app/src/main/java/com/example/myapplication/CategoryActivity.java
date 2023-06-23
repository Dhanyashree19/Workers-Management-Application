package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toast.makeText(getApplicationContext(), "Welcome to WorkEase", Toast.LENGTH_SHORT).show();


        CardView agricultural=findViewById(R.id.agriculture);
        agricultural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity5.class);
                startActivity(intent);
            }
        });

        CardView vehicle=findViewById(R.id.vehicle_cleaning);
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity6.class);
                startActivity(intent);
            }
        });

        CardView forestry=findViewById(R.id.forestry);
        forestry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this, HomeActivity7.class);
                startActivity(intent);
            }
        });

        CardView construction=findViewById(R.id.construction);
        construction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity9.class);
                startActivity(intent);
            }
        });
        CardView mining=findViewById(R.id.naturalresource);
        mining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity8.class);
                startActivity(intent);
            }
        });
        CardView cooking=findViewById(R.id.cooking);
        cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        CardView cleaning=findViewById(R.id.cleaning);
        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, HomeActivity1.class);
                startActivity(intent);
            }
        });
        CardView electrician=findViewById(R.id.electrician);
        electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity2.class);
                startActivity(intent);
            }
        });

        CardView plumber=findViewById(R.id.plumber);
        plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity3.class);
                startActivity(intent);
            }
        });

        CardView painter=findViewById(R.id.painter);
        painter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CategoryActivity.this,HomeActivity4.class);
                startActivity(intent);
            }

        });


    }
}