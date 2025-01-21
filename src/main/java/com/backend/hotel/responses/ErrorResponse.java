package com.backend.hotel.responses;

import java.util.HashMap;

public class ErrorResponse extends Response<HashMap<String, String>>{

    public void set(String message) {
        this.status = "error";
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        this.data = response;
    }
}
