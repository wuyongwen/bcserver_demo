package com.cyberlink.cosmetic.modules.sms.model.telesign;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.core.BeanLocator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(value = Include.NON_NULL)
public class VerifyResponse {

    public String reference_id;

    public String resource_uri;

    public String sub_resource;

    public Error[] errors;

    public Status status;

    public Device device;

    public App app;

    public Call_forwarding call_forwarding;

    public Verify verify;

    public UserResponse user_response;

    private final ObjectMapper mapper = BeanLocator.getBean("web.objectMapper");

    public static class Error {

        public int code;

        public String description;
    }

    public static class Status {

        public String updated_on;

        public int code;

        public String description;
    }

    public static class Device {

        public String phone_number;

        public String operating_system;

        public String language;
    }

    public static class App {

        public String signature;

        public String created_on_utc;
    }

    public static class Call_forwarding {

        public String action;

        public String call_forward;
    }

    public static class Verify {

        public String code_state;

        public String code_entered;

        public String code_expected;
    }

    public static class UserResponse {

        public String received;

        public String verification_code;

        public String selection;

    }

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }
}
