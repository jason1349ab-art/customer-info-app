
package ir.duami.customerinfo;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    LinearLayout list;
    EditText name, phone, note;
    SharedPreferences prefs;
    JSONArray customers;

    int navy = Color.rgb(15, 29, 55);
    int gold = Color.rgb(212, 170, 75);

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("customers", MODE_PRIVATE);
        try {
            customers = new JSONArray(prefs.getString("data", "[]"));
        } catch (Exception e) {
            customers = new JSONArray();
        }

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(245, 246, 250));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("اطلاعات مشتریان");
        title.setTextSize(25);
        title.setTextColor(gold);
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 25, 10, 25);
        title.setBackgroundColor(navy);
        root.addView(title);

        name = field(root, "نام و نام خانوادگی");
        phone = field(root, "شماره موبایل");
        note = field(root, "موضوع درخواست یا توضیحات");

        Button save = new Button(this);
        save.setText("ذخیره مشتری");
        save.setTextColor(Color.WHITE);
        save.setBackgroundTintList(android.content.res.ColorStateList.valueOf(navy));
        root.addView(save);

        TextView heading = new TextView(this);
        heading.setText("فهرست مشتریان");
        heading.setTextSize(21);
        heading.setTextColor(navy);
        heading.setPadding(5, 25, 5, 15);
        root.addView(heading);

        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        root.addView(list);

        save.setOnClickListener(v -> saveCustomer());
        showCustomers();
    }

    EditText field(LinearLayout root, String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(16);
        e.setSingleLine(false);
        e.setPadding(15, 12, 15, 12);
        e.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 10, 0, 10);
        root.addView(e, p);
        return e;
    }

    void saveCustomer() {
        String n = name.getText().toString().trim();
        String p = phone.getText().toString().trim();
        String d = note.getText().toString().trim();

        if (n.isEmpty() || p.isEmpty()) {
            Toast.makeText(this,
                "نام و شماره موبایل را وارد کنید",
                Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject item = new JSONObject();
            item.put("name", n);
            item.put("phone", p);
            item.put("note", d);
            customers.put(item);

            prefs.edit().putString("data",
                customers.toString()).apply();

            name.setText("");
            phone.setText("");
            note.setText("");

            showCustomers();
            Toast.makeText(this, "مشتری ذخیره شد",
                Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "خطا در ذخیره اطلاعات",
                Toast.LENGTH_SHORT).show();
        }
    }

    void showCustomers() {
        list.removeAllViews();

        for (int i = customers.length() - 1; i >= 0; i--) {
            try {
                JSONObject item = customers.getJSONObject(i);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(15, 12, 15, 12);
                card.setBackgroundColor(Color.WHITE);
                card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                TextView info = new TextView(this);
                info.setText(
                    item.optString("name") + "\n" +
                    item.optString("phone") + "\n" +
                    item.optString("note")
                );
                info.setTextSize(16);
                info.setTextColor(navy);
                card.addView(info);

                Button call = new Button(this);
                call.setText("تماس با مشتری");
                call.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + item.optString("phone")));
                    startActivity(intent);
                });
                card.addView(call);

                LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, 8, 0, 8);
                list.addView(card, params);

            } catch (Exception ignored) {}
        }
    }
}
