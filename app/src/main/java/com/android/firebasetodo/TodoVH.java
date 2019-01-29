package com.android.firebasetodo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class TodoVH extends RecyclerView.ViewHolder {

    private TextView tvTodo, tvTime;

    public TodoVH(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    private void initViews(View itemView) {
        tvTodo = itemView.findViewById(R.id.tvTodo);
        tvTime = itemView.findViewById(R.id.tvTime);
    }

    public void setTodoMessage(Todo todo) {
        if (todo.getTodo() != null && !todo.getTodo().isEmpty())
            tvTodo.setText(todo.getTodo());

        String date = String.valueOf(todo.getCreatedAt());
        long milliSec = Long.parseLong(date);
        String dateString = DateFormat.format("MMM dd hh:mm aaa", new Date(milliSec)).toString();

        tvTime.setText(dateString);
    }
}
