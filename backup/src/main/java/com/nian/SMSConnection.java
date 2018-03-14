package com.nian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.*;
import org.smpp.pdu.*;

import java.io.IOException;
import java.util.Properties;

public class SMSConnection {

    public final int SMPP_VERSION = 0x34;
    private static Logger logger = LoggerFactory.getLogger(SMSConnection.class
            .getName());

    private Session smscSession = null;
    private boolean isBounded1 = false;
    String scheduleDeliveryTime = "";
    String validityPeriod = "";
    byte replaceIfPresentFlag = 0;
    byte esmClass = 0;
    byte protocolId = 0;
    byte priorityFlag = 0;
    byte registeredDelivery = 0;
    byte dataCoding = 0;
    byte smDefaultMsgId = 0;

    public synchronized Session getCurrentConnection(Properties prop) {

        String host = "";
        int port = 0;
        long timeout = 6000;

        // Get the properties value from config file
        host = prop.getProperty("host");
        port = Integer.parseInt(prop.getProperty("port"));

        if (smscSession != null) {
            return smscSession;

        } else {
            SMSConnection smsConnection = new SMSConnection();
            // Making connection to the SMSC server
            smscSession = smsConnection.createConnection(host, port, timeout);
            return smscSession;

        }

    }

    // Creating the connection with SMSC server

    public Session createConnection(String host, int port, long timeout) {
        try {

            TCPIPConnection connection = new TCPIPConnection(host, port);
            connection.setReceiveTimeout(timeout);
            connection.setIOBufferSize(8188);
            connection.setReceiveBufferSize(8188);
            smscSession = new Session(connection);
            // Getting the session for connection with SMSC server
            if (smscSession == null) {
                logger.error("Unable to get the connection, Check HostName and port details");
                System.exit(1);
            }
            logger.debug("Created Session object " + smscSession);
        } catch (Exception e) {
            logger.error("Error in connection" + e.getMessage());
        }
        return smscSession;
    }

    // Binding with SMSC server

    public boolean bind(String systemId, String systemType, String password,
                        Session smscSession) {

        BindResponse response = null;
        BindRequest request = new BindTransmitter();

        try {

            request.setSystemId(systemId);
            request.setPassword(password);

            if (systemType != null && systemType.equals("")) {
                request.setSystemType(systemType);
            }

            request.setInterfaceVersion((byte) (SMPP_VERSION));
            logger.debug("Bind request " + request.debugString());
            response = smscSession.bind(request);
            logger.debug("Bind response " + response.debugString());

            if (response.getCommandStatus() == Data.ESME_ROK) {
                logger.debug("Binded with SMSC Server");
                isBounded1 = true;
            } else {
                logger.error("Unable to bind with SMSC Server");
            }
            Integer respCode = new Integer(response.getCommandStatus());
            logger.debug("SMSC -- Response Code" + respCode);
            response.setCommandStatus(respCode);
            Integer comLength = new Integer(response.getCommandLength());
            logger.debug("SMSC -- CommandLength" + comLength);
            response.setCommandLength(comLength);
            logger.debug("Response from SMSC" + response.toString());

        } catch (WrongLengthOfStringException e) {
            logger.error("Wrong length string exception" + e.getMessage());

        } catch (ValueNotSetException e) {
            logger.error("Value not set exception" + e.getMessage());

        } catch (TimeoutException e) {
            logger.error("Timeout exception " + e.getMessage());

        } catch (PDUException e) {
            logger.error("PDU exception " + e.getMessage());

        } catch (WrongSessionStateException e) {
            logger.error("Wrong Session exception " + e.getMessage());

        } catch (IOException e) {
            logger.error("Could not able to connect the host/port or Check the Username/Password for connection"
                    + e.getMessage());
        }
        return isBounded1;
    }

    // Unbinding from SMSC server

    public void unbind(boolean isBounded, Session smscSession) {
        try {
            if (!isBounded) {
                logger.debug("Not bound, cannot unbind.");
                return;
            }
            // send the request
            // logger.debug("Going to unbind.");
            if (smscSession.getReceiver().isReceiver()) {
                logger.debug("It can take a while to stop the receiver.");
            }
            UnbindResp response = smscSession.unbind();
            logger.debug("Unbind response " + response.debugString());
        } catch (Exception e) {
            logger.debug("Unbind operation failed. " + e.getMessage());
        }
    }

    // Submitting message sending request

    public boolean submit(String destAddress, String shortMessage,
                          String sender, byte senderTon, byte senderNpi, String serviceType,
                          boolean isBounded, Session smsSession) {

        try {

            if (isBounded == true) {
                SubmitSM request = new SubmitSM();
                SubmitSMResp response;
                // Set values
                request.setServiceType(serviceType);

                if (sender.startsWith("+")) {
                    sender = sender.substring(1);
                    senderTon = 1;
                    senderNpi = 1;
                }
                if (!sender.matches("\\d+")) {
                    senderTon = 5;
                    senderNpi = 0;
                }
                if (senderTon == 5) {
                    request.setSourceAddr(new Address(senderTon, senderNpi,
                            sender, 11));
                } else {
                    request.setSourceAddr(new Address(senderTon, senderNpi,
                            sender));
                }

                if (destAddress.startsWith("+")) {
                    destAddress = destAddress.substring(1);
                }
                request.setDestAddr(new Address((byte) 1, (byte) 1, destAddress));
                request.setReplaceIfPresentFlag(replaceIfPresentFlag);
                request.setShortMessage(shortMessage, Data.ENC_GSM7BIT);
                request.setScheduleDeliveryTime(scheduleDeliveryTime);
                request.setValidityPeriod(validityPeriod);
                request.setEsmClass(esmClass);
                request.setProtocolId(protocolId);
                request.setPriorityFlag(priorityFlag);
                request.setRegisteredDelivery(registeredDelivery);
                request.setDataCoding(dataCoding);
                request.setSmDefaultMsgId(smDefaultMsgId);

                // Send the request

                request.assignSequenceNumber(true);
                logger.debug("Submit request " + request.debugString());
                response = smsSession.submit(request);
                // response = smsSession.submitMulti(request);
                logger.debug("Submit response " + response.debugString());
                String messageId = response.getMessageId();
                logger.debug("Message ID = " + messageId);

            } else {
                isBounded = false;
                logger.error("Cannot process request in unbounded session");
            }
        } catch (Exception e) {
            isBounded = false;
            logger.error("Submit operation failed. " + e.getMessage());
        }
        return isBounded;

    }

}
