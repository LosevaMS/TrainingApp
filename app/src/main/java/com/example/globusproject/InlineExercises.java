package com.example.globusproject;

public class InlineExercises {
    private String name, uri;

    public InlineExercises(String name, String uri){
        this.name = name;
        this.uri = uri;
    }
    public InlineExercises(String name){
        this.name = name;
        uri=null;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getUri(){
        return uri;
    }
    public void setUri(String uri){
        this.uri = uri;
    }
}
