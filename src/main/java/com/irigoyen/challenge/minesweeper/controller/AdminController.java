package com.irigoyen.challenge.minesweeper.controller;

import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class AdminController {

    /**
     * Simple failsafe endpoint used to test if the service is running
     * @return current time
     */
    @GetMapping("/time")
    public Response<String> hello()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = "Hello, current time is: "+ dateFormat.format(date);
        return new Response<>(time);
    }
}
