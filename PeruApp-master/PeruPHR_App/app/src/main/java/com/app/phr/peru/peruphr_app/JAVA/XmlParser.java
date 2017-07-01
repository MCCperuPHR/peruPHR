package com.app.phr.peru.peruphr_app.JAVA;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by chiyo on 2016-08-05.
 */
public class XmlParser {   //RESPONSE받아오는 String data를 파싱해주는 class  -> 사용자 name, keyCD, data, response code를 추출할수 있다.
    private boolean mResponse; //request phr
    private String mData = "";
    private String key;
    private String pName;
    private PHR phr;


    public XmlParser() {
        key = "";
        pName = "";
    }

    public int checkResponse(String data) {
        int response = 0;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            mData = data;

            InputStream istream = new
                    ByteArrayInputStream(mData.getBytes("UTF-8"));
            Document doc = builder.parse(istream);
            Node node = doc.getFirstChild();


            NamedNodeMap Attrs = node.getAttributes();
            Node attr = Attrs.item(0);
            response = Integer.valueOf(attr.getNodeValue());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return response;
    }

    public boolean resForLogin(String data) { //check response for login
        mResponse = false;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            mData = data;


            InputStream istream = new
                    ByteArrayInputStream(mData.getBytes("utf-8"));
            Document doc = builder.parse(istream);
            Node node = doc.getFirstChild();


            NamedNodeMap Attrs = node.getAttributes();
            Node attr = Attrs.item(0);

            if(Integer.valueOf(attr.getNodeValue()) == 100){
                mResponse = true;
            }
            else{
               return false;
            }
            Element order = doc.getDocumentElement();
            Node item = order.getElementsByTagName("KeyCD").item(0);
            key = item.getFirstChild().getNodeValue();
            item = order.getElementsByTagName("PatientName").item(0);
            pName = item.getFirstChild().getNodeValue();

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mResponse;
    }
    public String getKey(){
        return key;
    }
    public String getPName(){
        return pName;
    }
    public PHR getPhrObject(String data){
        return phr;
    }
}


