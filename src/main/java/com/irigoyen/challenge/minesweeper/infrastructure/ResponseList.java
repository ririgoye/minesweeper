package com.irigoyen.challenge.minesweeper.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Same concept as Response class but used to handle lists
 */

@Getter
@Setter
public class ResponseList<T> extends Response<List<T>>
{
    private long totalElements;

    public ResponseList()
    {
        super();
    }

    public ResponseList(Collection<T> payload)
    {
        this();
        setPayload(payload);
    }

    public ResponseList(Exception ex, String message)
    {
        this();
        setStatus(ex, message);
    }

    public void setPayload(Collection<T> payload)
    {
        if (payload == null)
            super.setPayload(null);
        else if (payload instanceof List)
            super.setPayload((List<T>) payload);
        else{
            List<T> newPL = new ArrayList<T>(payload);
            super.setPayload(newPL);
        }
    }

    public ResponseList<T> setStatus(HttpStatus status, String message)
    {
        super.setStatus(status,message);
        return this;
    }
}
