package com.example.globusproject;

public class InlineExercises {
    private String name, uri;
    private boolean isSelect = false;

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

    public boolean isSelect(){
        return isSelect;
    }
    public void setSelect(boolean select){
        isSelect = select;
    }
}
