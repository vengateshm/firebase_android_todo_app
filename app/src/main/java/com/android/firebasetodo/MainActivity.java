package com.android.firebasetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;

import bolts.Continuation;
import bolts.Task;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TodoListAdapter.TodoListItemClickListener {

    private FloatingActionButton fabAdd;
    private RecyclerView rvTodoList;
    private TextView tvNoData;
    private LinearLayout llProgress;

    private Todo topTodo = new Todo();
    private Todo bottomTodo = new Todo();
    private TodoListAdapter adapter;
    private Query newTodoTrackerQuery;
    private ArrayList<Todo> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initViews();
        setRecyclerView();

        loadLatestMessages();
    }

    private void initViews() {
        fabAdd = findViewById(R.id.fabAdd);
        rvTodoList = findViewById(R.id.rvTodoList);
        tvNoData = findViewById(R.id.tvNoData);
        llProgress = findViewById(R.id.llProgress);

        fabAdd.setOnClickListener(this);
    }

    private void setRecyclerView() {
        adapter = new TodoListAdapter();
        adapter.setListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvTodoList.setLayoutManager(llm);
        rvTodoList.setAdapter(adapter);
    }

    private void loadLatestMessages() {
        showProgress();
        TodoHelper.getLatestTodos(10)
                .onSuccess(new Continuation<ArrayList<Todo>, Object>() {
                    @Override
                    public Object then(Task<ArrayList<Todo>> task) {
                        hideProgress();
                        if (task.isCancelled() || task.isFaulted()) {

                        } else {
                            onLatestMessagesSuccess(task.getResult());
                        }
                        return null;
                    }
                });
    }

    private void onLatestMessagesSuccess(ArrayList<Todo> chatMessages) {
        if (chatMessages != null && !chatMessages.isEmpty()) {
            setMessages();

            todoList.clear();
            todoList.addAll(chatMessages);

            if (adapter != null) {
                if (todoList.size() > 0) {
                    topTodo = todoList.get(0);
                    bottomTodo = todoList.get(todoList.size() - 1);
                }

                Collections.reverse(todoList);

                if (todoList.size() < 10) {
                    adapter.setTodoList(todoList);
                }
                if (todoList.size() >= 10) {
                    adapter.setTodoList(todoList);
                    adapter.addFooter();
                }

                adapter.notifyDataSetChanged();
                rvTodoList.scrollToPosition(0);
            }
        } else {
            setNoMessages();
        }
        startTrackingNewMessages();
    }

    private void startTrackingNewMessages() {
        if (newTodoTrackerQuery != null) {
            newTodoTrackerQuery.removeEventListener(todoChildEventListener);
        }
        newTodoTrackerQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child("todos")
                .orderByKey()
                .startAt(bottomTodo.getKey());
        newTodoTrackerQuery.addChildEventListener(todoChildEventListener);
    }

    private ChildEventListener todoChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Todo message = dataSnapshot.getValue(Todo.class);
            if (message != null) {
                message.setKey(dataSnapshot.getKey());

                if (todoList.isEmpty()) {
                    setMessages();
                }
                if (!todoList.contains(message)) {
                    todoList.add(0, message);
                    if (adapter != null) {
                        if (adapter.getItemCount() == 0) {
                            // Check count if no data set in adapter previously
                            // means no updates an user enters first update
                            adapter.setTodoList(todoList);
                        }
                        adapter.notifyItemInserted(0);
                        rvTodoList.scrollToPosition(0);
                    }
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onLoadMore() {
        loadOldTodos();
    }

    private void loadOldTodos() {
        TodoHelper.getOldTodos(topTodo.getKey(), 10)
                .onSuccess(new Continuation<ArrayList<Todo>, Object>() {
                    @Override
                    public Object then(Task<ArrayList<Todo>> task) {
                        hideLoadMoreProgress();

                        if (task.isCancelled() || task.isFaulted()) {

                        } else {
                            onOldMessagesSuccess(task.getResult());
                        }
                        return null;
                    }
                });
    }

    private void onOldMessagesSuccess(ArrayList<Todo> chatMessages) {
        if (chatMessages != null && !chatMessages.isEmpty()) {
            // Reverse
            Collections.reverse(chatMessages);
            topTodo = chatMessages.get(chatMessages.size() - 1);
            if (adapter != null) {
                adapter.removeFooter();
                adapter.addAll(chatMessages);
                adapter.addFooter();
            }
        } else {
            // If no data then its last page
            if (adapter != null) {
                adapter.removeFooter();
            }
        }
    }

    private void showLoadMoreProgress() {
        if (adapter != null) {
            adapter.showLoadMoreProgress();
        }
    }

    private void hideLoadMoreProgress() {
        if (adapter != null) {
            adapter.hideLoadMoreProgress();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabAdd) {
            onFabAddBtnClicked();
        }
    }

    private void onFabAddBtnClicked() {
        Intent intent = new Intent(MainActivity.this, CreateTodoActivity.class);
        startActivity(intent);
    }

    private void setMessages() {
        tvNoData.setVisibility(GONE);
        rvTodoList.setVisibility(VISIBLE);
    }

    private void setNoMessages() {
        tvNoData.setVisibility(VISIBLE);
        rvTodoList.setVisibility(GONE);
    }

    private void showProgress() {
        llProgress.setVisibility(VISIBLE);
    }

    private void hideProgress() {
        llProgress.setVisibility(GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forgot to remove any listener if any
        if (newTodoTrackerQuery != null && todoChildEventListener != null) {
            newTodoTrackerQuery.removeEventListener(todoChildEventListener);
        }
    }
}
