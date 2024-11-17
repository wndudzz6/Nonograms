package com.example.nonograms;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;

public class Cell extends AppCompatButton {
    private boolean blackSquare = false;
    private boolean checked = false;
    private MainActivity mainActivity;
    private final int row, col;

    public Cell(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
        initCell();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void initCell() {
        setBackgroundResource(R.drawable.cell_selector);

        setOnClickListener(v -> {
            if (!mainActivity.isCleared()) {
                blackSquare = !blackSquare;
                setBackgroundColor(blackSquare ? 0xFF000000 : 0xFFFFFFFF);
                if (blackSquare) {
                    if (!mainActivity.correctCells.contains(row + "," + col)) {
                        mainActivity.updateLife(-1);
                    }
                    mainActivity.updateRemainingBlackSquares(-1);
                } else {
                    mainActivity.updateRemainingBlackSquares(1);
                }
            }
        });

        setOnLongClickListener(v -> {
            if (!mainActivity.isCleared()) {
                checked = !checked;
                setText(checked ? "X" : "");
            }
            return true;
        });
    }
}
