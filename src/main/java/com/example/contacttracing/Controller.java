package com.example.contacttracing;

import com.example.contacttracing.Client.UserApp;
import com.sun.javafx.fxml.expression.Expression;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.shape.Polyline;

import java.io.IOException;
import java.awt.*;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Controller {

    protected static Expression<Object> Polyline;
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
    protected static Polyline poly1;

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
    protected void onClickScanQr1() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF1.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr2() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF2.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr3() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF3.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr4() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF4.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr5() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF5.getCharacters().toString());
    }
    @FXML
    protected void onClickScanQr6() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        UserApp.registerEntry(CF6.getCharacters().toString());
    }
    protected static void pictureFrame(byte[] bArray){
        //Image image = SwingFXUtils.toFXImage(bArray, null);
    };
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
    @FXML
    protected void onClickPrint1(){
        UserApp.printLogs();
    }
    @FXML
    protected void onClickPrint2(){
        UserApp.printLogs();
    }
    @FXML
    protected void onClickPrint3(){
        UserApp.printLogs();
    }
    @FXML
    protected void onClickPrint4(){
        UserApp.printLogs();
    }
    @FXML
    protected void onClickPrint5(){
        UserApp.printLogs();
    }
    @FXML
    protected void onClickPrint6(){
        UserApp.printLogs();
    }
}