package com.nian.receiver;

import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;

public class SimpleSMSReceiver {

    /**
     * Parameters used for connecting to SMSC (or SMPPSim)
     */
    private Session session = null;
    //    private String ipAddress = "192.168.43.28";
    private String ipAddress = "localhost";
    private String systemId = "smppclient1";
    private String password = "password";
    private int port = 2775;
//    private int port = 88;

    /**
     * z
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Sms receiver starts");

        SimpleSMSReceiver objSimpleSMSReceiver = new SimpleSMSReceiver();
        objSimpleSMSReceiver.bindToSmsc();

        while (true) {
            objSimpleSMSReceiver.receiveSms();
        }
    }

    private void bindToSmsc() {
        try {
            // setup connection
            TCPIPConnection connection = new TCPIPConnection(ipAddress, port);
            connection.setReceiveTimeout(20 * 1000);
            session = new Session(connection);

            // set request parameters
            BindRequest request = new BindReceiver();
            request.setSystemId(systemId);
            request.setPassword(password);

            // send request to bind
            BindResponse response = session.bind(request);
            if (response.getCommandStatus() == Data.ESME_ROK) {
                System.out.println("Sms receiver is connected to SMPPSim.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveSms() {
        try {

            PDU pdu = session.receive(1500);

            if (pdu != null) {
                DeliverSM sms = (DeliverSM) pdu;

                if ((int) sms.getDataCoding() == 0) {
                    //message content is English
                    System.out.println("***** New Message Received *****");
                    System.out.println("From: " + sms.getSourceAddr().getAddress());
                    System.out.println("To: " + sms.getDestAddr().getAddress());
                    System.out.println("Content: " + sms.getShortMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
