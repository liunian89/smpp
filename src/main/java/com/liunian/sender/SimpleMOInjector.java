package com.liunian.sender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleMOInjector {
    //    private String ipAddress = "192.168.43.173";
    private String ipAddress = "localhost";
    private int port = 88;
    private String shortMessage = "abcdefg";
    private String sourceAddress = "1234";
    private String destinationAddress = "5678";

    public static void main(String[] args) {
        SimpleMOInjector objSimpleSmsSender = new SimpleMOInjector();

        String urlString = "";
        urlString = objSimpleSmsSender.paramToUrlString();
        objSimpleSmsSender.sendSingleSMS(urlString);

        System.out.println("Program terminated");
    }

    public String paramToUrlString() {
        String result = "http://" + ipAddress + ":" + port + "/inject_mo?";

        result = result + "short_message=" + shortMessage;
        result = result + "&source_addr=" + sourceAddress;
        result = result + "&destination_addr=" + destinationAddress;

        result = result + "&submit=Submit+Message&service_type=&source_addr_ton=1&source_addr_npi=1&dest_addr_ton=1&dest_addr_npi=1&esm_class=0&protocol_ID=&priority_flag=&registered_delivery_flag=0&data_coding=0&user_message_reference=&source_port=&destination_port=&sar_msg_ref_num=&sar_total_segments=&sar_segment_seqnum=&user_response_code=&privacy_indicator=&payload_type=&message_payload=&callback_num=&source_subaddress=&dest_subaddress=&language_indicator=&tlv1_tag=&tlv1_len=&tlv1_val=&tlv2_tag=&tlv2_len=&tlv2_val=&tlv3_tag=&tlv3_len=&tlv3_val=&tlv4_tag=&tlv4_len=&tlv4_val=&tlv5_tag=&tlv5_len=&tlv5_val=&tlv6_tag=&tlv6_len=&tlv6_val=&tlv7_tag=&tlv7_len=&tlv7_val=";

        return result;
    }

    public void sendSingleSMS(String urlString) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            System.out.println("SMS sent");
        } catch (Exception e) {
            System.out.println("Failed to send SMS");
            e.printStackTrace();
        }
    }
}
