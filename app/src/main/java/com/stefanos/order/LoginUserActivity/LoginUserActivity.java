package com.stefanos.order.LoginUserActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.stefanos.order.AdministratorChoicesActivity.AdministratorChoicesActivity;
import com.stefanos.order.R;
import com.stefanos.order.SignUpUserActivity.SignUpUserActivity;
import com.stefanos.order.UserChoices.UserChoicesActivity;
import com.stefanos.order.Users.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class LoginUserActivity extends AppCompatActivity {

    private MaterialButton signUpButton;
    private MaterialButton loginButton;
    private EditText usernameEditText;
    private EditText passwordUserEditText;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private CheckBox radioButtonRemember;
    private Toolbar toolbar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);


        findViewByIdMethod();
        signUpButtonListener();
        loginButtonListener();
        setButtonRememberPassword();


    }


    private void findViewByIdMethod(){
        signUpButton=findViewById(R.id.signUpButtonXml);
        loginButton=findViewById(R.id.loginButtonXml);
        usernameEditText=findViewById(R.id.usernameEdit);
        passwordUserEditText=findViewById(R.id.passwordUserEdit);
        usernameTextInputLayout=findViewById(R.id.usernameXml);
        passwordTextInputLayout=findViewById(R.id.passwordTextXml);
//        forgetPasswordButton=findViewById(R.id.forgotPasswordButtonXml);
        toolbar=findViewById(R.id.toolbarLoginUser);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login_user_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private  void signUpButtonListener(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginUserActivity.this, SignUpUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginButtonListener(){

        usernameEditText.setText(getThePreferencesUser());
        passwordUserEditText.setText(getThePreferencesPassword());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(!usernameEditText.getText().toString().isEmpty()){
                String storeName=readPreferences();
                setPreferencesUsername();
                DocumentReference loginuser=db.collection("store").document(storeName).
                        collection("user").document(usernameEditText.getText().toString());
                loginuser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();

                            if (documentSnapshot.exists()){

                                Users user=new Users();
                                user.setUsername(documentSnapshot.getData().get("userName").toString());
                                user.setUserPassword(documentSnapshot.getData().get("userPassword").toString());
                                user.setAdministrator((Boolean) documentSnapshot.getData().get("administrator"));
                                user.setUser((Boolean) documentSnapshot.getData().get("user"));

                                if (user.getUsername().equals(usernameEditText.getText().toString()) &&
                                        user.getUserPassword().equals(passwordUserEditText.getText().toString())) {

                                    if (user.isAdministrator()){

                                        Intent intent = new Intent(LoginUserActivity.this, AdministratorChoicesActivity.class);
                                        startActivity(intent);
                                    }
                                    else {

                                        Intent intent= new Intent(LoginUserActivity.this, UserChoicesActivity.class);
                                        startActivity(intent);
                                    }

                                }else{

                                    passwordTextInputLayout.setError(getResources().getString(R.string.invalid_password_greek));
                                }
                            }

                        }else {
                            usernameTextInputLayout.setError(getResources().getString(R.string.username_does_not_exist_greek));
                        }
                    }
                });

            }
            else{
                  usernameTextInputLayout.setError(getResources().getString(R.string.username_does_not_exist_greek));
              }}
        });
    }

    private String readPreferences(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName=prefs.getString("name",null);

        return storeName;
    }



    private void setPreferencesUsername(){
        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.putString("username",usernameEditText.getText().toString());
        editor.apply();
    }
    private void setButtonRememberPassword(){



        radioButtonRemember=findViewById(R.id.rememberPasswordUser);
        radioButtonRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThePreferencesUser();
                setThePreferencesPassword();
            }
        });


    }




    private void setThePreferencesUser(){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("user");
        editor.apply();
        editor.putString("user",usernameEditText.getText().toString());
        editor.apply();
    }

    private String getThePreferencesUser(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String user= prefs.getString("user", null);

        return user;

    }

    private void setThePreferencesPassword(){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("userPassword");
        editor.apply();
        editor.putString("userPassword",passwordUserEditText.getText().toString());
        editor.apply();
    }

    private String getThePreferencesPassword(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String password = prefs.getString("userPassword", null);

        return password;

    }

}
