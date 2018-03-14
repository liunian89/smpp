package com.liunian.sender;


import org.smpp.Connection;
import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;

/**
 * In order to use transmitter, the LOOP_BACK must be set to TRUE in the property file.
 */
public class SimpleSMSTransmitter {

    private Session session = null;
//    private String ipAddress = "192.168.212.130";
    private String ipAddress = "localhost";
    private String systemId = "smppclient1";
    private String password = "password";
    private int port = 2775;
    private String shortMessage = "message from transmitter";
    private String sourceAddress = "1234";
    private String destinationAddress = "5678";

    public static void main(String[] args) throws InterruptedException {

        SimpleSMSTransmitter objSimpleSMSTransmitter = new SimpleSMSTransmitter();
        objSimpleSMSTransmitter.bindToSMSC();
        objSimpleSMSTransmitter.sendSingleSMS();

        System.out.println("Program terminated");
        Thread.sleep(5000l);
//        System.exit(0);
    }

    public void bindToSMSC() {
        try {
            Connection conn = new TCPIPConnection(ipAddress, port);
            session = new Session(conn);

            BindRequest breq = new BindTransmitter();
            breq.setSystemId(systemId);
            breq.setPassword(password);
            BindTransmitterResp bresp = (BindTransmitterResp) session.bind(breq);

            if (bresp.getCommandStatus() == Data.ESME_ROK) {
                System.out.println("Connected to SMSC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSingleSMS() {
        try {
            SubmitSM request = new SubmitSM();

            // set values
            request.setSourceAddr(sourceAddress);
            request.setDestAddr(destinationAddress);
            request.setShortMessage(shortMessage);

            // send the request
            SubmitSMResp resp = session.submit(request);

            if (resp.getCommandStatus() == Data.ESME_ROK) {
                System.out.println("Message submitted....");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to submit message....");
        }
    }
}
