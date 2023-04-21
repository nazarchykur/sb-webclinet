package com.example.sbwebclient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostDto {
    public int id;
    public int userId;
    public String title;
    public String body;
}
