package com.ak.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button send;
    EditText message;
    Firebase firebase;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        layout = (LinearLayout) findViewById(R.id.list);
        send.setOnClickListener(this);
        firebase = new Firebase("https://androidak01.firebaseio.com/msg");
        firebase.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                layout.removeAllViews();
                if (snapshot.exists()) {
                    try {
                        if (snapshot.getValue() instanceof HashMap) {
                            Map<String, Object>  mp = (Map<String, Object>) snapshot.getValue();
                            Iterator it = mp.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                System.out.println(pair.getKey() + " = " + pair.getValue());
                                layout.addView(createTextView((String) pair.getValue()));
                            }
                        }
                        layout.invalidate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (R.id.send == view.getId()) {
            String msg = message.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                firebase.push().setValue(msg);
            }else {
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private TextView createTextView(String msg) {
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.RIGHT);
        tv.setText(msg);
        return tv;
    }
}
