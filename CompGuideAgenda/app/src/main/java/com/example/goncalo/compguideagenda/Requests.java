package com.example.goncalo.compguideagenda;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Goncalo on 28/06/2017.
 */

public class Requests {

    private String cUser,cPass;
    List<Evento> eventos = new ArrayList<>();

    public boolean authetication(String username, String password) throws IOException {
            int status=0;
            boolean res=false;
            String pass;
            cUser = username;
            cPass = password;

            pass = PasswordService.getInstance().encrypt(password);

            //Your server URL
            String url = "http://10.0.2.2:8080/CompGuideCore/auth";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            //Request Parameters you want to send
            String urlParameters = "username="+username+"&"+"password="+pass;

            // Send post request
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == 200){
                res=true;
            }else {
                res=false;
            }


            return res;
    }


    //pedir para http://localhost:8081/CompGuide/webresources/com.compguide.web.persistence.entities.event
    public String getXML() throws IOException {

        InputStream inputStream = null;

        //Your server URL
        String url = "http://10.0.2.2:8081/CompGuide/webresources/com.compguide.web.persistence.entities.event";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        inputStream = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //System.out.println(sb.toString());

        //return eventos;
        return sb.toString();
    }

    public List<Evento> parse(InputStream is) {
        String text="";
        Evento evento = new Evento();
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is,null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("event")) {
                            evento = new Evento();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("event")) {
                            eventos.add(evento);
                        } else if (tagname.equalsIgnoreCase("endDate")) {
                            evento.fim = text.substring(0,10);
                        } else if (tagname.equalsIgnoreCase("startDate")) {
                            evento.inicio = text.substring(0,10);
                        } else if (tagname.equalsIgnoreCase("identifier")) {
                            evento.setDescricao(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return eventos;
    }



}
