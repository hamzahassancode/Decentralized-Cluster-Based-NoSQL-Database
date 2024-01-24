package com.example.NodeVM.model;

public class Response {
    public enum Status {
        SUCCESS,
        BAD_REQUEST,
        NOT_FOUND,
        INTERNAL_ERROR
    }

    private Status status;
    private String message;

    public Response() {
    }

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
