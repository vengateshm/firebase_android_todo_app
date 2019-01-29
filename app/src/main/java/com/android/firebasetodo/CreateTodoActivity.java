package com.android.firebasetodo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class CreateTodoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etTodo;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);

        initViews();
    }

    private void initViews() {
        etTodo = findViewById(R.id.etTodo);
        bSave = findViewById(R.id.bSave);

        bSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSave) {
            onSaveBtnClicked();
        }
    }

    private void onSaveBtnClicked() {
        String todo = etTodo.getText().toString().trim();
        if (todo.isEmpty()) {
            etTodo.setError("Field Required");
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("todos");
        String todoKey = ref.push().getKey();
        Todo todoObj = new Todo();
        todoObj.setTodo(todo);
        todoObj.setCreatedAt(new Date().getTime());
        if (todoKey != null && !todoKey.isEmpty())
            ref.child(todoKey).setValue(todoObj);

        finish();
    }
}
