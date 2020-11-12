package com.stefanos.order.TableIndividualActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stefanos.order.MenuCategoryFromTableActivity.MenuCategoryAccessFromTableActivity;
import com.stefanos.order.OrderItem.OrderItem;
import com.stefanos.order.PrintersActivity.MyPrintDocumentAdapter;
import com.stefanos.order.PrintersActivity.PrintItem;
import com.stefanos.order.R;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanos.order.SQLiteDatabaseForPrinters.DatabaseHandler;
import com.stefanos.order.SQLiteDatabaseForPrinters.IpAdressPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TableIndividualActivity extends AppCompatActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TableIndividualAdapter individualTableAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Toolbar toolbarBottom;
    private Button paymentButton;
    private Button printButton;
    private CollectionReference tablesRef;
    private FloatingActionButton addItemToTable;
    private DatabaseHandler databaseHandler;
    private static HashMap<Object, Object> dataHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_individual);


    }

    private void setUpRecyclerView() {

        recyclerView = findViewById(R.id.individualTableRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setThePreferencesTableIndividual();
        setThePreferencesOrofos();
        String storeName = readPreferencesStoreName();

        tablesRef = db.collection("store").document(storeName).
                collection("orofoi").document(readPreferencesOrofos()).
                collection("tables").document(readPreferencesTableIndividual()).collection(readPreferencesTableIndividual());

        Query query = tablesRef.orderBy("itemID");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    individualTableAdapter.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<OrderItem> options = new FirestoreRecyclerOptions.Builder<OrderItem>().
                setQuery(query, OrderItem.class).build();
        individualTableAdapter = new TableIndividualAdapter(options);
        recyclerView.setAdapter(individualTableAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                individualTableAdapter.deleteItem(viewHolder.getAdapterPosition());
                setTotalBottomToolbar(tablesRef);
            }
        }).attachToRecyclerView(recyclerView);

        setTotalBottomToolbar(tablesRef);

        setAddItemToTableMethod();

        setPrintButton();
    }

    private void setPrintButton() {

        printButton = findViewById(R.id.printButton);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setThePreferencesTableIndividual();
                setThePreferencesOrofos();
                String storeName = readPreferencesStoreName();

                tablesRef = db.collection("store").document(storeName).
                        collection("orofoi").document(readPreferencesOrofos()).
                        collection("tables").document(readPreferencesTableIndividual()).collection(readPreferencesTableIndividual());
                tablesRef.orderBy("itemID").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            databaseHandler = new DatabaseHandler(TableIndividualActivity.this);
                            Toast.makeText(TableIndividualActivity.this, "Εκτυπωση", Toast.LENGTH_LONG).show();
                            new ConnectionWithPrinter(databaseHandler, task.getResult()).execute();

                        }
                    }
                });


            }
        });
    }

    private void setTotalBottomToolbar(final CollectionReference tablesRef) {
        final double[] sum = {0};
        tablesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int count = 0;
                for (DocumentSnapshot document : task.getResult()) {
                    count++;
                }

                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        sum[0] = sum[0] + individualTableAdapter.getItem(i).getPriceItem();
                    }
                }
                if (sum[0] == 0) {
                    DocumentReference documentReference = db.collection("store").document(readPreferencesStoreName()).
                            collection("orofoi").document(readPreferencesOrofos()).
                            collection("tables").document(readPreferencesTableIndividual());

                    HashMap<String, Object> example2 = new HashMap<>();
                    example2.put("status", "");
                    documentReference.update(example2);
                }

                toolbarBottom = findViewById(R.id.toolbarBottom);
                toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });


                paymentButton = findViewById(R.id.paymentButton);
                final double finalSum = sum[0];
                final int finalCount = count;
                paymentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        tablesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    double partialSum = 0;
                                    int countItems = 0;
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        if (Boolean.parseBoolean(String.valueOf(documentSnapshot.get("enabledPartialPayment")))) {
                                            partialSum = partialSum + Double.parseDouble(String.valueOf(documentSnapshot.get("priceItem")));
                                            countItems++;
                                        }
                                    }
                                    if (partialSum == 0) {
                                        setPaymentButtonMethod(finalSum, view, finalCount);
                                    } else {
                                        setPartialPaymentMethod(partialSum, view, countItems);

                                    }

                                }
                            }
                        });
                    }
                });


            }
        });


    }

    private void setPartialPaymentMethod(final double sum, View view, int countItems) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        View viewDialog = layoutInflater.inflate(R.layout.create_partial_paid_dialog, null);
        alertDialogBuilder.setView(viewDialog);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        TextView numberOfItems = viewDialog.findViewById(R.id.numberOfItems);
        numberOfItems.setText("Αριθμός ειδών" + ":  " + countItems);
        TextView sumPartial = viewDialog.findViewById(R.id.partialSum);
        sumPartial.setText("Σύνολο" + ":  " + sum);
        Button yesButton = viewDialog.findViewById(R.id.yesButtonPartialPaidDialog);
        Button noButton = viewDialog.findViewById(R.id.noButtonPartialPaidDialog);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateDailyTzirosMethod(sum);
                setKouberOfTheDay(true);
                dialog.dismiss();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        toolbar = findViewById(R.id.toolbarIndividualTable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(readPreferencesTableIndividual() + " " + readPreferencesOrofos());

        if (individualTableAdapter != null) {
            individualTableAdapter.startListening();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (individualTableAdapter != null) {
            individualTableAdapter.stopListening();
        }

    }


    private String readPreferencesStoreName() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }

    private String readPreferencesOrofos() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String orofosName = prefs.getString("orofos", null);

        return orofosName;
    }

    private void setThePreferencesTableIndividual() {
        String docId;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            docId = bundle.getString("tableIndividual");
            SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
            editor.putString("tableIndividual", docId);
            editor.apply();
        }
    }

    private void setThePreferencesOrofos() {
        String docId;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            docId = bundle.getString("orofos");
            SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
            editor.putString("orofos", docId);
            editor.apply();
        }
    }

    public String readPreferencesTableIndividual() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String doc = prefs.getString("tableIndividual", null);

        return doc;
    }

    private void setAddItemToTableMethod() {
        addItemToTable = findViewById(R.id.addItemToTableFloatingButton);

        addItemToTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(TableIndividualActivity.this, MenuCategoryAccessFromTableActivity.class);

                startActivity(intent);
            }
        });

    }

    private void setPaymentButtonMethod(final double sum, View view, int finalCount) {

        if (sum > 0) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
            View viewDialog = layoutInflater.inflate(R.layout.create_paid_dialog, null);
            alertDialogBuilder.setView(viewDialog);
            final AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
            TextView numberOfItemsTotal = viewDialog.findViewById(R.id.numberOfItemsTotal);
            numberOfItemsTotal.setText(numberOfItemsTotal.getText().toString() + ":  " + finalCount);
            TextView sumTotal = viewDialog.findViewById(R.id.totalSum);
            sumTotal.setText(sumTotal.getText().toString() + ":  " + String.valueOf(sum));
            Button yesButton = viewDialog.findViewById(R.id.yesButtonPaidDialog);
            Button noButton = viewDialog.findViewById(R.id.noButtonPaidDialog);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String storeName = readPreferencesStoreName();
                    DocumentReference changeStatus = db.collection("store").
                            document(storeName).collection("orofoi").document(readPreferencesOrofos()).
                            collection("tables").document(readPreferencesTableIndividual());
                    HashMap<String, Object> example2 = new HashMap<>();
                    example2.put("status", "paid");
                    changeStatus.update(example2);
                    calculateDailyTzirosMethod(sum);
                    setKouberOfTheDay(false);
                    dialog.dismiss();
                }
            });


        }

    }


    private void calculateDailyTzirosMethod(double sum) {

        DocumentReference tzirosDaily = db.collection("store").document(readPreferencesStoreName()).collection("tzirosDaily").document();
        HashMap<Object, Object> hashMap = new HashMap<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        hashMap.put("currentYear", currentYear);
        hashMap.put("currentMonth", currentMonth);
        hashMap.put("currentDay", currentDay);
        hashMap.put("currentDateDays", currentYear * 365 + currentMonth * 30 + currentDay);
        hashMap.put("sum", sum);

        tzirosDaily.set(hashMap);

    }


    private void setKouberOfTheDay(final boolean partialPaymentEnabled) {

        tablesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!partialPaymentEnabled) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            HashMap<Object, Object> hashMap = new HashMap<>();

                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                            int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
                            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                            hashMap.put("currentYear", currentYear);
                            hashMap.put("currentMonth", currentMonth);
                            hashMap.put("currentDay", currentDay);
                            hashMap.put("currentDateDays", currentYear * 365 + currentMonth * 30 + currentDay);
                            hashMap.put("nameKouber", documentSnapshot.get("nameItem"));
                            hashMap.put("priceKouber", documentSnapshot.get("priceItem"));
                            hashMap.put("quantityKouber", documentSnapshot.get("quantity"));
                            DocumentReference kouberDaily = db.collection("store").document(readPreferencesStoreName()).collection("kouberDaily").document();
                            kouberDaily.set(hashMap);
                            documentSnapshot.getReference().delete();
                        }
                    } else {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            if (Boolean.parseBoolean(String.valueOf(documentSnapshot.get("enabledPartialPayment")))) {
                                HashMap<Object, Object> hashMap = new HashMap<>();

                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
                                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                                hashMap.put("currentYear", currentYear);
                                hashMap.put("currentMonth", currentMonth);
                                hashMap.put("currentDay", currentDay);
                                hashMap.put("currentDateDays", currentYear * 365 + currentMonth * 30 + currentDay);
                                hashMap.put("nameKouber", documentSnapshot.get("nameItem"));
                                hashMap.put("priceKouber", documentSnapshot.get("priceItem"));
                                hashMap.put("quantityKouber", documentSnapshot.get("quantity"));
                                DocumentReference kouberDaily = db.collection("store").document(readPreferencesStoreName()).collection("kouberDaily").document();
                                kouberDaily.set(hashMap);
                                documentSnapshot.getReference().delete();
                            }
                        }
                    }

                }
            }
        });

    }


    private class ConnectionWithPrinter extends AsyncTask<Void, Void, Void> {


        private DatabaseHandler databaseHandler;
        private QuerySnapshot querySnapshot;

        public ConnectionWithPrinter(DatabaseHandler databaseHandler, QuerySnapshot querySnapshot) {
            this.databaseHandler = databaseHandler;
            this.querySnapshot = querySnapshot;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {

            try {

                List<IpAdressPrinter> ipAdressPrinterList = new ArrayList<>();
                ipAdressPrinterList = databaseHandler.getAllIpAddresses();
                for (IpAdressPrinter ipAdressPrinter : ipAdressPrinterList) {

                    Socket sock = new Socket(ipAdressPrinter.getIpAdress(), Integer.parseInt(ipAdressPrinter.getPort()));
                    PrintWriter oStream = new PrintWriter(sock.getOutputStream());

                    Log.i("dataList", String.valueOf(ipAdressPrinterList.size()));

                    //select code page
                    oStream.write(27);
                    oStream.write(116);
                    oStream.write(24);


                    checkStringAndConvert("ΤΡΑΠΕΖΙ: " + readPreferencesTableIndividual(), oStream);
                    oStream.print("\n\n");

                    checkStringAndConvert("ΣΕΡΒΙΤΟΡΟΣ: " + getThePreferencesUser(), oStream);

                    oStream.print("\n\n");
                    Calendar calendar = new GregorianCalendar();
                    TimeZone timeZone = TimeZone.getTimeZone("Europe/Athens");
                    calendar.setTimeZone(timeZone);
                    checkStringAndConvert("ΗΜΕΡΟΜΗΝΙΑ: " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) +
                            " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE), oStream);
                    oStream.print("\n\n");
                    int countLines = 6;
                    int countSpacesExtra = -1;
                    int countSpacesXwris = -1;
                    int itemId = 1;
                    for (DocumentSnapshot documentSnapshot : querySnapshot) {
                        if (Integer.parseInt(String.valueOf(documentSnapshot.get("itemID")))!=itemId){
                            oStream.print("\n");
                            checkStringAndConvert("--------------", oStream);
                            itemId=Integer.parseInt(String.valueOf(documentSnapshot.get("itemID")));
                        }
                            oStream.print("\n\n");
                            checkStringAndConvert(String.valueOf(documentSnapshot.get("nameItem")), oStream);

                        Log.i("SOS1", String.valueOf(documentSnapshot.get("nameItem")));
                        if (!String.valueOf(documentSnapshot.get("extra")).isEmpty()) {
                            oStream.print("\n");

                            String cleanString;
                            cleanString = String.valueOf(documentSnapshot.get("extra")).replaceAll("[\n]", " ");
                            checkAndConvertXwrisAndExtra(cleanString, oStream, countSpacesExtra);
                            Log.i("extra", String.valueOf(documentSnapshot.get("extra")));

                            countLines++;
                        }
                        if (!String.valueOf(documentSnapshot.get("xwris")).isEmpty()) {
                            oStream.print("\n");

                            String cleanString;
                            cleanString = String.valueOf(documentSnapshot.get("xwris")).replaceAll("[\n]", " ");
                            checkAndConvertXwrisAndExtra(cleanString, oStream, countSpacesXwris);
                            countLines++;
                        }
                        countLines++;
                    }

                    //feed paper the printer
                    oStream.write(27);
                    oStream.write(100);
                    oStream.write(countLines);
                    //partial cut the paper
                    oStream.write(27);
                    oStream.write(109);

                    oStream.close();

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getThePreferencesUser() {

            SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
            String user = prefs.getString("user", null);

            return user;

        }

        private void checkStringAndConvert(String input, PrintWriter oStream) {

            for (int i = 0; i < input.length(); i++) {

                if (!checkLatinCharacterAndNumber(input.charAt(i))) {

                    if (input.charAt(i) == 'Ρ' || input.charAt(i) == 'ρ') {
                        oStream.print('P');
                    } else {
                        oStream.write(27);
                        oStream.write(116);
                        oStream.print(convertGreekCharacter(input.charAt(i)));
                    }
                } else {
                    oStream.print(input.charAt(i));
                }
            }

        }

        private void checkAndConvertXwrisAndExtra(String input, PrintWriter oStream, int countSpaces) {

            for (int i = 0; i < input.length(); i++) {

                if (!checkLatinCharacterAndNumber(input.charAt(i))) {

                    if (input.charAt(i) == 'Ρ' || input.charAt(i) == 'ρ') {
                        oStream.print('P');
                    } else {
                        oStream.write(27);
                        oStream.write(116);
                        oStream.print(convertGreekCharacter(input.charAt(i)));
                    }
                } else {
                    oStream.print(input.charAt(i));
                    if (input.charAt(i) == ' ') {
                        countSpaces++;
                    }
                    if (countSpaces % 2 == 0) {
                        oStream.print('\n');
                        oStream.print('-');
                    }
                }
            }
        }

        private char convertGreekCharacter(char androidChar) {

            switch (androidChar) {
                //Capital letters

                case 'Α':
                    return 'π';

                case 'Β':
                    return 'ρ';

                case 'Γ':
                    return 'ς';

                case 'Δ':
                    return 'σ';

                case 'Ε':
                    return 'τ';

                case 'Ζ':
                    return 'υ';

                case 'Η':
                    return 'φ';

                case 'Θ':
                    return 'χ';

                case 'Ι':
                    return 'ψ';

                case 'Κ':
                    return 'ω';

                case 'Λ':
                    return 'ϊ';

                case 'Μ':
                    return 'ϋ';

                case 'Ν':
                    return 'Ό';

                case 'Ξ':
                    return 'ύ';

                case 'Ο':
                    return 'ώ';

                case 'Π':
                    return 'Ώ';

                case 'Ρ':
                    return '═';

                case 'Σ':
                    return 'Α';

                case 'Τ':
                    return 'Β';

                case 'Υ':
                    return 'Γ';

                case 'Φ':
                    return 'Δ';

                case 'Χ':
                    return 'Ε';

                case 'Ψ':
                    return 'Ζ';

                case 'Ω':
                    return 'Η';

                //small letters
                case 'α':
                    return 'π';

                case 'β':
                    return 'ρ';

                case 'γ':
                    return 'ς';

                case 'δ':
                    return 'σ';

                case 'ε':
                    return 'τ';

                case 'ζ':
                    return 'υ';

                case 'η':
                    return 'φ';

                case 'θ':
                    return 'χ';

                case 'ι':
                    return 'ψ';

                case 'κ':
                    return 'ω';

                case 'λ':
                    return 'ϊ';

                case 'μ':
                    return 'ϋ';

                case 'ν':
                    return 'Ό';

                case 'ξ':
                    return 'ύ';

                case 'ο':
                    return 'ώ';

                case 'π':
                    return 'Ώ';

                case 'ρ':
                    return '═';

                case 'σ':
                    return 'Α';

                case 'τ':
                    return 'Β';

                case 'υ':
                    return 'Γ';

                case 'φ':
                    return 'Δ';

                case 'χ':
                    return 'Ε';

                case 'ψ':
                    return 'Ζ';

                case 'ω':
                    return 'Η';
                case 'ς':
                    return 'Α';
                //vocals small letters

                case 'ά':
                    return 'π';

                case 'έ':
                    return 'τ';

                case 'ή':
                    return 'φ';

                case 'ί':
                    return 'ψ';

                case 'ύ':
                    return 'Γ';

                case 'ώ':
                    return 'Η';

                case 'ό':
                    return 'ώ';

                //capital vocal letters

                case 'Ά':
                    return 'π';

                case 'Έ':
                    return 'τ';

                case 'Ή':
                    return 'φ';

                case 'Ί':
                    return 'ψ';

                case 'Ύ':
                    return 'Γ';

                case 'Ώ':
                    return 'Η';

                case 'Ό':
                    return 'π';


            }
            return androidChar;
        }

        private boolean checkLatinCharacterAndNumber(char androidChar) {

            HashMap<Object, Character> latinCharacterHashMap = new HashMap<>();
            latinCharacterHashMap.put('A', 'A');
            latinCharacterHashMap.put('B', 'B');
            latinCharacterHashMap.put('C', 'C');
            latinCharacterHashMap.put('D', 'D');
            latinCharacterHashMap.put('E', 'E');
            latinCharacterHashMap.put('F', 'F');
            latinCharacterHashMap.put('G', 'G');
            latinCharacterHashMap.put('H', 'H');
            latinCharacterHashMap.put('I', 'I');
            latinCharacterHashMap.put('J', 'J');
            latinCharacterHashMap.put('K', 'K');
            latinCharacterHashMap.put('L', 'L');
            latinCharacterHashMap.put('M', 'M');
            latinCharacterHashMap.put('N', 'N');
            latinCharacterHashMap.put('O', 'O');
            latinCharacterHashMap.put('P', 'P');
            latinCharacterHashMap.put('Q', 'Q');
            latinCharacterHashMap.put('R', 'R');
            latinCharacterHashMap.put('S', 'S');
            latinCharacterHashMap.put('T', 'T');
            latinCharacterHashMap.put('U', 'U');
            latinCharacterHashMap.put('V', 'V');
            latinCharacterHashMap.put('W', 'W');
            latinCharacterHashMap.put('X', 'X');
            latinCharacterHashMap.put('Y', 'Y');
            latinCharacterHashMap.put('Z', 'Z');
            latinCharacterHashMap.put('a', 'a');
            latinCharacterHashMap.put('b', 'b');
            latinCharacterHashMap.put('c', 'c');
            latinCharacterHashMap.put('d', 'd');
            latinCharacterHashMap.put('e', 'e');
            latinCharacterHashMap.put('f', 'f');
            latinCharacterHashMap.put('g', 'g');
            latinCharacterHashMap.put('h', 'h');
            latinCharacterHashMap.put('i', 'i');
            latinCharacterHashMap.put('j', 'j');
            latinCharacterHashMap.put('k', 'k');
            latinCharacterHashMap.put('l', 'l');
            latinCharacterHashMap.put('m', 'm');
            latinCharacterHashMap.put('n', 'n');
            latinCharacterHashMap.put('o', 'o');
            latinCharacterHashMap.put('p', 'p');
            latinCharacterHashMap.put('q', 'q');
            latinCharacterHashMap.put('r', 'r');
            latinCharacterHashMap.put('s', 's');
            latinCharacterHashMap.put('t', 't');
            latinCharacterHashMap.put('u', 'u');
            latinCharacterHashMap.put('v', 'v');
            latinCharacterHashMap.put('w', 'w');
            latinCharacterHashMap.put('x', 'x');
            latinCharacterHashMap.put('y', 'y');
            latinCharacterHashMap.put('z', 'z');
            latinCharacterHashMap.put('1', '1');
            latinCharacterHashMap.put('2', '2');
            latinCharacterHashMap.put('3', '3');
            latinCharacterHashMap.put('4', '4');
            latinCharacterHashMap.put('5', '5');
            latinCharacterHashMap.put('6', '6');
            latinCharacterHashMap.put('8', '8');
            latinCharacterHashMap.put('9', '9');
            latinCharacterHashMap.put('0', '0');
            latinCharacterHashMap.put('=', '=');
            latinCharacterHashMap.put('+', '+');
            latinCharacterHashMap.put('-', '-');
            latinCharacterHashMap.put('/', '/');
            latinCharacterHashMap.put("", ' ');
            latinCharacterHashMap.put('*', '*');
            latinCharacterHashMap.put('?', '?');
            latinCharacterHashMap.put(' ', ' ');
            latinCharacterHashMap.put(':', ':');
            latinCharacterHashMap.put('.', '.');
            return latinCharacterHashMap.containsKey(androidChar);
        }
    }

}



