package com.jarry.app.model;

/**
 * Created by Administrator on 2018/2/22.
 */

public class Welcome {
    private String title;
    private boolean fill = true;

    public Welcome(String title) {
        this.title = title;
        fill = true;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
