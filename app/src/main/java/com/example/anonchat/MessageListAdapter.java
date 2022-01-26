package com.example.anonchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageHolder> {
    private final OnStateClickListener onClickListener;
    ArrayList<Message> messages;
    public MessageListAdapter(ArrayList<Message> messages, OnStateClickListener onClickListener) {
        this.messages = messages;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.message_list_item, parent, false);

        MessageHolder holder = new MessageHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);

        Message message = messages.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (ChatAcitvity.isSelectedMessage(position))
                    view.findViewById(R.id.parent_layout).setBackground(view.getResources().getDrawable(R.drawable.round_corner));
                if (!ChatAcitvity.isSelectedMessage(position))
                    view.findViewById(R.id.parent_layout).setBackground(view.getResources().getDrawable(R.drawable.round_corner_selected));


                // вызываем метод слушателя, передавая ему данные
                onClickListener.onStateClick(message, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }







    interface OnStateClickListener{
        void onStateClick(Message message, int position);
    }


    class MessageHolder extends RecyclerView.ViewHolder{

        TextView tvMessage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.textView);
        }

        void bind(int listIndex){

            if (ChatAcitvity.isSelectedMessage(listIndex))
                itemView.findViewById(R.id.parent_layout).setBackground(itemView.getResources().getDrawable(R.drawable.round_corner_selected));
            if (!ChatAcitvity.isSelectedMessage(listIndex))
                itemView.findViewById(R.id.parent_layout).setBackground(itemView.getResources().getDrawable(R.drawable.round_corner));

            tvMessage.setText(messages.get(listIndex).message);
            if (tvMessage.getText().length() < 1){
                itemView.setVisibility(View.GONE);
                itemView.setClickable(false);
            }
            if (tvMessage.getText().length() > 0){
                itemView.setVisibility(View.VISIBLE);
                itemView.setClickable(true);
            }
        }
    }
}

