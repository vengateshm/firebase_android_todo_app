package com.android.firebasetodo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private List<Todo> todoList;
    private boolean isFooterRequired;
    private boolean showLoadMoreProgress = false;
    private TodoListItemClickListener listener;

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
    }

    public void setListener(TodoListItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        switch (viewType) {
            case TYPE_ITEM:
                View item = inflater.inflate(R.layout.todo_list_item, parent, false);
                return new TodoVH(item);
            case TYPE_FOOTER:
                View footer = inflater.inflate(R.layout.load_more_footer_item, parent, false);
                return new LoadMoreFooterVH(footer);
            default:
                throw new RuntimeException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case TYPE_ITEM:
                ((TodoVH) holder).setTodoMessage(todoList.get(position), listener);
                break;
            case TYPE_FOOTER:
                onLoadMore(holder);
                ((LoadMoreFooterVH) holder).showLoadMoreProgress(showLoadMoreProgress);
                break;
            default:
                throw new RuntimeException("Invalid view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == todoList.size() - 1 && isFooterRequired) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return todoList == null ? 0 : todoList.size();
    }

    private void onLoadMore(RecyclerView.ViewHolder holder) {
        if (((LoadMoreFooterVH) holder).getLoadMoreLayout() != null) {
            ((LoadMoreFooterVH) holder).getLoadMoreLayout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onLoadMore();
                    }
                }
            });
        }
    }

    public void addFooter() {
        isFooterRequired = true;
        add(new Todo());
    }

    private void add(Todo chatMessage) {
        if (todoList != null) {
            todoList.add(chatMessage);
            notifyItemInserted(todoList.size() - 1);
        }
    }

    public void addAll(List<Todo> items) {
        for (Todo item : items) {
            add(item);
        }
    }

    public void removeFooter() {
        isFooterRequired = false;
        removeUserIssue();
    }

    private void removeUserIssue() {
        int position = todoList.size() - 1;
        Todo item = getItem(position);

        if (item != null) {
            todoList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Todo getItem(int position) {
        if (!todoList.isEmpty()) {
            return todoList.get(position);
        } else {
            return null;
        }
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    public void showLoadMoreProgress() {
        showLoadMoreProgress = true;
        notifyItemChanged(todoList.size() - 1);
    }

    public void hideLoadMoreProgress() {
        showLoadMoreProgress = false;
        notifyItemChanged(todoList.size() - 1);
    }

    public interface TodoListItemClickListener {
        void onLoadMore();

        void onOptionsClicked(Todo todo, View view);
    }
}
