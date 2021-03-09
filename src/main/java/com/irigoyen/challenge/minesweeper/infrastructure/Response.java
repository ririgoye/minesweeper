package com.irigoyen.challenge.minesweeper.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;


/**
 * This is a response wrapper for all the API calls.
 * It includes additional information besides just the api call result.
 * It also allows better error handling than just throwing exceptions.
 */
@Getter
@Setter
public class Response<T>
{
    private HttpStatus status;
    private String message;
    private T payload;

    public Response(HttpStatus status, String message)
    {
        setStatus(status, message);
    }

    public Response()
    {
        this(OK);
    }

    public Response(HttpStatus status)
    {
        this(status,"");
    }

    public Response(T payload)
    {
        this();
        setPayload( payload);
    }

    //Initialize the response with response code, message, description...
    public Response<T> setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public Response<T> addMessage(String message)
    {
        if (Strings.isBlank(this.message))
            this.message = message;
        else
            this.message += "\n" + message;
        return this;
    }

    public Response<T> setPayload(T payload) {

        this.payload = payload;
        //by default payload is null, and it is ok to return that
        //BUT if it SET to null it means we where expecting a result.
        if (payload == null) {
            setStatus(HttpStatus.NOT_FOUND);
        }
        return this;
    }
    
    public Response<T> setStatus(HttpStatus status)
    {
        this.status = status;
        return this;
    }

    public Response<T> setStatus(HttpStatus status, String message)
    {
        setStatus(status);
        this.message = message;
        return this;
    }

    public Response<T> setStatus(Exception ex, String message)
    {
        setStatus(HttpStatus.INTERNAL_SERVER_ERROR, message + " - " + ex.toString());
        return this;
    }

    public boolean isOK()
    {
        return status.series() == HttpStatus.Series.SUCCESSFUL;
    }

    public Response<T> notFound(String message) {
        setPayload(null);
        setMessage(message);
        return this;
    }
}
