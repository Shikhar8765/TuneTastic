package com.example.tunetastic;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview1);

        runtimePermission();

    }

    public void runtimePermission()
    {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaysongs();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
             permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> findsong(File file)
    {
        ArrayList<File> arrayList= new ArrayList<>();
        File[] files= file.listFiles();
        assert files != null;
        for(File singlefile:files)
        {
            if(singlefile.isDirectory()&&!singlefile.isHidden())
            {
                arrayList.addAll(findsong(singlefile));
            }
            else {
                if(singlefile.getName().endsWith(".mp3")||singlefile.getName().endsWith(".wav"))
                {
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }
    void displaysongs()
    {
        final ArrayList<File> mysongs=findsong(Environment.getExternalStorageDirectory());
        items=new String[mysongs.size()];
        for(int i=0;i<mysongs.size();i++)
        {
            items[i]= mysongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }
       customAdapter customAdapter= new customAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName=(String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs", mysongs).putExtra("songname", songName).putExtra("position", position));
            }
        });

    }



    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myView = convertView;
            if (myView == null) {
                myView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            TextView textsong = myView.findViewById(R.id.txtsongname);
            if (textsong != null) {
                textsong.setSelected(true);
                textsong.setText(items[i]);
            }

            return myView;
        }

    }
}