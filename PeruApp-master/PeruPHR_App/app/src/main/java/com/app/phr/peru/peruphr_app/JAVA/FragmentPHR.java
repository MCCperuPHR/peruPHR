package com.app.phr.peru.peruphr_app.JAVA;

/**
 * Created by hansol on 2016-08-10.
 * phr xml parsing
 * PHR.java 를 통하여 메소드에 항목별로 변수 지정 됨
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phr.peru.peruphr_app.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class FragmentPHR extends Fragment {
    private PHR phr;
    private boolean getPHR = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private XmlParser parser;
    private String mID = "";
    private String mKey = "";
    private requestPHRTask requestTask = null;
    private boolean flag = false;
    private String Phr;
    TableLayout tb1, tb2, tb3;
    public String title;
    public FragmentPHR() {
        parser = new XmlParser();
    }

    private void requestPHR() {  //request phr data to server
        Log.d("frag", "request");
        if (requestTask != null) {
            return;
        }
        mID = preferences.getString(PreferencePutter.PREF_ID, "error");
        mKey = preferences.getString(PreferencePutter.PREF_KEY, "error");
        requestTask = new requestPHRTask();
        requestTask.execute((Void) null);
    }

    private void parse_putPHR(String result) {  //parse and put phr data in activirty through using xml parser
        phr = parser.getPhrObject(result);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(PreferencePutter.PREF_FILE_NAME, Activity.MODE_PRIVATE);

        if (NetworkUtil.getConnectivityStatusBoolean((getActivity()))) {   //PHR fragment가 create 되었을때 network 상태 유무를 판단해서 network가 연결되어있으면 server에 phr 요청 후 받은 response로 layour 구성
            //network가 연결 안될시에는 SharedPreference에 저장했던 response xml data 를 parsing해서 띄우기. (network에게 response를 받아올때마다 sharedPreference에 저장)
            requestPHR();

            while (!flag) {
            }
            flag = false;
// 데이터 receive 성공 유무 판단 !!
            Log.d("frag", "get phr");
        } else {
            setLayoutWithout_Net();
        }
    }
    private void setLayoutWithout_Net(){
        Log.d("frag", "networkless");
        Phr = preferences.getString(PreferencePutter.PHR, "null");
        if (Phr.equals("null")) {
            //show need to networ for receiving data
            Log.d("frag", "null phr");
            getPHR = false;
        } else if (Phr.equals("non_record")) {
            getPHR = false;
        } else {
            //parsing result & show
            parse_putPHR(Phr);  //insert phr into layout
            Log.d("non net", Phr);
            getPHR = true;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // hideKeyboard();

        View rootView;
        if(getPHR) {
            rootView = inflater.inflate(R.layout.fragment_phr, container, false);
            phr = new PHR();
            tb1 = (TableLayout) rootView.findViewById(R.id.table1);
            tb1.setStretchAllColumns(true);
            tb2 = (TableLayout) rootView.findViewById(R.id.table2);
            tb2.setStretchAllColumns(true);
            tb3 = (TableLayout) rootView.findViewById(R.id.table3);
            tb3.setStretchAllColumns(true);

            //항목별로 매소드 구성
            AllergyParsing();
            AdverseReactionParsing();
            PastHistoryParsing();
            FamilyHistoryParsing();
            SocialHistoryParsing();
            PM_HeightParsing();
            PM_WeightParsing();
            PM_BloodPressureParsing();
            PM_PulseParsing();
            PM_BloodTypeParsing();
            MedicationParsing();
            TeleMedicineParsing();

        } else {
            rootView = inflater.inflate(R.layout.non_phr, container, false);
        }
        return rootView;

    }

    public class requestPHRTask extends AsyncTask<Void, Void, String> {  //thread to connect to server
        private HTTPClient client;
        private XmlWriter writer;

        @Override
        protected void onPreExecute() {

            writer = new XmlWriter();
            client = new HTTPClient();
            Log.d("http send", writer.getForPhr(mID, mKey).toString());
            client.setDoc(writer.getForPhr(mID, mKey));
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            // Simulate network access.
            result = client.connect();


            if (result.equals("connection error")) {
                setLayoutWithout_Net();
            } else {
                int response = parser.checkResponse(result);
                //save result in object and show to activity.
                if (response == 100) {
                    Log.d("phr", "response msg : 100");
                    //layout = R.layout.fragment_phr;
                    editor = preferences.edit();
                    editor.putString(PreferencePutter.PHR, result);
                    editor.commit();
                    Phr = result;
                    getPHR = true;
                } else if (response == 202) {
                    //show 진료된 기록이 없습니다.
//               Log.d("phr", "response msg : 100");
                    editor = preferences.edit();
                    editor.putString(PreferencePutter.PHR, "non_record");
                    editor.commit();
                    getPHR = false;

                } else {
                    Log.d("phr","response 500");
                    setLayoutWithout_Net();
                }
            }
            flag = true;
            requestTask = null;
            return "";

        }

        @Override
        protected void onCancelled() {
            flag = true;
            requestTask = null;
            //showProgress(false);
        }

    }

    public void AllergyParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //노드 읽어오기
            Element order = doc.getDocumentElement();
            //노드 개수 파악
            NodeList allergys = order.getElementsByTagName("Allergy");

            //항목이 0개가 아니면 title 부르기
            if (allergys.getLength() != 0) {
                setTitle("Allergy");
                setSubtitle2();
            }

            //노드-자식노드를 통하여 값을 파싱 함.
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Allergy");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String Date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경 홀수 단위로 생각하기.
                String Value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setAllergy_d(Date);
                phr.setAllergy_v(Value);
                setXmlParsing2(phr.getAllergy_d(), phr.getAllergy_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void AdverseReactionParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("AdverseReaction");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("AdverseReaction");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("AdverseReaction");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String Date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String Value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setAdverseReaction_d(Date);
                phr.setAdverseReaction_v(Value);
                setXmlParsing2(phr.getAdverseReaction_d(), phr.getAdverseReaction_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PastHistoryParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("PastHistory");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("PastHistory");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("PastHistory");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPastHistory_d(date);
                phr.setPastHistory_v(value);
                setXmlParsing2(phr.getPastHistory_d(), phr.getPastHistory_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void FamilyHistoryParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("FamilyHistory");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("FamilyHistory");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("FamilyHistory");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setFamilyHistory_d(date);//여기변경
                phr.setFamilyHistory_v(value);
                setXmlParsing2(phr.getFamilyHistory_d(), phr.getFamilyHistory_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void SocialHistoryParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("SocialHistory");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("SocialHistory");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("SocialHistory");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setSocialHistory_d(date);
                phr.setSocialHistory_v(value);
                setXmlParsing2(phr.getSocialHistory_d(), phr.getSocialHistory_v());

            }
        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PM_HeightParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("Height");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("Height");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Height");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPM_Height_d(date);
                phr.setPM_Height_v(value);
                setXmlParsing2(phr.getPM_Height_d(), phr.getPM_Height_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PM_WeightParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("Weight");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("Weight");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Weight");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPM_Weight_d(date);
                phr.setPM_Weight_v(value);
                setXmlParsing2(phr.getPM_Weight_d(), phr.getPM_Weight_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PM_BloodPressureParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("BloodPressure");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("BloodPressure");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("BloodPressure");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPM_BloodPressure_d(date);
                phr.setPM_BloodPressure_v(value);
                setXmlParsing2(phr.getPM_BloodPressure_d(), phr.getPM_BloodPressure_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PM_PulseParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("Pulse");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("Pulse");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Pulse");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPM_Pulse_d(date);
                phr.setPM_Pulse_v(value);
                setXmlParsing2(phr.getPM_Pulse_d(), phr.getPM_Pulse_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void PM_BloodTypeParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("BloodType");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle("BloodType");
                setSubtitle2();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("BloodType");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setPM_BloodType_d(date);
                phr.setPM_BloodType_v(value);
                setXmlParsing2(phr.getPM_BloodType_d(), phr.getPM_BloodType_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void MedicationParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("Medication");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitle2("Medication");
                setSubtitle3();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Medication");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String code = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                String value = node.getChildNodes().item(5).getFirstChild().getNodeValue();
                phr.setMedication_d(date);
                phr.setMedication_c(code);
                phr.setPastHistory_v(value);
                setXmlParsing3(phr.getMedication_d(), phr.getMedication_c(), phr.getPastHistory_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void TeleMedicineParsing() {
        try {
            String xml = Phr;
            //xml 읽어오기
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream istream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            Document doc = builder.parse(istream);

            //xml element 읽어오기
            Element element = doc.getDocumentElement();
            //노드list 선언
            NodeList AdverseReactions = element.getElementsByTagName("TeleMedicine");

            //항목이 0개가 아니면 title 부르기
            if (AdverseReactions.getLength() != 0) {
                setTitleLast("TeleMedicine");
         //       setSubtitleLast();
            }

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("TeleMedicine");//여기 변경
            int count = nodeList.getLength();
            for (int i = 0; i < count; i++) {
                Node node = nodeList.item(i);
                String date = node.getChildNodes().item(1).getFirstChild().getNodeValue();//여기변경
                String value = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                phr.setTeleMedicine_d(date);
                phr.setTeleMedicine_v(value);
                setXmlParsingLast(phr.getTeleMedicine_d(), phr.getTeleMedicine_v());

            }


        } catch (Exception e) {
            failTb();
            //Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //xml 파싱 단게에서 오류 났을 때 출력하는 매소드
    public void failTb() {

        TableRow tr_ti = new TableRow(getActivity());
        tr_ti.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tb1_t = new TextView(getActivity());
        tb1_t.setText("code xml parsing fail");
    }

    public void setTitle(String title) {
        TableRow tr_ti = new TableRow(getActivity());
        tr_ti.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tb1_t = new TextView(getActivity());
        tr_ti.setBackgroundColor(Color.rgb(194, 194, 194));
        tb1_t.setPadding(40, 10, 0, 10);
        tb1_t.setText(title);
        tb1_t.setTextColor(Color.rgb(2, 46, 43));
        tb1_t.setTextSize(23);
        tr_ti.addView(tb1_t);
        tb1.addView(tr_ti, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    public void setTitle2(String title) {
        TableRow tr_ti = new TableRow(getActivity());
        tr_ti.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tb1_t = new TextView(getActivity());
        tr_ti.setBackgroundColor(Color.rgb(194, 194, 194));
        tb1_t.setPadding(40, 10, 0, 10);
        tb1_t.setText(title);
        tb1_t.setTextColor(Color.rgb(2, 46, 43));
        tb1_t.setTextSize(23);
        tr_ti.addView(tb1_t);
        tb2.addView(tr_ti, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    public void setTitleLast(String title) {
        TableRow tr_ti = new TableRow(getActivity());
        tr_ti.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tb1_t = new TextView(getActivity());
        tr_ti.setBackgroundColor(Color.rgb(194, 194, 194));
        tb1_t.setPadding(40, 10, 0, 10);
        tb1_t.setText(title);
        tb1_t.setTextColor(Color.rgb(2, 46, 43));
        tb1_t.setTextSize(23);
        tr_ti.addView(tb1_t);
        tb3.addView(tr_ti, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    //항목 2개짜리(date, value only)
    public void setSubtitle2() {
        TableRow tr_head = new TableRow(getActivity());
        tr_head.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView date = new TextView(getActivity());
        date.setText("Date");
        date.setTextColor(Color.BLACK);
        date.setTextSize(19);
        date.setPadding(40, 10, 0, 10);
        tr_head.addView(date);
        TextView value = new TextView(getActivity());
        value.setText("Value");
        value.setTextColor(Color.BLACK);
        value.setPadding(0, 10, 0, 10);
        value.setTextSize(19);
        tr_head.addView(value);
        tb1.addView(tr_head, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    //항목 2개짜리(date, value only)
    public void setSubtitleLast() {
        TableRow tr_head = new TableRow(getActivity());
        tr_head.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView date = new TextView(getActivity());
        date.setText("Date");
        date.setTextSize(19);
        date.setTextColor(Color.BLACK);
        date.setTextSize(19);
        date.setPadding(40, 10, 0, 10);
        tr_head.addView(date);
        TextView value = new TextView(getActivity());
        value.setText("Value");
        value.setTextColor(Color.BLACK);
        value.setPadding(0, 10, 0, 10);
        value.setTextSize(18);
        tr_head.addView(value);
        tb3.addView(tr_head, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    //항목 2개짜리(date, value only) 파싱
    public void setXmlParsing2(String date, String value) {
        TextView dateView = new TextView(getActivity());
        dateView.setText(date);
        dateView.setTextSize(16);
        dateView.setPadding(40, 0, 0, 0);
        TextView valueView = new TextView(getActivity());
        valueView.setText(value);
        valueView.setTextSize(16);
        valueView.setPadding(0, 0, 0, 0);
        TableRow tr = new TableRow(getActivity());
        tr.setPadding(0, 10, 0, 10);
        tr.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.addView(dateView);
        tr.addView(valueView);
        tb1.addView(tr, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    //항목 3개짜리(date, code, value only)
    public void setSubtitle3() {
        TableRow tr_head = new TableRow(getActivity());
        tr_head.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView date = new TextView(getActivity());
        date.setText("Date");
        date.setTextColor(Color.BLACK);
        date.setTextSize(19);
        date.setPadding(40, 0, 0, 0);
        tr_head.addView(date);
        TextView code = new TextView(getActivity());
        code.setText("Code");
        code.setTextColor(Color.BLACK);
        code.setTextSize(19);
        code.setPadding(0, 0, 0, 0);
        tr_head.addView(code);
        TextView value = new TextView(getActivity());
        value.setText("Value");
        value.setTextColor(Color.BLACK);
        value.setTextSize(19);
        value.setPadding(40, 0, 0, 0);
        tr_head.addView(value);
        tb2.addView(tr_head, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    //항목 3개짜리(date, code, value only) 파싱
    public void setXmlParsing3(String date, String code, String value) {
        TextView dateView = new TextView(getActivity());
        dateView.setPadding(40, 0, 0, 0);
        dateView.setTextSize(16);
        dateView.setText(date);
        TextView codeView = new TextView(getActivity());
        codeView.setPadding(0, 0, 0, 0);
        codeView.setTextSize(16);
        codeView.setText(code);
        TextView valueView = new TextView(getActivity());
        valueView.setPadding(40, 0, 0, 0);
        valueView.setTextSize(16);
        valueView.setText(value);
        TableRow tr = new TableRow(getActivity());
        tr.setPadding(0, 10, 0, 10);
        tr.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.addView(dateView);
        tr.addView(codeView);
        tr.addView(valueView);
        tb2.addView(tr, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setXmlParsingLast(String date, String value) {
        TextView dateView = new TextView(getActivity());
        dateView.setText(date);
        dateView.setTextSize(16);
        dateView.setPadding(40, 0, 0, 0);
        dateView.setTypeface(null, Typeface.BOLD_ITALIC);

        TextView valueView = new TextView(getActivity());
        valueView.setText(value);
        valueView.setTextSize(16);
        valueView.setPadding(40, 0, 0, 0);
        valueView.setMaxLines(20);
        TableRow tr = new TableRow(getActivity());
        TableRow tr2 = new TableRow(getActivity());

        tr.setPadding(0, 10, 0, 10);
        tr.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tr.addView(dateView);
        tr2.addView(valueView);
        tb3.addView(tr, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tb3.addView(tr2, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }
}
