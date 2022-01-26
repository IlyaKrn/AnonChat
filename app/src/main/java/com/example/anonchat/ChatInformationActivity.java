package com.example.anonchat;

import static com.example.anonchat.MainActivity.dbChats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatInformationActivity extends AppCompatActivity {

    TextView tvChatName, tvDescription, tvMemberCount, tvMessagesCount;
    String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_information);
        init();
        getChatData();
    }
    // инициализация
    void init(){
        chatId = getIntent().getExtras().getString("id");
        tvChatName = findViewById(R.id.tvChatName);
        tvDescription = findViewById(R.id.tvDescription);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvMessagesCount = findViewById(R.id.tvMessagesCount);
    }
    // получение данных о чате из бд
    void getChatData(){
        dbChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat c = (Chat) ds.getValue(Chat.class);
                    assert c != null;
                    if (c.id.equals(chatId)) {
                        Log.i(null, c.name + " " +  c.description + " " +  String.valueOf(c.messages.size()) + " " +  getMemberCount(c));
                        if (!c.messages.get(0).message.equals(""))
                            setValues(c.name, c.description, "Сообщений:\n" + String.valueOf(c.messages.size()), "Пользователей:\n" + getMemberCount(c));
                        else
                            setValues(c.name, c.description, "Сообщений:\n" + String.valueOf(c.messages.size()-1), "Пользователей:\n" + getMemberCount(c));

                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    // установка значений
    void setValues(String n, String d, String mess, String memb){
        tvChatName.setText(n);
        tvDescription.setText(d);
        tvMessagesCount.setText(mess);
        tvMemberCount.setText(memb);
    }
    // получение колличества пользователейб оставивших сообщение
    String getMemberCount(Chat c){
        int count = 0;
        ArrayList<User> memberNames = new ArrayList<>();
        for (Message message : c.messages){
            if (message.user != null) {
                boolean isUsed = false;
                for (User user : memberNames) {
                    if (message.user.equals(user)) {
                        isUsed = true;
                        break;
                    }
                }
                if (!isUsed){
                    memberNames.add(message.user);
                    count++;
                }
            }
        }
        return String.valueOf(count);
    }

    // закрытие активности
    public void onCloseInfo(View view){
        Intent intent = new Intent(ChatInformationActivity.this, ChatAcitvity.class);
        finish();
    }
    // изменение имени
    public void onRefactorName(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.refactor);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_refactoring, null);
        builder.setView(customLayout);
        EditText etRefact = (EditText)customLayout.findViewById(R.id.etRefactor);
        etRefact.setText(tvChatName.getText().toString());
        builder.setPositiveButton(R.string.refactor, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbChats.orderByChild("id").equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (etRefact.getText().toString().length() > 0) {

                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString(chatId)).getRef().child("name").setValue(etRefact.getText().toString());
                            }
                            else {
                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString(chatId)).getRef().child("name").setValue(" ");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // изменение описания
    public void onRefactorDescription(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.refactor);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_refactoring, null);
        builder.setView(customLayout);
        EditText etRefact = (EditText)customLayout.findViewById(R.id.etRefactor);
        etRefact.setText(tvDescription.getText().toString());
        builder.setPositiveButton(R.string.refactor, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbChats.orderByChild("id").equalTo(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (etRefact.getText().toString().length() > 0) {
                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString(chatId)).getRef().child("description").setValue(etRefact.getText().toString());
                            }
                            else {
                                child.getRef().orderByChild("id").equalTo(getIntent().getExtras().getString(chatId)).getRef().child("description").setValue(" ");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}