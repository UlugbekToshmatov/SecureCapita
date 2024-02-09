package com.example.SecureCapitaInitializr.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsUtils {
    public static final String FROM_NUMBER = "+998901362576";
    public static final String SID = "";
    public static final String TOKEN = "";

    public static void sendSms(String to, String message) {
        log.info("\nMessage '{}' sent to +{}", message, to);
        Twilio.init(SID, TOKEN);
        Message.creator(new PhoneNumber("+" + to), new PhoneNumber(FROM_NUMBER), message).create();
    }
}
