package com.android.firebasetodo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class TodoVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tvTodo, tvOptions, tvTime;

    private Todo todo;
    private TodoListAdapter.TodoListItemClickListener listener;

    public TodoVH(@NonNull View itemView) {
        super(itemView);
        initViews(itemView);
    }

    private void initViews(View itemView) {
        tvTodo = itemView.findViewById(R.id.tvTodo);
        tvOptions = itemView.findViewById(R.id.tvOptions);
        tvTime = itemView.findViewById(R.id.tvTime);

        tvOptions.setOnClickListener(this);
    }

    public void setTodoMessage(Todo todo, TodoListAdapter.TodoListItemClickListener listener) {
        this.todo = todo;
        this.listener = listener;

        if (todo.getTodo() != null && !todo.getTodo().isEmpty())
            tvTodo.setText(todo.getTodo());

        String date = String.valueOf(todo.getCreatedAt());
        long milliSec = Long.parseLong(date);
        String dateString = DateFormat.format("MMM dd hh:mm aaa", new Date(milliSec)).toString();

        tvTime.setText(dateString);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvOptions) {
            onOptionsClicked();
        }
    }

    private void onOptionsClicked() {
        if (listener != null) {
            listener.onOptionsClicked(todo, tvOptions);
        }
    }
}
