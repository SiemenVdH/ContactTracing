package com.example.contacttracing;

import com.example.contacttracing.Client.UserApp;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField Enroll1;
    @FXML
    private TextField Enroll2;
    @FXML
    private TextField Enroll3;
    @FXML
    private TextField Enroll4;
    @FXML
    private TextField Enroll5;
    @FXML
    private TextField Enroll6;
    @FXML
    private TextField CF1;
    @FXML
    private TextField CF2;
    @FXML
    private TextField CF3;
    @FXML
    private TextField CF4;
    @FXML
    private TextField CF5;
    @FXML
    private TextField CF6;

    @FXML
    protected void onClickEnroll1(){
        new UserApp(Enroll1.getCharacters().toString());
    }
    @FXML
    protected void onClickEnroll2(){
        new UserApp(Enroll2.getCharacters().toString());
    }
    @FXML
    protected void onClickEnroll3(){
        new UserApp(Enroll3.getCharacters().toString());
    }
    @FXML
    protected void onClickEnroll4(){
        new UserApp(Enroll4.getCharacters().toString());
    }
    @FXML
    protected void onClickEnroll5(){
        new UserApp(Enroll5.getCharacters().toString());
    }
    @FXML
    protected void onClickEnroll6(){
        new UserApp(Enroll6.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr1(){
        CF1.getCharacters().toString();
    }
    @FXML
    protected void onClickScanQr2(){
        CF2.getCharacters().toString();
    }
    @FXML
    protected void onClickScanQr3(){
        CF3.getCharacters().toString();
    }
    @FXML
    protected void onClickScanQr4(){
        CF4.getCharacters().toString();
    }
    @FXML
    protected void onClickScanQr5(){
        CF5.getCharacters().toString();
    }
    @FXML
    protected void onClickScanQr6(){
        CF6.getCharacters().toString();
    }
    @FXML
    protected void onClickLeave1(){

    }
    @FXML
    protected void onClickLeave2(){

    }
    @FXML
    protected void onClickLeave3(){

    }
    @FXML
    protected void onClickLeave4(){

    }
    @FXML
    protected void onClickLeave5(){

    }
    @FXML
    protected void onClickLeave6(){

    }
}