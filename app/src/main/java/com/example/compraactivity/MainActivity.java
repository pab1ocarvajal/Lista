package com.example.compraactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String FILENAME = "compra_list.txt";
    private static final int MAX_BYTES = 8000;

    private ArrayList<shopingItem> itemList;
    private CompraListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    private void writeItemList(){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for(int i = 0; i < itemList.size(); i++){
                shopingItem it = itemList.get(i);
                String line = String.format("%s;%b\n", it.getText(), it.isChecked());
                fos.write(line.getBytes());
            }
            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("Pablo","writeItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannotwrite, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Pablo","writeItemList: IOException");
            Toast.makeText(this, R.string.cannotwrite, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void readItemList(){
        itemList = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(FILENAME );
            byte[] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);

            if(nread > 0) {
                String content = new String(buffer, 0, nread);
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String[] parts = line.split(";");
                    itemList.add(new shopingItem(parts[0], parts[1].equals("true")));
                }
            }
            fis.close();

        } catch (FileNotFoundException e) {
            Log.i("Pablo","readItemList: FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Pablo","readItemList: IOException");
            Toast.makeText(this, R.string.cannotread, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeItemList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText) findViewById(R.id.edit_item);

        itemList = new ArrayList<>();

        /*itemList.add(new shopingItem ("Jabón", true));
        itemList.add(new shopingItem ("Shampoo" , true));
        */

        readItemList();

        adapter = new CompraListAdapter(this, R.layout.compra_item, itemList);

        //Añadir desde la app
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        //Añadir desde el teclado del celular
        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItem();
                return true;
            }
        });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                itemList.get(pos).toogleChecked();
                adapter.notifyDataSetChanged();

                /*shopingItem item = itemList.get(pos);
                boolean checked = item.isChecked();
                itemList.get(pos).setChecked(!checked);*/
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return true;
            }
        });
    }

    //Eliminar un producto, se utiliza la posición
    private void maybeRemoveItem(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt = getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(fmt, itemList.get(pos).getText()));

        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addItem() { //Añadir un item (producto) a la lista
        String item_text = edit_item.getText().toString();
        if (!item_text.isEmpty()) {
            itemList.add(new shopingItem(item_text));
            adapter.notifyDataSetChanged(); //Notificar al adaptador
            edit_item.setText("");
        }
        list.smoothScrollToPosition(itemList.size()-1);
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.clear_checked:
                clearChecked();
                return true;
            case R.id.clear_all:
                clearAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void clearChecked() {
        int i = 0;
        while( i < itemList.size()){
            if(itemList.get(i).isChecked()){
                itemList.remove(i);
            }else{
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }


}
