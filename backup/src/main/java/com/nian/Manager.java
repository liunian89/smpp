package com.nian;

import org.smpp.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Manager {


    @RequestMapping("/")
    public String send() {
        SMSConnection smsConnection = new SMSConnection();

        Session session = smsConnection.createConnection("localhost", 88, 1000);

        smsConnection.bind("smppclient1", null, "password", session);


        System.out.println();
        return "SENT!";
    }
}
