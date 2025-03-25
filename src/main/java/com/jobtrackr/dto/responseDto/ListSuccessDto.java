package com.jobtrackr.dto.responseDto;

import java.util.List;

public class ListSuccessDto <T>{
    public int status;
    public String message;
    public List<T> data;

    public ListSuccessDto() {
    }

    public ListSuccessDto(int status, String message, List<T> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
