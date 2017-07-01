package com.app.phr.peru.peruphr_app.JAVA;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by chiyo on 2016-08-04.
 */
public class HTTPClient {   //HTTP connection cliecnt
    private Document mDoc;
    private String request;
    final static String LINK ="http://ucare.gilhospital.com/Peru/gateway.aspx";
            //"http://52.78.19.78/insert.php";
            //"http://ucare.gilhospital.com/Peru/gateway.aspx";
    public HTTPClient() {
      request = "";
    }
    public void setDoc(Document doc){
        mDoc = doc;
    }
    public String connect() {
        String result ="";
        try {
            URL url = new URL(LINK);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            String send =DocumentToString(mDoc);
            Log.d("http send123",send);

            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type","application/xml; charset=UTF-8"); //xml 형태 데이터 요청 세팅

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            bufferedWriter.write(DocumentToString(mDoc));
            bufferedWriter.flush();
            bufferedWriter.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            StringBuilder sb = new StringBuilder();
            String line = "";
            Log.d("http check","check2");
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                result += line;
            }
           Log.d("result",result);

            reader.close();
            conn.disconnect();
            //reader.close();

        }catch (FileNotFoundException e){
            Log.d("err",e.toString());
        }
        catch (MalformedURLException e) {
            Log.d("err",e.toString());
            return "connection error";
        } catch (IOException e) {
            e.printStackTrace();
            return "connection error";
        }
        return result;
    }

    public static String DocumentToString(Document doc) {
        try {
            StringWriter clsOutput = new StringWriter();
            Transformer clsTrans = TransformerFactory.newInstance().newTransformer();
            clsTrans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            clsTrans.setOutputProperty(OutputKeys.METHOD, "xml");
            clsTrans.setOutputProperty(OutputKeys.INDENT, "yes");
            clsTrans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            clsTrans.transform(new DOMSource(doc), new StreamResult(clsOutput));
            return clsOutput.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
