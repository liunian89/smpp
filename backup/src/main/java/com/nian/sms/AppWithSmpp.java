package com.nian.sms;

import com.seleniumsoftware.SMPPSim.SMPPSim;
import org.smpp.smscsim.Simulator;

import java.io.IOException;

public class AppWithSmpp {

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String[] smppArgs = new String[1];
        smppArgs[0] = "conf/smppsim.props";
//        Simulator.main(smppArgs);

        SMPPSim smppSim = new SMPPSim();
        smppSim.start("conf/smppsim.props");


    }
}
