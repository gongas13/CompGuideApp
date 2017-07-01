package com.example.goncalo.compguideagenda;

import android.graphics.Path;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CalendarActivity extends AppCompatActivity {

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
        System.out.println("######### CRIA FICHEIRO ################");
        File file = new File("C:/Users/Goncalo/AndroidStudioProjects/CompGuideAgenda/eventos.xml");
        try{
            PrintWriter writer = new PrintWriter(file);
            writer.println(xml);
            writer.close();
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


    public ArrayList<EventData> getEventDataList(int count, String descricao) {
        eventDataList = new ArrayList();

        for (int i = 0; i < count; i++) {
            EventData dateData = new EventData();
            ArrayList<dataAboutDate> dataAboutDates = new ArrayList();

            dateData.setSection("");
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

        Iterator<Evento> iteventos = eventos.iterator();
        while (iteventos.hasNext()) {

            iteventos.toString();
            customCalendar.addAnEvent(iteventos.next().inicio, 1, getEventDataList(1,iteventos.next().descricao));

        }

    }




}
