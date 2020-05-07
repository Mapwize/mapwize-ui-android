package io.mapwize.mapwizeui.refacto;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import io.mapwize.mapwizeui.R;

public class SearchBar extends ConstraintLayout {

    private SearchBarListener listener;
    private FrameLayout backButton;
    private EditText searchEditText;

    public SearchBar(Context context) {
        super(context);
        initialize(context);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.mwz_search_bar, this);
    }

    public void setListener(SearchBarListener listener) {
        this.listener = listener;
        backButton = findViewById(R.id.mwz_search_bar_back_button);
        backButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchBarBackButtonClick();
            }
        });
        searchEditText = findViewById(R.id.mwz_search_bar_query_field);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listener.onSearchBarQueryChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm =(InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
            else {
                InputMethodManager imm =(InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
        });
    }

    public void textFieldRequestFocus() {
        searchEditText.requestFocus();
    }

    public interface SearchBarListener {
        void onSearchBarQueryChange(String query);
        void onSearchBarBackButtonClick();
    }

}