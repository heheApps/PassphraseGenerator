package com.heheapps.passphrasegenerator;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

class MinMaxTextWatcher implements TextWatcher {
    private int min, max;
    private EditText editText;

    public MinMaxTextWatcher(int min, int max, EditText editText){
        this.min = min;
        this.max = max;
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String strEnteredVal = charSequence.toString();
        if (strEnteredVal.equals("")) {
            strEnteredVal = "0";
        }
        int inputNumber = Integer.parseInt(strEnteredVal);

        if (inputNumber > max) {
            editText.setError("This number must be between " + min + " and " + max);
        }else if (inputNumber < min) {
            editText.setError("This number must be between " + min + " and " + max);
        }else {
            editText.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}
}
