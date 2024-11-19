package com.example.nonograms;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

public class Cell extends AppCompatButton {
    private boolean isBlack = false; // 셀이 검은색인지 상태를 저장
    private boolean isMarked = false; // 셀이 체크 표시되었는지 상태를 저장
    private final int row;
    private final int col;

    public Cell(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
        initCell();
    }

    private void initCell() {
        setBackgroundResource(R.drawable.cell_selector); // 기본 배경 설정
        setOnLongClickListener(v -> {
            toggleMark(); // 길게 누르면 체크 표시
            return true;
        });
    }

    // 셀을 검은색으로 채우는 메서드
    public void fillBlack() {
        isBlack = true;
        setBackgroundColor(0xFF000000); // 검은색으로 설정
        setEnabled(false); // 선택 불가 상태로 변경
    }

    // 셀의 체크 상태를 토글하는 메서드
    public void toggleMark() {
        isMarked = !isMarked;
        setText(isMarked ? "X" : ""); // "X" 표시 또는 텍스트 제거
    }

    // 셀이 검은색인지 확인하는 메서드
    public boolean isFilledBlack() {
        return isBlack;
    }

    public void clearCell() {
        isBlack = false;
        setBackgroundColor(0xFFFFFFFF); // 하얀색으로 설정
        setText(""); // 텍스트 제거
    }

}
