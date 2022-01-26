package com.example.anonchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab; // кнопка добавления чата
    ListView lvMain; // список чатов
    final String CHAT = "chats"; // чаты в бд
    ArrayList<Chat> chatList; // сисок чатов
    ArrayList<String> chatNameList; // насвания чатов для адаптера
    public static DatabaseReference dbChats; // бд
    ArrayAdapter<String> adapter; // адаптер
    EditText etSearchChat; // поиск чата

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getChatList();
        Log.i(null, "lkllllllllllllllllllllllllllllllllllllllllllllllllllll " + (RegistrationAcitvity.manager.getDbUserNameList()));
        // добавление нового чата
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_add_title);
                View customLayout = getLayoutInflater().inflate(R.layout.dialog_add, null);
                builder.setView(customLayout);
                EditText etAdd = (EditText)customLayout.findViewById(R.id.etName);
                builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (etAdd.getText().toString().length() > 0) {
                            ArrayList<Message> messages = new ArrayList<>();
                            messages.add( new Message("Добро пожаловать в новый чат!", null, null));
                            dbChats.push().setValue(new Chat(etAdd.getText().toString(), getId(), " ", messages));
                            getChatList();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),R.string.no_name, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // выбор чата
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ChatAcitvity.class);
                intent.putExtra("id", chatList.get(i).id);
                intent.putExtra("userName", user.userName);
                intent.putExtra("userPassword", user.password);
                startActivity(intent);
                finish();
            }
        });

    }
    // инициализация
    void init(){
        dbChats = FirebaseDatabase.getInstance().getReference(CHAT);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        lvMain = (ListView) findViewById(R.id.lvMain);
        chatList = new ArrayList<>();
        chatNameList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatNameList);
        lvMain.setAdapter(adapter);
        user = new User();
        etSearchChat = (EditText) findViewById(R.id.etSearchChat);
        getUser();
    }
    // получение чатов из бд
    void getChatList(){
        dbChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (chatNameList.size() > 0) chatNameList.clear();
                if (chatList.size() > 0) chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat c = (Chat) ds.getValue(Chat.class);
                    assert c != null;
                    chatList.add(c);
                    chatNameList.add(c.name);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    // получение пользователя
    public void getUser(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            user.userName = bundle.getString("userName");
            user.password = bundle.getString("userPassword");
        }
    }

    // получение уникального id
    String getId(){
        int id = 0;
        for (int i = 0; i < chatList.size(); i++){
            int Id = Integer.parseInt(chatList.get(i).id) + 1;
            if (Id > id) {
                id = Id;
            }
        }
        return String.valueOf(id);
    }
    // Поиск чата
    public void onSearch(View view){
        Toast toast = Toast.makeText(getApplicationContext(),R.string.no_function, Toast.LENGTH_LONG);
        toast.show();
    }
    // открытие меню
    public void onOpenMenu(View view){
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.popup_main);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    // выход из аккаунта
                    case 0:
                        Intent intent = new Intent(MainActivity.this, RegistrationAcitvity.class);
                        RegistrationAcitvity.manager.clear();
                        startActivity(intent);
                        finish();
                }

                return false;
            }
        });
        popup.show();
    }

}














