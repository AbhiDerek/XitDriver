package com.app.xit.home.signature;

public class BeanManager {

    private static BeanManager instance = null;
    private Object imagePath;

    private BeanManager() {
    }

    public static BeanManager getInstance() {
        if (instance == null) {
            Object object = new Object();
            synchronized (object) {
                instance = new BeanManager();
            }
        }
        return instance;
    }


    public Object getImagePath() {
        return imagePath;
    }

    public void setImagePath(Object imagePath) {
        this.imagePath = imagePath;
    }
}