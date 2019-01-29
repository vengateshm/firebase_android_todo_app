package com.android.firebasetodo;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bolts.Task;
import bolts.TaskCompletionSource;

public class TodoHelper {
    // Get latest messages
    public static Task<ArrayList<Todo>> getLatestTodos(int count) {
        final TaskCompletionSource<ArrayList<Todo>> tcs = new TaskCompletionSource<>();
        final ArrayList<Todo> messages = new ArrayList<>();

        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("todos")
                .orderByKey()
                .limitToLast(count);

        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Todo todo = snapshot.getValue(Todo.class);
                        if (todo != null) {
                            todo.setKey(snapshot.getKey());
                            messages.add(todo);
                        }
                    }
                }
                tcs.setResult(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setError(databaseError.toException());
            }
        });

        return tcs.getTask();
    }

    // Get old messages
    public static Task<ArrayList<Todo>> getOldTodos(final String beforeKey, int count) {
        final TaskCompletionSource<ArrayList<Todo>> tcs = new TaskCompletionSource<>();
        final ArrayList<Todo> messages = new ArrayList<>();

        Query ref = FirebaseDatabase.getInstance().getReference()
                .child("todos")
                .orderByKey()
                .endAt(beforeKey)
                .limitToLast(count + 1);
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Todo todo = snapshot.getValue(Todo.class);
                        if (todo != null) {
                            todo.setKey(snapshot.getKey());
                            if (!beforeKey.equals(todo.getKey())) {
                                messages.add(todo);
                            }
                        }
                    }
                }
                tcs.setResult(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tcs.setError(databaseError.toException());
            }
        });
        return tcs.getTask();
    }
}
