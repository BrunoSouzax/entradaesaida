package com.bsouza.entradaesaida01;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private SearchView searchView;
    private OrderAdapter adapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        searchView = findViewById(R.id.searchView);
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(adapter);

        // Carregar encomendas do Firebase
        loadOrdersFromFirebase();

        // Configurar a funcionalidade de busca
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOrders(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchOrders(newText);
                return true;
            }
        });
    }

    private void loadOrdersFromFirebase() {
        Query query = FirebaseDatabase.getInstance().getReference().child("orders")
                .orderByChild("timestamp") // Supondo que você tenha um campo de timestamp em seus dados
                .limitToLast(50); // Ajuste conforme necessário

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(0, order); // Adicionando na posição 0
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrdersActivity.this, "Erro ao carregar encomendas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchOrders(String query) {
        String searchUnit = query.trim().toLowerCase();
        List<Order> filteredList = new ArrayList<>();

        for (Order order : orderList) {
            if (!TextUtils.isEmpty(order.getUnit()) && order.getUnit().toLowerCase().contains(searchUnit)) {
                filteredList.add(order);
            }
        }

        if (filteredList.isEmpty()) {
            // Se a lista filtrada estiver vazia, isso significa que não foram encontrados resultados
            Toast.makeText(this, "Nenhuma encomenda encontrada para a unidade: " + searchUnit, Toast.LENGTH_SHORT).show();
        } else {
            // Se foram encontrados resultados, iniciar a atividade de resultados de pesquisa
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("SEARCH_UNIT", searchUnit);
            startActivity(intent);
        }
    }

    public List<Order> getOrderList() {
        return orderList;
    }
}
