package com.heheapps.passphrasegenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private boolean advancedOptionsOpen = false;
    private String passphrase;

    private int numberOfWords;
    private int minPassphraseLength;
    private int maxPassphraseLength;
    private int minWordLength;
    private int maxWordLength;
    private int numberLength;

    private String customSeparatorString;

    private TextView passphraseTextView;

    private EditText numberOfWordsEt;
    private EditText minPassphraseLengthEt;
    private EditText maxPassphraseLengthEt;
    private EditText minWordLengthEt;
    private EditText maxWordLengthEt;
    private EditText numberLengthEt;
    private EditText customSeparatorEt;

    private CheckBox memorableCheckBox;
    private CheckBox strongCheckBox;
    private CheckBox customCheckBox;

    private CheckBox enLanguageCheckBox, esLanguageCheckBox, frLanguageCheckBox, deLanguageCheckBox, noLanguageCheckBox;

    private boolean lengthByWords;
    private boolean capFirst;
    private boolean capRandom;
    private boolean numbersAtEnd;
    private boolean numbersAtRandom;
    private boolean randomSeparator;
    private boolean customSeparator;
    private boolean sameSeparator;

    private boolean enChecked, esChecked, frChecked, deChecked, noChecked;

    private ArrayList<String> words;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberOfWords = 3;
        minPassphraseLength = 20;
        maxPassphraseLength = 30;
        minWordLength = 3;
        maxWordLength = 8;
        numberLength = 2;

        customSeparatorString = "";

        passphraseTextView = findViewById(R.id.passphrase_text_view);

        numberOfWordsEt = findViewById(R.id.number_of_words_et);
        minPassphraseLengthEt = findViewById(R.id.min_passphrase_length_et);
        maxPassphraseLengthEt = findViewById(R.id.max_passphrase_length_et);
        minWordLengthEt = findViewById(R.id.min_word_length_et);
        maxWordLengthEt = findViewById(R.id.max_word_length_et);
        numberLengthEt = findViewById(R.id.number_length_edit_text);
        customSeparatorEt = findViewById(R.id.custom_separator_editText);

        memorableCheckBox = findViewById(R.id.memorable_top_checkBox);
        strongCheckBox = findViewById(R.id.strong_top_checkBox);
        customCheckBox = findViewById(R.id.custom_top_checkBox);

        enLanguageCheckBox = findViewById(R.id.en_language_checkBox);
        esLanguageCheckBox = findViewById(R.id.es_language_checkBox);
        frLanguageCheckBox = findViewById(R.id.fr_language_checkBox);
        deLanguageCheckBox = findViewById(R.id.de_language_checkBox);
        noLanguageCheckBox = findViewById(R.id.no_language_checkBox);

        lengthByWords = true;
        capFirst = true;
        capRandom = false;
        numbersAtEnd = true;
        numbersAtRandom = false;
        randomSeparator = true;
        customSeparator = false;
        sameSeparator = true;

        loadLanguage();
        makeWordList();

        //Setting upper and lower limits for the editTexts
        numberOfWordsEt.addTextChangedListener(new MinMaxTextWatcher(1, 999, numberOfWordsEt));

        minPassphraseLengthEt.addTextChangedListener(new MinMaxTextWatcher(4, 9000, minPassphraseLengthEt));
        maxPassphraseLengthEt.addTextChangedListener(new MinMaxTextWatcher(6, 9999, maxPassphraseLengthEt));

        minWordLengthEt.addTextChangedListener(new MinMaxTextWatcher(1, 10, minWordLengthEt));
        maxWordLengthEt.addTextChangedListener(new MinMaxTextWatcher(2, 9999, maxWordLengthEt));

        numberLengthEt.addTextChangedListener(new MinMaxTextWatcher(1, 99, numberLengthEt));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("passphrase", passphrase);

        outState.putString("numberOfWords", numberOfWords + "");
        outState.putString("minPassphraseLength", minPassphraseLength + "");
        outState.putString("maxPassphraseLength", maxPassphraseLength + "");
        outState.putString("minWordLength", minWordLength + "");
        outState.putString("maxWordLength", maxWordLength + "");
        outState.putString("numberLength", numberLength + "");
        outState.putString("customSeparatorString", customSeparatorString);

        outState.putBoolean("lengthByWords", lengthByWords);
        outState.putBoolean("capFirst", capFirst);
        outState.putBoolean("capRandom", capRandom);
        outState.putBoolean("numbersAtEnd", numbersAtEnd);
        outState.putBoolean("numbersAtRandom", numbersAtRandom);
        outState.putBoolean("randomSeparator", randomSeparator);
        outState.putBoolean("customSeparator", customSeparator);
        outState.putBoolean("sameSeparator", sameSeparator);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        passphrase = savedInstanceState.getString("passphrase", "");
        passphraseTextView.setText(passphrase);

        lengthByWords = savedInstanceState.getBoolean("lengthByWords", true);
        capFirst = savedInstanceState.getBoolean("capFirst", true);
        capRandom = savedInstanceState.getBoolean("capRandom", false);
        numbersAtEnd = savedInstanceState.getBoolean("numbersAtEnd", true);
        numbersAtRandom = savedInstanceState.getBoolean("numbersAtRandom", false);
        randomSeparator = savedInstanceState.getBoolean("randomSeparator", true);
        customSeparator = savedInstanceState.getBoolean("customSeparator", false);
        sameSeparator = savedInstanceState.getBoolean("sameSeparator", true);

        numberOfWordsEt.setText(savedInstanceState.getString("numberOfWords", "3"));
        numberLengthEt.setText(savedInstanceState.getString("numberLength", "3"));
        minWordLengthEt.setText(savedInstanceState.getString("minWordLength", "2"));
        maxWordLengthEt.setText(savedInstanceState.getString("maxWordLength", "20"));
        minPassphraseLengthEt.setText(savedInstanceState.getString("minPassphraseLength", "40"));
        maxPassphraseLengthEt.setText(savedInstanceState.getString("maxPassphraseLength", "50"));
        customSeparatorEt.setText(savedInstanceState.getString("customSeparatorString", ""));

        updateEditText();
        updateCheckBoxes();
        updatePassphraseLengthType();
    }

    public void advancedOptionsBtn(View view) {
        View view1 = findViewById(R.id.advanced_options_view);
        if (advancedOptionsOpen) {
            view1.setVisibility(View.GONE);
            advancedOptionsOpen = false;
        } else {
            view1.setVisibility(View.VISIBLE);
            advancedOptionsOpen = true;
        }
    }

    public void copyBtn(View view) {
        if (passphrase.equals("")) {
            Toast.makeText(this, "No passphrase has been made yet", Toast.LENGTH_LONG).show();
            return;
        }

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", passphrase);

        //Avoiding nullpointer exceptions
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clip);
        } else {
            Toast.makeText(this, "Something vent wrong", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, "Passphrase copied to clipboard", Toast.LENGTH_LONG).show();
    }

    public void generatePasswordBtn(View view) {
        if(!(deChecked || enChecked || esChecked || frChecked || noChecked)){
            Toast.makeText(MainActivity.this, "Please select at least one language", Toast.LENGTH_LONG).show();
            return;
        }

        checkIntEditText();
        numberLength = Math.max(Integer.parseInt(numberLengthEt.getText().toString()), 1);

        passphrase = "";
        String symbolSeparator = symbolSeparator();

        if (lengthByWords) {
            numberOfWords = Math.max(Integer.parseInt(numberOfWordsEt.getText().toString()), 1);
            for (int i = 1; i <= numberOfWords; i++) {
                String randomNumber = randomNumber(false);
                String word = pickWord();
                passphrase += word + randomNumber;
                if (i != numberOfWords) {
                    if (sameSeparator) {
                        passphrase += symbolSeparator;
                    } else {
                        passphrase += symbolSeparator();
                    }
                }
            }
        } else {
            maxPassphraseLength = Math.max(Integer.parseInt(maxPassphraseLengthEt.getText().toString()), 6);
            minPassphraseLength = Math.max(Integer.parseInt(minPassphraseLengthEt.getText().toString()), 4);

            while (passphrase.length() < minPassphraseLength - (numbersAtEnd ? numberLength : 0)) {
                String word = pickWord();
                String randomNumber = randomNumber(false);
                word = word + randomNumber;

                if ((word.length() + passphrase.length()) + (numbersAtEnd ? numberLength : 0) <= maxPassphraseLength) {
                    passphrase += word;

                    if (passphrase.length() < minPassphraseLength - (numbersAtEnd ? numberLength : 0)) {
                        if (sameSeparator) {
                            passphrase += symbolSeparator;
                        } else {
                            passphrase += symbolSeparator();
                        }
                    }

                } else if ((maxPassphraseLength - passphrase.length() - (numbersAtEnd ? numberLength : 0)) < minWordLength) {
                    while (passphrase.length() < (minPassphraseLength - (numbersAtEnd ? numberLength : 0))) {
                        passphrase += "x";
                    }
                }
            }
        }

        if (numbersAtEnd) {
            try {
                int test = Integer.parseInt(passphrase.substring(passphrase.length() - 1));
            } catch (NumberFormatException e) {
                passphrase += randomNumber(true);
            }
        }

        TextView passphraseLength = findViewById(R.id.passphrase_length_text_view);
        String passphraseLengthString = "Passphrase length: " + passphrase.length();
        passphraseLength.setText(passphraseLengthString);
        passphraseTextView.setText(passphrase);
    }

    public String pickWord() {
        minWordLength = Math.max(Integer.parseInt(minWordLengthEt.getText().toString()), 1);
        maxWordLength = Math.max(Integer.parseInt(maxWordLengthEt.getText().toString()), 2);

        //Swaps min and max if min > max
        if(minWordLength > maxWordLength){
            int temp = minWordLength;
            minWordLength = maxWordLength;
            maxWordLength = temp;
        }
        String word = words.get((int) (Math.random() * (words.size() - 1)));

        if (capFirst) {
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
        }

        if (capRandom) {
            for (int i = 0; i < word.length(); i++) {
                if (Math.random() > 0.5) {
                    word = word.substring(0, i) + word.substring(i, i + 1).toUpperCase() + word.substring(i + 1);
                }
            }
        }

        if (minWordLength > word.length() || word.length() > maxWordLength) {
            word = pickWord();
        }
        return word;
    }

    public String randomNumber(boolean passAtEnd) {
        String randomNumber = "";
        if ((numbersAtRandom && (int) (Math.random() * 2) == 1) || passAtEnd) {
            numberLength = Math.max(Integer.parseInt(numberLengthEt.getText().toString()), 1);
            for (int i = 0; i < numberLength; i++) {
                randomNumber += (int) (Math.random() * 10);
            }

        }
        return randomNumber;
    }

    public String symbolSeparator() {
        String symbol = "";
        customSeparatorString = customSeparatorEt.getText().toString();

        if (randomSeparator && customSeparator) {
            String randomSymbols = " ,.\"%&$!?+-_'*#@=";
            if (!customSeparatorString.equals("")) {
                randomSymbols += customSeparatorString;
            }
            int random = (int) (Math.random() * randomSymbols.length());
            symbol = randomSymbols.substring(random, random + 1);

        } else if (randomSeparator) {
            String randomSymbols = " ,.\"%&$!?+-_'*#@=";
            int random = (int) (Math.random() * randomSymbols.length());
            symbol = randomSymbols.substring(random, random + 1);

        } else if (customSeparator) {
            if (!customSeparatorString.equals("")) {
                int random = (int) (Math.random() * customSeparatorString.length());
                symbol = customSeparatorString.substring(random, random + 1);
            }
        }
        return symbol;
    }

    public void checkIntEditText(){
        EditText[] editTexts = new EditText[] {numberOfWordsEt, minPassphraseLengthEt, maxPassphraseLengthEt, minWordLengthEt, maxWordLengthEt, numberLengthEt};
        for (EditText editText: editTexts){
            try {
                Integer.parseInt(editText.getText().toString());
            } catch (NumberFormatException e){
                editText.setText("0");
            }
        }
    }

    public void passphraseTypeCheckbox(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        boolean customWasChecked = customCheckBox.isChecked();

        customSeparatorString = customSeparatorEt.getText().toString();

        Button customBtn = findViewById(R.id.save_custom_btn);

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.memorable_top_checkBox:
                if (checked) {
                    if (customWasChecked) {
                        saveCustom();
                    }
                    strongCheckBox.setChecked(false);
                    customCheckBox.setChecked(false);
                    passphraseTypeSetup("memorable");
                    customBtn.setVisibility(View.VISIBLE);
                } else {
                    memorableCheckBox.setChecked(true);
                }
                break;
            case R.id.strong_top_checkBox:
                if (checked) {
                    if (customWasChecked) {
                        saveCustom();
                    }
                    memorableCheckBox.setChecked(false);
                    customCheckBox.setChecked(false);
                    passphraseTypeSetup("strong");
                    customBtn.setVisibility(View.VISIBLE);
                } else {
                    memorableCheckBox.setChecked(true);
                    passphraseTypeSetup("memorable");
                }
                break;
            case R.id.custom_top_checkBox:
                if (checked) {
                    memorableCheckBox.setChecked(false);
                    strongCheckBox.setChecked(false);
                    passphraseTypeSetup("custom");
                    customBtn.setVisibility(View.GONE);
                } else {
                    saveCustom();
                    memorableCheckBox.setChecked(true);
                    passphraseTypeSetup("memorable");
                    customBtn.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    PopupWindow popupWindow;

    public void saveCustomBtn(View view) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    public void popupYesBtn(View view) {
        saveCustom();
        popupWindow.dismiss();
        Toast.makeText(MainActivity.this, "Setting saved to custom passphrase", Toast.LENGTH_SHORT).show();
    }

    public void popupCancelBtn(View view) {
        popupWindow.dismiss();
    }

    public void saveCustom() {
        SharedPreferences sharedpreferences = getSharedPreferences("custom", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("numberOfWords", numberOfWords + "");
        editor.putString("minPassphraseLength", minPassphraseLength + "");
        editor.putString("maxPassphraseLength", maxPassphraseLength + "");
        editor.putString("minWordLength", minWordLength + "");
        editor.putString("maxWordLength", maxWordLength + "");
        editor.putString("numberLength", numberLength + "");
        editor.putString("customSeparatorString", customSeparatorString);

        editor.putBoolean("lengthByWords", lengthByWords);
        editor.putBoolean("capFirst", capFirst);
        editor.putBoolean("capRandom", capRandom);
        editor.putBoolean("numbersAtEnd", numbersAtEnd);
        editor.putBoolean("numbersAtRandom", numbersAtRandom);
        editor.putBoolean("randomSeparator", randomSeparator);
        editor.putBoolean("customSeparator", customSeparator);
        editor.putBoolean("sameSeparator", sameSeparator);

        editor.apply();
    }

    public void passphraseTypeSetup(String passphraseType) {

        switch (passphraseType) {
            case "memorable":
                lengthByWords = true;
                capFirst = true;
                capRandom = false;
                numbersAtEnd = true;
                numbersAtRandom = false;
                randomSeparator = true;
                customSeparator = false;
                sameSeparator = true;

                numberOfWordsEt.setText("3");
                numberLengthEt.setText("2");
                minWordLengthEt.setText("3");
                maxWordLengthEt.setText("8");
                minPassphraseLengthEt.setText("20");
                maxPassphraseLengthEt.setText("30");
                customSeparatorEt.setText("");

                updateCheckBoxes();
                updatePassphraseLengthType();
                updateEditText();
                break;
            case "strong":
                lengthByWords = true;
                capFirst = true;
                capRandom = true;
                numbersAtEnd = true;
                numbersAtRandom = true;
                randomSeparator = true;
                customSeparator = false;
                sameSeparator = false;

                numberOfWordsEt.setText("6");
                numberLengthEt.setText("3");
                minWordLengthEt.setText("2");
                maxWordLengthEt.setText("20");
                minPassphraseLengthEt.setText("40");
                maxPassphraseLengthEt.setText("50");
                customSeparatorEt.setText("");

                updateCheckBoxes();
                updatePassphraseLengthType();
                updateEditText();
                break;

            case "custom":
                SharedPreferences sharedPreferences = getSharedPreferences("custom", MODE_PRIVATE);

                lengthByWords = sharedPreferences.getBoolean("lengthByWords", true);
                capFirst = sharedPreferences.getBoolean("capFirst", true);
                capRandom = sharedPreferences.getBoolean("capRandom", false);
                numbersAtEnd = sharedPreferences.getBoolean("numbersAtEnd", true);
                numbersAtRandom = sharedPreferences.getBoolean("numbersAtRandom", false);
                randomSeparator = sharedPreferences.getBoolean("randomSeparator", true);
                customSeparator = sharedPreferences.getBoolean("customSeparator", false);
                sameSeparator = sharedPreferences.getBoolean("sameSeparator", true);

                numberOfWordsEt.setText(sharedPreferences.getString("numberOfWords", "6"));
                numberLengthEt.setText(sharedPreferences.getString("numberLength", "3"));
                minWordLengthEt.setText(sharedPreferences.getString("minWordLength", "2"));
                maxWordLengthEt.setText(sharedPreferences.getString("maxWordLength", "20"));
                minPassphraseLengthEt.setText(sharedPreferences.getString("minPassphraseLength", "40"));
                maxPassphraseLengthEt.setText(sharedPreferences.getString("maxPassphraseLength", "50"));
                customSeparatorEt.setText(sharedPreferences.getString("customSeparatorString", ""));

                updateEditText();
                updateCheckBoxes();
                updatePassphraseLengthType();
                break;
        }
    }

    public void updateEditText() {
        numberOfWords = Integer.parseInt(numberOfWordsEt.getText().toString());
        numberLength = Integer.parseInt(numberLengthEt.getText().toString());
        minWordLength = Integer.parseInt(minWordLengthEt.getText().toString());
        maxWordLength = Integer.parseInt(maxWordLengthEt.getText().toString());
        minPassphraseLength = Integer.parseInt(minPassphraseLengthEt.getText().toString());
        maxPassphraseLength = Integer.parseInt(maxPassphraseLengthEt.getText().toString());
        customSeparatorString = customSeparatorEt.getText().toString();
    }

    public void passphraseLengthDeterminerCheckBoxes(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.passphrase_length_words_checkBox:
                if (checked) {
                    lengthByWords = true;
                    updatePassphraseLengthType();
                } else {
                    lengthByWords = false;
                    updatePassphraseLengthType();
                }
                break;
            case R.id.passphrase_length_chars_checkBox:
                if (checked) {
                    lengthByWords = false;
                    updatePassphraseLengthType();
                } else {
                    lengthByWords = true;
                    updatePassphraseLengthType();
                }
                break;
        }
    }

    public void updatePassphraseLengthType() {
        CheckBox wordsCheckBox = findViewById(R.id.passphrase_length_words_checkBox);
        CheckBox charsCheckBox = findViewById(R.id.passphrase_length_chars_checkBox);

        View wordsView = findViewById(R.id.length_by_words_view);
        View charsView = findViewById(R.id.length_by_chars_view);

        SharedPreferences sharedpreferences = getSharedPreferences("custom", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (lengthByWords) {
            charsCheckBox.setChecked(false);
            wordsCheckBox.setChecked(true);
            wordsView.setVisibility(View.VISIBLE);
            charsView.setVisibility(View.GONE);
            if (customCheckBox.isChecked()) {
                editor.putBoolean("lengthByWords", true);
            }
        } else {
            charsCheckBox.setChecked(true);
            wordsCheckBox.setChecked(false);
            wordsView.setVisibility(View.GONE);
            charsView.setVisibility(View.VISIBLE);
            if (customCheckBox.isChecked()) {
                editor.putBoolean("lengthByWords", false);
            }
        }
        editor.apply();
    }

    //It's called Android Studios is lying, Se MyCheckBoxStyle.xml
    public void OnCheckBoxClicked(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences("custom", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.capitalize_first_checkBox:
                capFirst = checked;
                break;
            case R.id.capitalize_random_checkBox:
                capRandom = checked;
                break;
            case R.id.add_numbers_end_checkBox:
                numbersAtEnd = checked;
                break;
            case R.id.add_numbers_random_checkBox:
                numbersAtRandom = checked;
                break;
            case R.id.random_separator_checkBox:
                randomSeparator = checked;
                break;
            case R.id.custom_separator_checkBox:
                customSeparator = checked;
                break;
            case R.id.same_separator_checkBox:
                sameSeparator = checked;
                break;
        }
        editor.apply();
    }

    public void updateCheckBoxes() {
        CheckBox capFirstCB = findViewById(R.id.capitalize_first_checkBox);
        CheckBox capRandomCB = findViewById(R.id.capitalize_random_checkBox);
        CheckBox numbersAtEndCB = findViewById(R.id.add_numbers_end_checkBox);
        CheckBox numbersAtRandomCB = findViewById(R.id.add_numbers_random_checkBox);
        CheckBox randomSeparatorCB = findViewById(R.id.random_separator_checkBox);
        CheckBox customSeparatorCB = findViewById(R.id.custom_separator_checkBox);
        CheckBox sameSeparatorCB = findViewById(R.id.same_separator_checkBox);

        capFirstCB.setChecked(capFirst);
        capRandomCB.setChecked(capRandom);
        numbersAtEndCB.setChecked(numbersAtEnd);
        numbersAtRandomCB.setChecked(numbersAtRandom);
        randomSeparatorCB.setChecked(randomSeparator);
        customSeparatorCB.setChecked(customSeparator);
        sameSeparatorCB.setChecked(sameSeparator);
    }

    public void numberOfWordsDecrease(View view) {
        numberOfWords = Integer.parseInt(numberOfWordsEt.getText().toString());
        if(numberOfWords > 0) {
            numberOfWords--;
            numberOfWordsEt.setText("" + numberOfWords);
        }
    }

    public void numberOfWordsIncrease(View view) {
        numberOfWords = Integer.parseInt(numberOfWordsEt.getText().toString());
        numberOfWords++;
        numberOfWordsEt.setText("" + numberOfWords);
    }

    public void minPassphraseLengthDecrease(View view) {
        minPassphraseLength = Integer.parseInt(minPassphraseLengthEt.getText().toString());
        if(minPassphraseLength > 0) {
            minPassphraseLength--;
            minPassphraseLengthEt.setText("" + minPassphraseLength);
        }
    }

    public void minPassphraseLengthIncrease(View view) {
        minPassphraseLength = Integer.parseInt(minPassphraseLengthEt.getText().toString());
        minPassphraseLength++;
        minPassphraseLengthEt.setText("" + minPassphraseLength);
    }

    public void maxPassphraseLengthDecrease(View view) {
        maxPassphraseLength = Integer.parseInt(maxPassphraseLengthEt.getText().toString());
        if(maxPassphraseLength > 0) {
            maxPassphraseLength--;
            maxPassphraseLengthEt.setText("" + maxPassphraseLength);
        }
    }

    public void maxPassphraseLengthIncrease(View view) {
        maxPassphraseLength = Integer.parseInt(maxPassphraseLengthEt.getText().toString());
        maxPassphraseLength++;
        maxPassphraseLengthEt.setText("" + maxPassphraseLength);
    }

    public void minWordLengthDecrease(View view) {
        minWordLength = Integer.parseInt(minWordLengthEt.getText().toString());
        if(minWordLength > 0) {
            minWordLength--;
            minWordLengthEt.setText("" + minWordLength);
        }
    }

    public void minWordLengthIncrease(View view) {
        minWordLength = Integer.parseInt(minWordLengthEt.getText().toString());
        minWordLength++;
        minWordLengthEt.setText("" + minWordLength);
    }

    public void maxWordLengthDecrease(View view) {
        maxWordLength = Integer.parseInt(maxWordLengthEt.getText().toString());
        if(maxWordLength > 0) {
            maxWordLength--;
            maxWordLengthEt.setText("" + maxWordLength);
        }
    }

    public void maxWordLengthIncrease(View view) {
        maxWordLength = Integer.parseInt(maxWordLengthEt.getText().toString());
        maxWordLength++;
        maxWordLengthEt.setText("" + maxWordLength);
    }

    public void numberLengthDecrease(View view) {
        numberLength = Integer.parseInt(numberLengthEt.getText().toString());
        if(numberLength > 0) {
            numberLength--;
            numberLengthEt.setText("" + numberLength);
        }
    }

    public void numberLengthIncrease(View view) {
        numberLength = Integer.parseInt(numberLengthEt.getText().toString());
        numberLength++;
        numberLengthEt.setText("" + numberLength);
    }

    public ArrayList<String> fileReader(String fileName) {
        ArrayList<String> arrayList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String line;
            while ((line = reader.readLine()) != null) {
                arrayList.add(line);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("---------------------------------------------------------------");
                    System.out.println("IO");
                    System.out.println("---------------------------------------------------------------");
                    Toast.makeText(this, "There was an error reading form file", Toast.LENGTH_LONG).show();
                }
            }
        }
        return arrayList;
    }

    @Override
    protected void onPause() {
        if (customCheckBox.isChecked()) {
            customSeparatorString = customSeparatorEt.getText().toString();
            saveCustom();
        }
        super.onPause();
    }

    public void languageCheckBoxes(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Checks which checkbox was clicked
        switch (view.getId()) {
            case R.id.en_language_checkBox:
                enChecked = checked;
                break;
            case R.id.es_language_checkBox:
                esChecked = checked;
                break;
            case R.id.fr_language_checkBox:
                frChecked = checked;
                break;
            case R.id.de_language_checkBox:
                deChecked = checked;
                break;
            case R.id.no_language_checkBox:
                noChecked = checked;
                break;
        }
        saveLanguage();
        makeWordList();
    }

    public void makeWordList() {
        words = new ArrayList<>();

        if(deChecked){words.addAll(Arrays.asList(getResources().getStringArray(R.array.wordListDe)));}
        if(enChecked){words.addAll(Arrays.asList(getResources().getStringArray(R.array.wordListEn)));}
        if(esChecked){words.addAll(Arrays.asList(getResources().getStringArray(R.array.wordListEs)));}
        if(frChecked){words.addAll(Arrays.asList(getResources().getStringArray(R.array.wordListFr)));}
        if(noChecked){words.addAll(Arrays.asList(getResources().getStringArray(R.array.wordListNo)));}
    }

    public void saveLanguage(){
        SharedPreferences sharedpreferences = getSharedPreferences("Languages", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("en", enChecked);
        editor.putBoolean("es", esChecked);
        editor.putBoolean("fr", frChecked);
        editor.putBoolean("de", deChecked);
        editor.putBoolean("no", noChecked);
        editor.apply();
    }

    public void loadLanguage(){
        SharedPreferences sharedpreferences = getSharedPreferences("Languages", MODE_PRIVATE);

        enChecked = sharedpreferences.getBoolean("en", true);
        enLanguageCheckBox.setChecked(enChecked);

        esChecked = sharedpreferences.getBoolean("es", false);
        esLanguageCheckBox.setChecked(esChecked);

        frChecked = sharedpreferences.getBoolean("fr", false);
        frLanguageCheckBox.setChecked(frChecked);

        deChecked = sharedpreferences.getBoolean("de", false);
        deLanguageCheckBox.setChecked(deChecked);

        noChecked = sharedpreferences.getBoolean("no", false);
        noLanguageCheckBox.setChecked(noChecked);
    }
}
