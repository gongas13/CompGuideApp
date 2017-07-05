package com.example.goncalo.compguideagenda;

import android.content.pm.PackageManager;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.riontech.calendar.CustomCalendar;
import com.riontech.calendar.dao.EventData;
import com.riontech.calendar.dao.dataAboutDate;
import com.riontech.calendar.utils.CalendarUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CalendarActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private CustomCalendar customCalendar;
    private ArrayList<EventData> eventDataList;
    Requests r = new Requests();
    FileInputStream fis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("############# ON CREATE ###################");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        customCalendar = (CustomCalendar) findViewById(R.id.customCalendar);
        String xml = getIntent().getStringExtra("XML");
        System.out.println(xml);
        //requestPermission();
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                System.out.println("######### CRIA FICHEIRO ################");
                File file = new File("");
                try{
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + "/eventos.xml");
                    file.createNewFile();
                    byte[] b = xml.getBytes();
                    if(file.exists())
                    {
                        OutputStream fo = new FileOutputStream(file);
                        fo.write(b);
                        fo.close();
                        System.out.println("file created: "+file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println("#################### CRIA INPUT STREAM ###################");
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    System.out.println("################ CRIA ARRAY ###############");
                    List<Evento> ev =  r.parse(fis);
                    for (Evento e: ev){
                        System.out.println(e.toString());
                    }
                    System.out.println("##################### TENTA INSERIR ########################");
                    insere(ev);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            // Code for Below 23 API Oriented Device
            System.out.println("######### CRIA FICHEIRO ################");
            File file = new File("");
            try{
                file = new File(Environment.getExternalStorageDirectory() + File.separator + "/eventos.xml");
                file.createNewFile();
                byte[] b = xml.getBytes();
                if(file.exists())
                {
                    OutputStream fo = new FileOutputStream(file);
                    fo.write(b);
                    fo.close();
                    System.out.println("file created: "+file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println("#################### CRIA INPUT STREAM ###################");
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("##################### TENTA INSERIR ########################");
            try {
                insere(r.parse(fis));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }


        /*System.out.println("######### CRIA FICHEIRO ################");
        File file = new File("");
        try{
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "/eventos.xml");
            file.createNewFile();
            byte[] b = xml.getBytes();
            if(file.exists())
            {
                OutputStream fo = new FileOutputStream(file);
                fo.write(b);
                fo.close();
                System.out.println("file created: "+file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("#################### CRIA INPUT STREAM ###################");
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("##################### TENTA INSERIR ########################");
        try {
            insere(r.parse(fis));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }*/


    }


    public ArrayList<EventData> getEventDataList(int count, String descricao, String data) {
        eventDataList = new ArrayList();

        for (int i = 0; i < count; i++) {
            EventData dateData = new EventData();
            ArrayList<dataAboutDate> dataAboutDates = new ArrayList();

            dateData.setSection(data);
            dataAboutDate dataAboutDate = new dataAboutDate();

            int index = new Random().nextInt(CalendarUtils.getEVENTS().length);

            dataAboutDate.setTitle(descricao);
            dataAboutDates.add(dataAboutDate);

            dateData.setData(dataAboutDates);
            eventDataList.add(dateData);
        }

        return eventDataList;
    }

    public void insere(List<Evento> eventos) throws IOException, XmlPullParserException {

        System.out.println("############ TOU DENTRO DO INSERE ################");
        for (Evento e : eventos) {
            if (!e.inicio.equals(e.fim)){
                int diai = Integer.parseInt(e.inicio.substring(8,10));
                int diaf = Integer.parseInt(e.fim.substring(8,10));
                System.out.println("DIA DE INICIO: "+diai);
                System.out.println("DIA DE FIM: "+diaf);
                int dias = diaf - diai;
                for (int i =0; i< dias+1; i++){
                    String data = e.inicio.substring(0,8);
                    String dia = Integer.toString(diai);
                    if (diai < 10) {
                        String zero = "0" + dia;
                        System.out.println("################### ZERO "+zero+" #################");
                        data = data + zero;
                        System.out.println("############ DATA A INSERIR "+data+" ################");
                    }else {
                        data = data + dia;
                        System.out.println("############ DATA A INSERIR "+data+" ################");
                    }
                    customCalendar.addAnEvent(data, 1, getEventDataList(1,e.descricao,e.inicio));
                    diai++;
                }
            }else {
                System.out.println(e.toString());
                customCalendar.addAnEvent(e.inicio, 1, getEventDataList(1, e.descricao,e.inicio));
            }
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CalendarActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(CalendarActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(CalendarActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CalendarActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }




}
