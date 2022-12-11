package com.example.contacttracing.Client;

import com.example.contacttracing.Controller;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CateringFacility extends Controller {

    private final String name;
    private final String city;
    private final String phone;
    private final String busNum; // Business number
    private final String CF; // Unique identifier
    private ArrayList<byte[]> pseudoKeys;


    private String generateQRString(byte[] todayPseudo) throws Exception {
        byte[] Ri = new byte[16];
        new SecureRandom().nextBytes(Ri);

        byte[] combined = new byte[Ri.length + todayPseudo.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < Ri.length ? Ri[i] : todayPseudo[i - Ri.length];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined);

        return Ri.toString()+CF+hash.toString();
    }

//    private void generateQR(String text) throws WriterException, IOException {
//        String charset = "UTF-8";
//        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
//        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//
//        BitMatrix matrix = new MultiFormatWriter().encode(new String(text.getBytes(charset), charset), BarcodeFormat.QR_CODE, 200, 200);
//        //BitMatrix to javaFx
//        int height = matrix.getHeight();
//        int width = matrix.getWidth();
//        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        for (int x = 0; x < width; x++){
//            for (int y = 0; y < height; y++){
//                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
//            }
//        }
//        ImageView qr_image = (ImageView) findViewById(R.id.qrimage);
//        qr_image.setImageBitmap(bmp);
//    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            AtomicInteger today = new AtomicInteger(LocalDateTime.now().getDayOfMonth());

            Runnable getKeys = () -> {
                try {
                    if(pseudoKeys.isEmpty()) {
                        // Clear old keys
                        regImpl.clearDailyAndPseudoKeys(CF);
                        // Derive daily and pseudo keys for the coming 30 days
                        regImpl.deriveKeys(CF);
                        pseudoKeys = regImpl.getPseudoKeys(CF);
                    }

                    if(LocalDateTime.now().getDayOfMonth()!= today.get()) {
                        today.set(LocalDateTime.now().getDayOfMonth());
                        // Get pseudo key of the day
                        byte[] todayPseudo = pseudoKeys.remove(0);
                        //  Generate daily QR string and code
                        String qrText = generateQRString(todayPseudo);
                        System.out.println(qrText);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(getKeys, 0, 5, TimeUnit.SECONDS);   //iedere dag

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CateringFacility(String n,String c,String p, String bN){
        this.name = n;
        this.city = c;
        this.phone = p;
        this.busNum = bN;
        this.CF = n+c+p+bN;
        this.pseudoKeys = new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Name: ");
        String name = input.nextLine();
        System.out.println("City: ");
        String city = input.nextLine();
        System.out.println("Phone: ");
        String phone = input.nextLine();
        System.out.println("busNum: ");
        String busNum = input.nextLine();

        CateringFacility main = new CateringFacility(name, city, phone, busNum);
        main.run();
    }
}
