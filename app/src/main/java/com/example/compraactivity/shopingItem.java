package com.example.compraactivity;

public class shopingItem {
    private String text;
    private boolean checked;

    public shopingItem(String text) {
        this.text = text;
        this.checked = false;
    }

    public shopingItem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toogleChecked() {
        this.checked = ! this.checked;
    }
}
