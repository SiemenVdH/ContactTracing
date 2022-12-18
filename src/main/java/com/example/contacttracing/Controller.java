package com.example.contacttracing;

import com.example.contacttracing.Client.UserApp;
import com.example.contacttracing.Client.Doctor;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Controller {

    @FXML
    private TextField Enroll;
    @FXML
    private Label enrollStatus;
    @FXML
    private TextArea QRString;
    @FXML
    private Pane color1;
    @FXML
    private Pane color2;
    @FXML
    private Pane color3;
    @FXML
    private Label logsPrinted;
    @FXML
    private Label leaveStamp;


    @FXML
    protected void onClickEnroll() {
        new UserApp(Enroll.getCharacters().toString());
        setLabelEnrol(UserApp.getEnrolStatus());
    }
    @FXML
    protected void setLabelEnrol(String status) {enrollStatus.setText(status);}
    @FXML
    protected void onClickScanQr() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        String[] result = UserApp.registerEntry(QRString.getText());
        setColor1(result[0]+result[1]+result[2]);
        setColor2(result[3]+result[4]+result[5]);
        setColor3(result[6]+result[7]+result[8]);
    }
    @FXML
    protected void onClickPrint() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, RemoteException {
        String status = UserApp.printLogs();
        setLabelPrint(status);
        new Doctor();
        Doctor.readLogs();
    }
    @FXML
    protected void setLabelPrint(String status) {logsPrinted.setText(status);}
    @FXML
    protected void onClickLeave(){
        String status = UserApp.leaveFacility();
        setLabelLeave(status);
    }
    @FXML
    protected void setLabelLeave(String status) {leaveStamp.setText(status);}
    @FXML
    protected void setColor1(String color) {color1.setStyle("-fx-background-color: #" + color);}
    @FXML
    protected void setColor2(String color) {color2.setStyle("-fx-background-color: #" + color);}
    @FXML
    protected void setColor3(String color) {color3.setStyle("-fx-background-color: #" + color);}
}