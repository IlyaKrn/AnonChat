package com.example.anonchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anonchat.SQLiteDatabase.SQLiteDbManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegistrationAcitvity extends AppCompatActivity {

    final String USER = "users"; // пользователи в бд
    ArrayList<User> userList;// список пользователей
    public static DatabaseReference dbUsers;
    public static SQLiteDbManager manager;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_acitvity);
        init();
        getUserList();
        getSavedAccount();
    }

    void init(){
        dbUsers = FirebaseDatabase.getInstance().getReference(USER);
        userList = new ArrayList<>();
        manager = new SQLiteDbManager(this);
        user = null;
    }
    // получение пользователей из бд
    void getUserList(){
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userList.size() > 0) userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User u = (User) ds.getValue(User.class);
                    assert u != null;
                    userList.add(u);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    // кнопка регистрация
    public void onRegistration(View view){
        registration();
    }
    // кнопка авторизация
    public void onAuthorization(View view){
        authorization();
    }
    // проверка налиия сохраненных аккаунтов
    public void getSavedAccount(){
        if (manager.getDbUserNameList().size() > 0){
            user = manager.getDbUserList().get(manager.getDbUserList().size()-1);
            Intent intent = new Intent(RegistrationAcitvity.this, MainActivity.class);
            intent.putExtra("userName", user.userName);
            intent.putExtra("userPassword", user.password);
            startActivity(intent);
            finish();
        }
    }




    // авторизация
    public void authorization(){
        AlertDialog.Builder builderAuth = new AlertDialog.Builder(RegistrationAcitvity.this);
        builderAuth.setTitle(R.string.authorization);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_authorization, null);
        builderAuth.setView(customLayout);
        EditText etUserName = (EditText)customLayout.findViewById(R.id.name_auth);
        EditText etUserPassword = (EditText)customLayout.findViewById(R.id.password_auth);
        // ВОЙТИ
        builderAuth.setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String n = etUserName.getText().toString();
                String p = etUserPassword.getText().toString();
                CheckBox cbAlwaysUse = (CheckBox) customLayout.findViewById(R.id.cbAlwaysUse);
                if (etUserName.getText().toString().length() > 0 && isRegistratedUser(n,p)) {
                    user = new User(n,p);
                    if (cbAlwaysUse.isChecked()){
                        pushAccountInSQLite(n,p);
                    }
                    Intent intent = new Intent(RegistrationAcitvity.this, MainActivity.class);
                    intent.putExtra("userName", user.userName);
                    intent.putExtra("userPassword", user.password);
                    startActivity(intent);
                    finish();
                }
                else if (isRegistratedName(n)){
                    Toast.makeText(RegistrationAcitvity.this, R.string.broken_password ,Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(RegistrationAcitvity.this, R.string.no_authorized, Toast.LENGTH_LONG).show();
                }
            }
        });
        // НАЗАД
        builderAuth.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });
        AlertDialog dialog = builderAuth.create();
        dialog.show();
    }
    // регистрация
    public void registration(){
        AlertDialog.Builder builderReg = new AlertDialog.Builder(RegistrationAcitvity.this);
        builderReg.setTitle(R.string.registrarion);
        builderReg.setMessage(R.string.registration_subtitle);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_registration, null);
        builderReg.setView(customLayout);
        EditText etUserNameReg = (EditText)customLayout.findViewById(R.id.name_reg);
        EditText etUserPasswordReg1 = (EditText)customLayout.findViewById(R.id.password_reg_1);
        EditText etUserPasswordReg2 = (EditText)customLayout.findViewById(R.id.password_reg_2);
        CheckBox cbAlwaysUse = (CheckBox) customLayout.findViewById(R.id.cbAlwaysUse);
        // ЗАРЕГИСТРИРОВАТЬСЯ
        builderReg.setPositiveButton(R.string.registrarion, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String n = etUserNameReg.getText().toString();
                String p1 = etUserPasswordReg1.getText().toString();
                String p2 = etUserPasswordReg2.getText().toString();
                if (p1.equals(p2) ) {
                    if (n.length() > 0) {
                        if (!isRegistratedName(n)) {
                            user = new User(n, p1);
                            dbUsers.push().setValue(user);
                            if (cbAlwaysUse.isChecked()){
                               pushAccountInSQLite(n, p1);
                            }
                            Intent intent = new Intent(RegistrationAcitvity.this, MainActivity.class);
                            intent.putExtra("userName", user.userName);
                            intent.putExtra("userPassword", user.password);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(RegistrationAcitvity.this, R.string.this_name_is_used, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        // НАЗАД
        builderReg.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog dialog = builderReg.create();
        dialog.show();
    }
    // проверка существования пользователя и пароля
    public boolean isRegistratedUser(String n, String p){
        for (int i = 0; i < userList.size(); i++){
            if (userList.get(i).userName.equals(n) && userList.get(i).password.equals(p)){
                return true;
            }
        }
        return false;
    }
    // проверка существования имени пользователя
    public boolean isRegistratedName(String n){
        for (int i = 0; i < userList.size(); i++){
            if (userList.get(i).userName.equals(n)){
                return true;
            }
        }
        return false;
    }
    // запись данных аккаунта в SQLite
    public void pushAccountInSQLite(String n, String p){
        manager.clear();
        manager.insertToDb(n, p);
    }
}
