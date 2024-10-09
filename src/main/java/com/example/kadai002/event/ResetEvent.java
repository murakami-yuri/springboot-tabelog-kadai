package com.example.kadai002.event;

import org.springframework.context.ApplicationEvent;

import com.example.kadai002.entity.User;

import lombok.Getter;

@Getter
public class ResetEvent extends ApplicationEvent {
	private User user;
    private String requestUrl;

    public ResetEvent(Object source, User user, String requestUrl) {
        super(source);

        this.user = user;
        this.requestUrl = requestUrl;
    }
}
