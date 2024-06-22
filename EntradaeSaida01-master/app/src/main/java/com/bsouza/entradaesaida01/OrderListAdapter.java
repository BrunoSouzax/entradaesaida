package com.bsouza.entradaesaida01;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderListAdapter(Context context, List<Order> orderList) {
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

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView unitTextView, nameTextView, dateTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            unitTextView = itemView.findViewById(R.id.textViewUnit);
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
        }
    }
}
