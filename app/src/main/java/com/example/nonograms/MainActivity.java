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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int TOTAL_SIZE = 8;
    private static final int BUTTON_SIZE = 150;
    private int requiredBlackSquares;
    private int[][] answerGrid = new int[5][5];
    private Cell[][] cells = new Cell[5][5];
    private int remainingBlackSquares;
    private int life = 3;

    private TextView lifeTextView, remainingSquaresTextView;
    private TableLayout tableLayout;
    private boolean isGameOver = false;
    private boolean isXToggle = false; // X 토글 상태 확인

    private final String[][] rowHints = new String[5][3];
    private final String[][] colHints = new String[5][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeHints();

        generateRandomAnswerGrid();
        calculateHints();
        requiredBlackSquares = calculateTotalBlackSquares();
        remainingBlackSquares = requiredBlackSquares;

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

        Button modeToggleButton = new Button(this);
        modeToggleButton.setText("BLACK SQUARES"); // 초기 텍스트 설정
        modeToggleButton.setPadding(10, 0, 10, 0);
        modeToggleButton.setTextSize(18);
        modeToggleButton.setOnClickListener(v -> {
            isXToggle = !isXToggle; // 모드 변경
            modeToggleButton.setText(isXToggle ? "X TOGGLE" : "BLACK SQUARES"); // 텍스트 변경
        });

        bottomLayout.addView(modeToggleButton);

        tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tableLayout.setGravity(Gravity.CENTER);

        for (int i = 1; i <= TOTAL_SIZE; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableLayout.addView(tableRow);

            for (int j = 1; j <= TOTAL_SIZE; j++) {
                if (i >= 4 && j >= 4) {
                    Cell cell = createCell(i - 4, j - 4);
                    cells[i - 4][j - 4] = cell;
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
        Cell cell = new Cell(this, row, col);
        TableRow.LayoutParams params = new TableRow.LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        cell.setLayoutParams(params);
        cell.setOnClickListener(v -> {
            if (isGameOver) return;

            if (isXToggle) {
                cell.toggleMark(); // X 표시 토글
            } else {
                if (answerGrid[row][col] == 1) {
                    cell.fillBlack();
                    remainingBlackSquares--;
                    if (remainingBlackSquares <= 0) {
                        endGame("Game Cleared!");
                    }
                } else {
                    decreaseLife();
                }
            }
        });
        return cell;
    }

    private void initializeHints() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                rowHints[i][j] = " ";
                colHints[i][j] = " ";
            }
        }
    }

    private void generateRandomAnswerGrid() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int blockCount = random.nextInt(5) + 1;
            int startIndex = random.nextInt(6 - blockCount);
            for (int j = startIndex; j < startIndex + blockCount; j++) {
                answerGrid[i][j] = 1;
            }
        }
    }

    private void calculateHints() {
        for (int i = 0; i < 5; i++) {
            String[] rowHint = calculateHint(answerGrid[i]); // 행 힌트 계산
            String[] colHint = calculateHint(getColumn(i));  // 열 힌트 계산

            // 힌트를 뒤쪽에 공백을 추가하며 저장 (공백을 뒤로 밀어냄)
            for (int j = 0; j < 3; j++) {
                rowHints[i][j] = j < rowHint.length ? rowHint[j] : " "; // 숫자 먼저 저장, 나머지는 공백
                colHints[i][j] = j < colHint.length ? colHint[j] : " ";
            }
        }
    }


    private int[] getColumn(int colIndex) {
        int[] column = new int[5];
        for (int i = 0; i < 5; i++) {
            column[i] = answerGrid[i][colIndex];
        }
        return column;
    }

    private String[] calculateHint(int[] line) {
        List<String> hints = new ArrayList<>();
        int count = 0;
        for (int cell : line) {
            if (cell == 1) {
                count++;
            } else if (count > 0) {
                hints.add(String.valueOf(count));
                count = 0;
            }
        }
        if (count > 0) {
            hints.add(String.valueOf(count));
        }
        return hints.isEmpty() ? new String[]{"0"} : hints.toArray(new String[0]);
    }

    private int calculateTotalBlackSquares() {
        int total = 0;
        for (String[] rowHint : rowHints) {
            for (String hint : rowHint) {
                if (hint != null && !hint.equals(" ")) {
                    total += Integer.parseInt(hint.trim());
                }
            }
        }
        return total;
    }

    private void decreaseLife() {
        life--;
        lifeTextView.setText("Life: " + life);
        if (life <= 0) {
            endGame("Game Over");
        }
    }

    private void endGame(String message) {
        isGameOver = true;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.setClickable(false);
            }
        }
    }

    private TextView createStyledTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundColor(Color.TRANSPARENT);
        return textView;
    }
}
