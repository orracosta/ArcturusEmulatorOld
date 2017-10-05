package com.habboproject.server.game.rooms.objects.items.data;

public class BackgroundTonerData {
    private int hue;
    private int saturation;
    private int lightness;

    public BackgroundTonerData(int hue, int saturation, int lightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.lightness = lightness;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getLightness() {
        return lightness;
    }

    public void setLightness(int lightness) {
        this.lightness = lightness;
    }

    public static BackgroundTonerData get(String extradata) {
        if (!extradata.contains(";#;")) {
            return null;
        }

        String[] data = extradata.split(";#;");

        if (data.length < 3) {
            return null;
        }

        return new BackgroundTonerData(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    }

    public static String get(BackgroundTonerData data) {
        return data.getHue() + ";#;" + data.getSaturation() + ";#;" + data.getLightness();
    }
}
