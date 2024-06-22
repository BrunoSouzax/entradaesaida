package com.bsouza.entradaesaida01;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.recyclerView);
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Receber os dados da unidade selecionada da intent
        String searchUnit = getIntent().getStringExtra("SEARCH_UNIT");

        // Consultar o banco de dados Firebase
        searchOrdersByUnit(searchUnit);
    }

    private void searchOrdersByUnit(String searchUnit) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.orderByChild("unit").equalTo(searchUnit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchResultsActivity.this, "Erro ao buscar dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
