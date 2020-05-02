package com.example.basiqueforum;

public class Model {

    private String title, ctn, img;

    public Model() {

    }

    public Model(String title, String ctn, String img) {
        this.title = title;
        this.ctn = ctn;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCtn() {
        return ctn;
    }

    public void setCtn(String ctn) {
        this.ctn = ctn;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
