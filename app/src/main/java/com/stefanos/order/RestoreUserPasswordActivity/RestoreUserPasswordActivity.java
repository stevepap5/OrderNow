package com.stefanos.order.RestoreUserPasswordActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stefanos.order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RestoreUserPasswordActivity extends AppCompatActivity {

    private TextInputLayout storeNameTextInputLayout;
    private TextInputLayout emailUserTextInputLayout;
    private EditText storeNameEditText;
    private EditText emailUserEditText;
    private Toolbar toolbar;
    private MaterialButton buttonRestorePassword;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_user_password);

        findViewsById();
        setButtonRestorePasswordMethod();
    }

    private void findViewsById(){
        storeNameTextInputLayout=findViewById(R.id.giveNameOfStoreTextLayoutXml);
        storeNameEditText=findViewById(R.id.giveStoreEditText);
        emailUserTextInputLayout=findViewById(R.id.emailUserTextXml);
        emailUserEditText=findViewById(R.id.emailUserEditText);
        toolbar=findViewById(R.id.toolbarRestoreUserPassword);
        buttonRestorePassword=findViewById(R.id.restoreButtonXml);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.restore_user_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setButtonRestorePasswordMethod(){

       buttonRestorePassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(!storeNameEditText.getText().toString().trim().isEmpty()&&
               !emailUserEditText.getText().toString().trim().isEmpty()){
                   final String storeName=readPreferences();
                   DocumentReference documentReference=db.collection("store").document(storeName);
                   documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                           if(task.isSuccessful()){
                               DocumentSnapshot documentSnapshot=task.getResult();
                               if(documentSnapshot.exists()){
                                   CollectionReference particularUser = db.collection("store").document(storeName).collection("user");
                                   particularUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                           if(task.isSuccessful()){
                                               if (task.getResult()!=null) {
                                                   for (DocumentSnapshot documentSnapshot1:task.getResult()){
                                                        if(documentSnapshot1.get("userMail").equals(emailUserEditText.getText().toString().trim())){
                                                            Toast.makeText(RestoreUserPasswordActivity.this,"I found it",Toast.LENGTH_LONG).show();
                                                            Intent emailIntent=new Intent(Intent.ACTION_SEND);

                                                            emailIntent.setType("message/rfc822");
                                                            emailIntent.putExtra(Intent.EXTRA_EMAIL,emailUserEditText.getText().toString().trim());
                                                            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Your user password");
                                                            emailIntent.putExtra(Intent.EXTRA_TEXT,"Your username");


                                                                startActivity(Intent.createChooser(emailIntent,"Send to..."));
                                                                finish();

                                                        }
                                                   }
                                               }
                                           }
                                       }
                                   });
                               }
                           }
                           else {
                               storeNameTextInputLayout.setError(getResources().getString(R.string.store_does_not_exist));
                           }
                       }
                   });

               }
           }
       });
    }

    private String readPreferences(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName=prefs.getString("name",null);

        return storeName;
    }


}
