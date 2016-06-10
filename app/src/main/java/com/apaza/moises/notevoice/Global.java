package com.apaza.moises.notevoice;

import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class Global {

    public static ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

    public static Drawable getTextDrawable(String text){
        int color = colorGenerator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(text.substring(0,1), color, 5);
        return drawable;
    }
}
