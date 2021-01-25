package com.app2.engine.service;

public interface HRDataService {
    void region(String date); //HRREGION.TXT
    void section(String date);//HRSECTION.TXT
    void position(String date);//HRPOSITION.TXT
    void branch(String date);//HRBRANCH.TXT
    void lineBusiness(String date);//HRDIV.TXT
    void unit(String date);//HRUNIT.TXT
    void orgGroup(String date);//HRBUSILINE.TXT
    void company(String date);//HRMAINSTR.TXT
    void hrInterface(String date);//HRCOMPANYREL.TXT

}
