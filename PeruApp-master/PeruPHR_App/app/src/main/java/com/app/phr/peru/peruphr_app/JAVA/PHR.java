package com.app.phr.peru.peruphr_app.JAVA;

import java.util.ArrayList;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by hansol on 2016-08-17.
 */

public class PHR {
    //Todo 임신주기 일단 주석
    //private String PregnancyDueDate_d, PregnancyDueDate_v;
    private String Allergy_d, Allergy_v;
    private String AdverseReaction_d, AdverseReaction_v;
    private String PastHistory_d, PastHistory_v;
    private String FamilyHistory_d, FamilyHistory_v;
    private String SocialHistory_d, SocialHistory_v;
    private String PM_Height_d, PM_Height_v;
    private String PM_Weight_d, PM_Weight_v;
    private String PM_BloodPressure_d, PM_BloodPressure_v;
    private String PM_Pulse_d, PM_Pulse_v;
    private String PM_BloodType_d, PM_BloodType_v;
    private String Medication_d, Medication_c, Medication_v;
    private String TeleMedicine_d, TeleMedicine_v;
    //todo comment 주석
    //private String PatientComment;


    //public void setPregnancyDueDate_d(String pregnancyDueDate_d) {this.PregnancyDueDate_d = pregnancyDueDate_d;}
    //public void setPregnancyDueDate_v(String pregnancyDueDate_v) {this.PregnancyDueDate_v = pregnancyDueDate_v;}
    public void setAllergy_d(String allergy_d) {this.Allergy_d = allergy_d;}
    public void setAllergy_v(String allergy_v) {this.Allergy_v = allergy_v;}
    public void setAdverseReaction_d(String adverseReaction_d) {this.AdverseReaction_d = adverseReaction_d;}
    public void setAdverseReaction_v(String adverseReaction_v) {this.AdverseReaction_v = adverseReaction_v;}
    public void setPastHistory_d(String pastHistory_d) {this.PastHistory_d = pastHistory_d;}
    public void setPastHistory_v(String pastHistory_v) {this.PastHistory_v = pastHistory_v;}
    public void setFamilyHistory_d(String familyHistory_d) {this.FamilyHistory_d = familyHistory_d;}
    public void setFamilyHistory_v(String familyHistory_v) {this.FamilyHistory_v = familyHistory_v;}
    public void setSocialHistory_d(String socialHistory_d) {this.SocialHistory_d = socialHistory_d;}
    public void setSocialHistory_v(String socialHistory_v) {this.SocialHistory_v = socialHistory_v;}
    public void setPM_Height_d(String pm_height_d) {this.PM_Height_d = pm_height_d;}
    public void setPM_Height_v(String pm_height_v) {this.PM_Height_v = pm_height_v;}
    public void setPM_Weight_d(String pm_weight_d) {this.PM_Weight_d = pm_weight_d;}
    public void setPM_Weight_v(String pm_weight_v) {this.PM_Weight_v = pm_weight_v;}
    public void setPM_BloodPressure_d(String pm_bloodPressure_d) {this.PM_BloodPressure_d = pm_bloodPressure_d;}
    public void setPM_BloodPressure_v(String pm_bloodPressure_v) {this.PM_BloodPressure_v = pm_bloodPressure_v;}
    public void setPM_Pulse_d(String pm_pulse_d) {this.PM_Pulse_d = pm_pulse_d;}
    public void setPM_Pulse_v(String pm_pulse_v) {this.PM_Pulse_v = pm_pulse_v;}
    public void setPM_BloodType_d(String bloodType_d) {this.PM_BloodType_d = bloodType_d;}
    public void setPM_BloodType_v(String bloodType_v) {this.PM_BloodType_v = bloodType_v;}
    public void setMedication_d(String medication_d) {this.Medication_d = medication_d;}
    public void setMedication_c(String medication_c) {this.Medication_c = medication_c;}
    public void setMedication_v(String medication_v) {this.Medication_v = medication_v;}
    public void setTeleMedicine_d(String teleMedicine_d) {this.TeleMedicine_d = teleMedicine_d;}
    public void setTeleMedicine_v(String teleMedicine_v) {this.TeleMedicine_v = teleMedicine_v;}
    //public void setPatientComment(String patientComment) {this.PatientComment = patientComment;}

    //public String getPregnancyDueDate_d() {return  this.PregnancyDueDate_d;}
    //public String getPregnancyDueDate_v() {return  this.PregnancyDueDate_v;}
    public String getAllergy_d() {return this.Allergy_d;}
    public String getAllergy_v() {return this.Allergy_v;}
    public String getAdverseReaction_d() {return this.AdverseReaction_d;}
    public String getAdverseReaction_v() {return this.AdverseReaction_v;}
    public String getPastHistory_d() {return this.PastHistory_d;}
    public String getPastHistory_v() {return this.PastHistory_v;}
    public String getFamilyHistory_d() {return this.FamilyHistory_d;}
    public String getFamilyHistory_v() {return this.FamilyHistory_v;}
    public String getSocialHistory_d() {return this.SocialHistory_d;}
    public String getSocialHistory_v() {return this.SocialHistory_v;}
    public String getPM_Height_d() {return this.PM_Height_d;}
    public String getPM_Height_v() {return this.PM_Height_v;}
    public String getPM_Weight_d() {return this.PM_Weight_d;}
    public String getPM_Weight_v() {return this.PM_Weight_v;}
    public String getPM_BloodPressure_d() {return this.PM_BloodPressure_d;}
    public String getPM_BloodPressure_v() {return this.PM_BloodPressure_v;}
    public String getPM_Pulse_d() {return this.PM_Pulse_d;}
    public String getPM_Pulse_v() {return this.PM_Pulse_v;}
    public String getPM_BloodType_d() {return this.PM_BloodType_d;}
    public String getPM_BloodType_v() {return this.PM_BloodType_v;}
    public String getMedication_d() {return this.Medication_d;}
    public String getMedication_c() {return this.Medication_c;}
    public String getMedication_v() {return this.Medication_v;}
    public String getTeleMedicine_d() {return this.TeleMedicine_d;}
    public String getTeleMedicine_v() {return this.TeleMedicine_v;}
    //public String getPatientComment() {return this.PatientComment;}

}