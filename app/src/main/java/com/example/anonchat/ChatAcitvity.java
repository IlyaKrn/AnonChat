package com.example.anonchat;

import static com.example.anonchat.MainActivity.dbChats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatAcitvity extends AppCompatActivity {
    TextView tvName;
    RecyclerView rvMain;
    EditText etSend;
    ArrayList<Message> messages;
    MessageListAdapter adapter;
    final String NULL_MESSAGE = "";
    LinearLayoutManager layoutManager;
    static ArrayList<Integer> selectedIds;
    String chatId;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitvity);
        init();
        getChatData();
        // установка отступа RecyclerView
        etSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                rvMain.setPadding(0,50,0, etSend.getHeight());
            }
        });
        // установка адаптера и слушатедей нажатий
        adapter = new MessageListAdapter(messages, new MessageListAdapter.OnStateClickListener() {
            // удаление сообщения
            @Override
            public void onStateClick(Message message, int position) {
                // добавление/удаление из списка для удаления
                if (isSelectedMessage(position))
                    deleteFromSelectedIds(position);
                else
                    selectedIds.add(position);
                // открытие/закрытие меню действий
                if (selectedIds.size() > 0){
                    findViewById(R.id.toolbarSelected).setVisibility(View.VISIBLE);
                    findViewById(R.id.toolbarMain).setVisibility(View.GONE);
                }
                else {
                    findViewById(R.id.toolbarSelected).setVisibility(View.GONE);
                    findViewById(R.id.toolbarMain).setVisibility(View.VISIBLE);
                }
            }
        });
        rvMain.setAdapter(adapter);
    }
    // иницализация
    void init(){
        tvName = (TextView)findViewById(R.id.tvName);
        rvMain = (RecyclerView) findViewById(R.id.rvMain);
        etSend = (EditText)findViewById(R.id.etSend);
        messages = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMain.setLayoutManager(layoutManager);
        chatId = getIntent().getExtras().getString("id");
        user = new User();
        selectedIds = new ArrayList<>();
        getUser();
    }
    // проверка наличия элемента в списке вабранных
    public static boolean isSelectedMessage(int id){
        for (Integer integer : selectedIds) {
            if (id == integer)
                return true;
        }
        return false;
    }
    // удаление элемента из списка вабранных
    void deleteFromSelectedIds(int id){
        for (int i = 0; i < selectedIds.size(); i++) {
            if (id == selectedIds.get(i)){
                selectedIds.remove(i);
                return;
            }
        }
    }
    // получение данных о чате из бд
    void getChatData(){
        dbChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = getIntent().getExtras().getString("id");
                    Chat c = ds.getValue(Chat.class);
                    assert c != null;
                    if (c.id.equals(id)) {
                        tvName.setText(c.name);
                        if (messages.size() > 0) messages.clear();
                        for (int i = 0; i < c.messages.size(); i++) {
                            messages.add(c.messages.get(i));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                rvMain.scrollToPosition(messages.size()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
    // запись сообщения в бд
    public void onSend(View view){
        if (etSend.getText().toString().length() > 0) {
            String massege = etSend.getText().toString();
            if (messages.get(0).equals(NULL_MESSAGE))
                messages.remove(0);
            messages.add(new Message(massege, user, null));
            dbChats.orderByChild("id").equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString("id")).getRef().child("messages").setValue(messages);
                        etSend.setText("");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    // получение пользоватея
    public void getUser(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            user.userName = bundle.getString("userName");
            user.password = bundle.getString("userPassword");
        }
    }
    // закрытие активности
    public void onClose(View view){
        Intent intent = new Intent(ChatAcitvity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    // установка фотографии в сообщение
    public void onSetImage(View view){
        Toast toast = Toast.makeText(getApplicationContext(),R.string.no_function, Toast.LENGTH_LONG);
        toast.show();
    }
    // открытие меню
    public void onChatMenuOpen(View view){
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.popup_chat);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    // активность с информацией о чате
                    case 0:
                        Intent intent = new Intent(ChatAcitvity.this, ChatInformationActivity.class);
                        intent.putExtra("id", chatId);
                        startActivity(intent);
                }
                return false;
            }
        });
        popup.show();
    }
    // удаление выбраных элементов
    public void onDeliteSelectedItems(View view){
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatAcitvity.this);
        builder.setTitle(R.string.delite_message_dialog_title);
        builder.setMessage(R.string.delite_message_dialog_subtitle);
        builder.setPositiveButton(R.string.delite, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for(int i = messages.size() - 1; i >= 0; i--) {
                    if (isSelectedMessage(i)) {
                        messages.remove(i);
                        Log.i(null, "delited" + i);
                    }
                }
                if (messages.size() == 0){
                    messages.add(new Message("", null, null));
                }
                dbChats.orderByChild("id").equalTo(getIntent().getExtras().getString("id")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString("id")).getRef().child("messages").setValue(messages);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                selectedIds.clear();
                findViewById(R.id.toolbarSelected).setVisibility(View.GONE);
                findViewById(R.id.toolbarMain).setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                selectedIds.clear();
                findViewById(R.id.toolbarSelected).setVisibility(View.GONE);
                findViewById(R.id.toolbarMain).setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.i(null, Arrays.toString(selectedIds.toArray()));
    }
}