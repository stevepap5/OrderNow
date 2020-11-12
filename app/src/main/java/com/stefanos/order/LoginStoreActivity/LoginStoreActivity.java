package com.stefanos.order.LoginStoreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.crashlytics.internal.common.CrashlyticsReportDataCapture;
import com.google.protobuf.StringValue;
import com.stefanos.order.LoginUserActivity.LoginUserActivity;
import com.stefanos.order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginStoreActivity extends AppCompatActivity {

    private MaterialButton loginStoreButton;
    private TextInputLayout enterStoreNameTextLayoutLoginStoreActivity;
    private TextInputLayout enterPasswordTextLayoutLoginStoreActivity;
    private EditText enterStoreLoginStoreActivity;
    private EditText enterPasswordLoginStoreActivity;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Toolbar toolbar;
    private CheckBox buttonRememberPassword;
    private Button crashButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_store);

        findViewsMethod();
        setLoginStoreButtonMethod();
        setButtonRememberPassword();


    }

    private void findViewsMethod(){

        loginStoreButton=findViewById(R.id.loginStoreButtonXml);
        enterStoreLoginStoreActivity=findViewById(R.id.enterStoreEditText);
        enterPasswordLoginStoreActivity=findViewById(R.id.enterPasswordLoginEditText);
        enterStoreNameTextLayoutLoginStoreActivity=findViewById(R.id.storeNameTextLayoutXml);
        enterPasswordTextLayoutLoginStoreActivity=findViewById(R.id.passwordStoreTextXml);
        buttonRememberPassword=findViewById(R.id.rememberPassword);
        toolbar=findViewById(R.id.toolbarLoginStore);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.log_in_store_greek);


    }



    private void setLoginStoreButtonMethod(){
        setRememberedPassword();
        loginStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!enterStoreLoginStoreActivity.getText().toString().isEmpty()){

                DocumentReference documentReference = db.collection("store").
                        document(enterStoreLoginStoreActivity.getText().toString());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                StoreInfo storeInfo=new StoreInfo();
                                storeInfo.setNameStore(String.valueOf(document.get("nameStore")));
                                storeInfo.setPasswordStore(String.valueOf(document.get("passwordStore")));
                                storeInfo.setEditionStore(Boolean.parseBoolean(String.valueOf(document.get("edition"))));
                                setThePreferencesEdition(storeInfo.isEditionStore());

                                if (storeInfo.getNameStore().equals(enterStoreLoginStoreActivity.getText().toString()) &&
                                        storeInfo.getPasswordStore().equals(enterPasswordLoginStoreActivity.getText().toString())
                                ) {

                                    setThePreferences();

                                    Intent intent = new Intent(LoginStoreActivity.this, LoginUserActivity.class);
                                    startActivity(intent);
                                }else{

                                    enterPasswordTextLayoutLoginStoreActivity.setError(getResources().getString(R.string.invalid_password_greek));
                                }

                            }else {
                                enterStoreNameTextLayoutLoginStoreActivity.setError(getResources().getString(R.string.store_does_not_exist_greek));

                            }

                    }
                }
                });
            }
            else{
                    enterStoreNameTextLayoutLoginStoreActivity.setError(getResources().getString(R.string.store_does_not_exist_greek));
                }}
        });
    }

    private void setButtonRememberPassword(){

        buttonRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    setThePreferencesStore();
                    setThePreferencesPassword();

            }
        });

    }

    private void setRememberedPassword(){
        enterStoreLoginStoreActivity.setText(getThePreferencesStoreName());
        enterPasswordLoginStoreActivity.setText(getThePreferencesPassword());
    }


    private void setThePreferencesEdition(boolean edition){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("edition");
        editor.apply();
        editor.putBoolean("edition",edition);
        editor.apply();
    }


    private void setThePreferences(){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("name");
        editor.apply();
        editor.putString("name",enterStoreLoginStoreActivity.getText().toString());
        editor.apply();
    }

    private void setThePreferencesPassword(){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("storePassword");
        editor.apply();
        editor.putString("storePassword",enterPasswordLoginStoreActivity.getText().toString());
        editor.apply();
    }

    private String getThePreferencesPassword(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String password = prefs.getString("storePassword", null);

        return password;

    }

    private void setThePreferencesStore(){

        SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
        editor.remove("storeName");
        editor.apply();
        editor.putString("storeName",enterStoreLoginStoreActivity.getText().toString());
        editor.apply();
    }

    private String getThePreferencesStoreName(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName= prefs.getString("storeName", null);

        return storeName;

    }
}
