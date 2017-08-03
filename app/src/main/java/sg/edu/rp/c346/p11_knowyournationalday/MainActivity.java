package sg.edu.rp.c346.p11_knowyournationalday;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvFacts;
    ArrayList<String> factList = new ArrayList<String>();
    ArrayAdapter<String> aa;
    String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = prefs.getString("key", "");

        if (key.equals("")){
            loginDialog();
        }

        lvFacts = (ListView) findViewById(R.id.lvFacts);
        factList.add("Singapore National Day is on 9 Aug");
        factList.add("Singapore is 52 years old");
        factList.add("There is '#OneNationTogether'");
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, factList);
        lvFacts.setAdapter(aa);
        permission();
    }

    public void permission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                1);
    }

    public void loginDialog() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout passPhrase =
                (LinearLayout) inflater.inflate(R.layout.passphrase, null);
        final EditText etPassphrase = (EditText) passPhrase
                .findViewById(R.id.editTextPassPhrase);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please login")
                .setView(passPhrase)
                .setCancelable(false)
                // Set text for the positive button and the corresponding
                //  OnClickListener when it is clicked
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    protected Object clone() throws CloneNotSupportedException {
                        return super.clone();
                    }

                    public void onClick(DialogInterface dialog, int id) {
                        if (etPassphrase.getText().toString().equalsIgnoreCase("738964")) {
                            Toast.makeText(MainActivity.this, "You have login",
                                    Toast.LENGTH_LONG).show();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            accessCode = etPassphrase.getText().toString();
                            final SharedPreferences.Editor prefEdit = prefs.edit();
                            prefEdit.putString("key", accessCode);
                            prefEdit.commit();
                        } else {
                            Toast.makeText(MainActivity.this, "You have entered the wrong password",
                                    Toast.LENGTH_LONG).show();
                            loginDialog();
                        }
                    }
                })
                // Set text for the negative button and the corresponding
                //  OnClickListener when it is clicked
                .setNegativeButton("NO ACCESS CODE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "You clicked no",
                                Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Tally against the respective action item clicked
        //  and implement the appropriate action
        if (item.getItemId() == R.id.sendToFriend) {
            String[] list = new String[]{"Email", "SMS"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the way to enrich your friend?")
                    // Set the list of items easily by just supplying an
                    //  array of the items
                    .setItems(list, new DialogInterface.OnClickListener() {
                        // The parameter "which" is the item index
                        // clicked, starting from 0
                        public void onClick(DialogInterface dialog, int which) {
                            String message = "";
                            for(int i = 0; i < factList.size(); i++){
                                message += (i+1) + ". " + factList.get(i) + "\n";
                            }
                            if (which == 0) {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL,
                                        new String[]{"15017103@rp.edu.sg"});
                                email.putExtra(Intent.EXTRA_SUBJECT,
                                        "-");
                                email.putExtra(Intent.EXTRA_TEXT,
                                        "Hi Andre, \n" + message);
                                email.setType("message/rfc822");
                                startActivity(Intent.createChooser(email,
                                        "Choose an Email client :"));
                                Toast.makeText(MainActivity.this, "Email has been sent",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                try{
                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                    sendIntent.setData(Uri.parse("sms:"));

                                    sendIntent.putExtra("sms_body", message);
                                    startActivity(sendIntent);

                                    Toast.makeText(MainActivity.this, "SMS has been sent",
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "SMS has not been sent",
                                            Toast.LENGTH_LONG).show();
                                }



                            }
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (item.getItemId() == R.id.quiz) {
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout quiz =
                    (LinearLayout) inflater.inflate(R.layout.quiz, null);
            final RadioGroup rg1 = (RadioGroup)quiz.findViewById(R.id.rg1);
            final RadioGroup rg2 = (RadioGroup)quiz.findViewById(R.id.rg2);
            final RadioGroup rg3 = (RadioGroup)quiz.findViewById(R.id.rg3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Test Yourself!")
                    .setView(quiz)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String result = "";
                            int selectedButtonId1 = rg1.getCheckedRadioButtonId();
                            int selectedButtonId2 = rg2.getCheckedRadioButtonId();
                            int selectedButtonId3 = rg3.getCheckedRadioButtonId();
                            if(selectedButtonId1 == R.id.radioButton){
                                result += "Answer 1: Wrong\n";
                            }
                            else{
                                result += "Answer 1: Correct\n";
                            }
                            if(selectedButtonId2 == R.id.radioButton3){
                                result += "Answer 2: Correct\n";
                            }
                            else{
                                result += "Answer 2: Wrong\n";
                            }
                            if(selectedButtonId3 == R.id.radioButton5){
                                result += "Answer 3: Correct\n";
                            }
                            else{
                                result += "Answer 3: Wrong\n";
                            }
                            Toast.makeText(MainActivity.this, result,
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Don't Know Lah", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Buck Up Pls!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else if (item.getItemId() == R.id.quit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quit?")
                    // Set text for the positive button and the corresponding
                    //  OnClickListener when it is clicked
                    .setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "You have quit",
                                    Toast.LENGTH_LONG).show();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            final SharedPreferences.Editor prefEdit = prefs.edit();
                            prefEdit.putString("key", "");
                            prefEdit.commit();
                            finish();
                        }
                    })
                    // Set text for the negative button and the corresponding
                    //  OnClickListener when it is clicked
                    .setNegativeButton("NOT REALLY", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Yeah",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            // Create the AlertDialog object and return it
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.SEND_SMS);
                    if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    } else {

                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
