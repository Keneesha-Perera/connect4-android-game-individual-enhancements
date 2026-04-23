package com.example.connect_four;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

public class ColorPickerDialog {

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    private Context context;
    private OnColorSelectedListener listener;
    private int selectedColor;

    public ColorPickerDialog(Context context, OnColorSelectedListener listener, int initialColor) {
        this.context = context;
        this.listener = listener;
        this.selectedColor = initialColor;
    }

    public void show() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_color_picker);

        GridLayout colorGrid = dialog.findViewById(R.id.color_grid);
        int[] colors = {
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
                Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.DKGRAY,
                Color.WHITE, Color.BLACK
        };

        for (int color : colors) {
            Button colorButton = new Button(context);
            colorButton.setBackgroundColor(color);
            colorButton.setLayoutParams(new GridLayout.LayoutParams());
            colorButton.setOnClickListener(v -> {
                selectedColor = color;
                listener.onColorSelected(selectedColor);
                dialog.dismiss();
            });
            colorGrid.addView(colorButton);
        }

        dialog.show();
    }
}
