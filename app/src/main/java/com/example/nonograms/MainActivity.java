package com.example.nonograms;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int TOTAL_SIZE = 8;
    private static final int BUTTON_SIZE = 150;
    private static final int REQUIRED_BLACK_SQUARES = 15;
    private static final int MAX_LIFE = 3;
    private int remainingBlackSquares = REQUIRED_BLACK_SQUARES;
    private int life = MAX_LIFE;

    private Button blackSquaresButton;
    private TextView lifeTextView, remainingSquaresTextView;
    private TableLayout tableLayout;
    private boolean isCleared = false;

    private final String[][] rowHints = {
            {" ", "1", "1"},
            {" ", "2", "2"},
            {" ", " ", "5"},
            {" ", " ", "3"},
            {" ", " ", "1"}
    };

    private final String[][] colHints = {
            {" ", " ", "2"},
            {" ", " ", "4"},
            {" ", " ", "3"},
            {" ", " ", "4"},
            {" ", " ", "2"}
    };

    public final Set<String> correctCells = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCorrectCells();

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);

        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setGravity(Gravity.START);
        topLayout.setPadding(10, 10, 10, 10);

        lifeTextView = new TextView(this);
        lifeTextView.setText("Life: " + life);
        lifeTextView.setPadding(10, 0, 10, 0);
        lifeTextView.setTextSize(18);

        topLayout.addView(lifeTextView);

        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setGravity(Gravity.START);
        bottomLayout.setPadding(10, 10, 10, 10);

        blackSquaresButton = new Button(this);
        blackSquaresButton.setText("Black Squares");
        blackSquaresButton.setPadding(10, 0, 10, 0);
        blackSquaresButton.setTextSize(18);
        blackSquaresButton.setOnClickListener(v -> toggleRemainingSquaresDisplay());

        remainingSquaresTextView = new TextView(this);
        remainingSquaresTextView.setPadding(10, 0, 0, 0);
        remainingSquaresTextView.setTextSize(18);
        remainingSquaresTextView.setVisibility(View.GONE);

        bottomLayout.addView(blackSquaresButton);
        bottomLayout.addView(remainingSquaresTextView);

        tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tableLayout.setGravity(Gravity.CENTER);

        for (int i = 1; i <= TOTAL_SIZE; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow);

            for (int j = 1; j <= TOTAL_SIZE; j++) {
                if (i >= 4 && j >= 4) {
                    Cell cell = createCell(i, j);
                    tableRow.addView(cell);
                } else if (i <= 3 && j >= 4) {
                    tableRow.addView(createStyledTextView(colHints[j - 4][i - 1]));
                } else if (i >= 4 && j <= 3) {
                    tableRow.addView(createStyledTextView(rowHints[i - 4][j - 1]));
                } else {
                    tableRow.addView(createStyledTextView(""));
                }
            }
        }

        mainLayout.addView(topLayout);
        mainLayout.addView(tableLayout);
        mainLayout.addView(bottomLayout);
        setContentView(mainLayout);
    }
    private Cell createCell(int row, int col) {
        // Context로 MainActivity.this 사용, row와 col는 셀 위치 지정
        Cell cell = new Cell(this, row, col);
        // 셀의 크기 설정
        TableRow.LayoutParams params = new TableRow.LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        cell.setLayoutParams(params);
        // MainActivity 참조를 셀에 설정하여 게임 로직에 접근할 수 있게 함
        cell.setMainActivity(this);
        return cell;
    }

    private void setCorrectCells() {
        String[] correctCoordinates = {"4,5", "4,7", "5,4", "5,5", "5,7", "5,8", "6,4", "6,5", "6,6", "6,7", "6,8", "7,5", "7,6", "7,7", "8,6"};
        for (String coord : correctCoordinates) {
            correctCells.add(coord);
        }
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void updateRemainingBlackSquares(int delta) {
        remainingBlackSquares += delta;
        if (remainingBlackSquares <= 0) {
            isCleared = true;
            Toast.makeText(this, "Game Cleared!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLife(int delta) {
        life += delta;
        lifeTextView.setText("Life: " + life);
        if (life <= 0) {
            Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show();
            disableGameControls();
        }
    }

    private void disableGameControls() {
        blackSquaresButton.setEnabled(false);
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View cell = row.getChildAt(j);
                cell.setEnabled(false);
            }
        }
    }

    private TextView createStyledTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundColor(Color.LTGRAY);
        return textView;
    }



    private void toggleRemainingSquaresDisplay() {
        if (remainingSquaresTextView.getVisibility() == View.GONE) {
            remainingSquaresTextView.setText(String.valueOf(remainingBlackSquares));
            remainingSquaresTextView.setVisibility(View.VISIBLE);

            // 다시 숨김
            new Handler().postDelayed(() -> {
                remainingSquaresTextView.setVisibility(View.GONE);
            }, 500); // 1000ms = 1초 후 실행
        }
    }

}
