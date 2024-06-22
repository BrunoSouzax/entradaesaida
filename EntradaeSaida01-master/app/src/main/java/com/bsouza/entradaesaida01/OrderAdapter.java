package com.bsouza.entradaesaida01;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.unitTextView.setText(order.getUnit());
        holder.nameTextView.setText(order.getName());
        holder.dateTextView.setText(order.getDate());
        holder.statusTextView.setText(order.getStatus());

        // Define a cor de fundo com base no status
        if ("requisitada".equals(order.getStatus()) || "pendente".equals(order.getStatus())) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
        } else if ("entregue".equals(order.getStatus())) {
            holder.itemView.setBackgroundColor(Color.GREEN);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("ORDER", order);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged(); // Notifica o RecyclerView sobre as mudanças na lista
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView unitTextView, nameTextView, dateTextView, statusTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            unitTextView = itemView.findViewById(R.id.textViewUnit);
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            statusTextView = itemView.findViewById(R.id.textViewStatus); // Adiciona o TextView para status
        }
    }
}
