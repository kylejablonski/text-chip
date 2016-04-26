package com.divshark.textchipsample;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.divshark.text_chip.TextChip;
import com.larswerkman.lobsterpicker.LobsterPicker;

/**
 * MainActivity to show the use of the TextChip library
 */
public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------------------------------------
    // Constants

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String [] TEXT_SIZES = new String[] {"14", "16", "18", "20", "22", "24", "26", "28"};
    private static final String [] COLORS = new String [] {"Black", "White"};


    // --------------------------------------------------------------------------------------------
    // Member Variables

    private TextChip mChip;
    private AppCompatEditText mEtText;
    private AppCompatSpinner mSpOptions;
    private AppCompatSpinner mSpTextColor;
    private FrameLayout mFlBackgroundColor;
    private AppCompatCheckBox mCbTextCaps;
    private SharedPreferences mSharedPreferences;

    private static final String PREFS = "prefs";

    private static final String TEXT = "text";
    private static final String TEXT_COLOR = "text_color";
    private static final String TEXT_SIZE = "text_size";
    private static final String BG_COLOR =  "bg_color";
    private static final String TEXT_CAPS =  "cap_text";

    private String textColor;
    private String textSize;
    private int bgColor;
    private String text;
    private boolean textCaps;

    // --------------------------------------------------------------------------------------------
    // Class overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        mChip = (TextChip) findViewById(R.id.text_chip);
        mEtText = (AppCompatEditText) findViewById(R.id.et_text);
        mSpOptions = (AppCompatSpinner) findViewById(R.id.sp_text_size);
        mSpTextColor = (AppCompatSpinner) findViewById(R.id.sp_text_color);
        mFlBackgroundColor = (FrameLayout) findViewById(R.id.fl_bg_color);
        mCbTextCaps = (AppCompatCheckBox) findViewById(R.id.cb_text_caps);


        // Setup from preferences
        text = mSharedPreferences.getString(TEXT, "Text Chip");
        textColor = mSharedPreferences.getString(TEXT_COLOR, "White");
        textSize = mSharedPreferences.getString(TEXT_SIZE, "14");
        bgColor = mSharedPreferences.getInt(BG_COLOR, Color.parseColor("#2196F3"));
        textCaps = mSharedPreferences.getBoolean(TEXT_CAPS, false);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, TEXT_SIZES);
        mSpOptions.setAdapter(adapter);
        mSpOptions.setOnItemSelectedListener(SpinnerListener);

        ArrayAdapter<String> adapterTextColor = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, COLORS);
        mSpTextColor.setAdapter(adapterTextColor);
        mSpTextColor.setOnItemSelectedListener(TextColorListener);

        init();

        mChip.setOnClickListener(ChipListener);
        mEtText.addTextChangedListener(ChipTextWatcher);
        mCbTextCaps.setOnCheckedChangeListener(CbListener);

        mFlBackgroundColor.setOnClickListener(BgColorListener);
    }

    // --------------------------------------------------------------------------------------------
    // Public / private helpers

    private void init(){

        mChip.setText(text);
        mChip.setTextSize(Integer.parseInt(textSize) * 2);
        mChip.setBackgroundColor(bgColor);
        mChip.setUpperCase(textCaps);
        mCbTextCaps.setChecked(textCaps);

        mFlBackgroundColor.setBackgroundColor(bgColor);

        int colorMatch = 0;
        for(int i = 0; i <= COLORS.length - 1; i++){
            if(COLORS[i].equals(textColor)){
                colorMatch = i;
            }
        }
        mSpTextColor.setSelection(colorMatch);
        switch(colorMatch){
            case 0:
                mChip.setTextColor(Color.BLACK);
                break;

            case 1:
                mChip.setTextColor(Color.WHITE);
                break;
        }

        int match = 0;
        for(int i = 0; i <= TEXT_SIZES.length - 1; i++){
            if(TEXT_SIZES[i].equals(textSize)) {
                match = i;
                Log.d(TAG, "matched at pos "+ match);
            }
        }
        mSpOptions.setSelection(match);
        mEtText.setText(text);
    }

    // --------------------------------------------------------------------------------------------
    // Listeners for the UI Elements

    private final TextWatcher ChipTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            mChip.setText(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            mSharedPreferences.edit().putString(TEXT, s.toString()).apply();
        }
    };

    private final AdapterView.OnItemSelectedListener SpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Integer value = Integer.parseInt(TEXT_SIZES[position]);
            mChip.setTextSize(value * 2);
            mSharedPreferences.edit().putString(TEXT_SIZE, Integer.toString(value)).apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    private final AdapterView.OnItemSelectedListener TextColorListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch(position){
                case 0:
                    mChip.setTextColor(Color.BLACK);
                    mSharedPreferences.edit().putString(TEXT_COLOR, "Black").apply();
                    break;

                case 1:
                    mChip.setTextColor(Color.WHITE);
                    mSharedPreferences.edit().putString(TEXT_COLOR, "White").apply();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final View.OnClickListener BgColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final View view  = getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
            final LobsterPicker lobsterPicker = (LobsterPicker) view.findViewById(R.id.lobsterpicker);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.background_color))
                    .setView(view)
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int color = lobsterPicker.getColor();
                            mChip.setBackgroundColor(color);
                            mSharedPreferences.edit().putInt(BG_COLOR, color).apply();
                            mFlBackgroundColor.setBackgroundColor(color);
                        }
                    }).setNegativeButton(getString(R.string.cancel), null);

            Dialog dialog = builder.create();
            dialog.show();


        }
    };

    private final CompoundButton.OnCheckedChangeListener CbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSharedPreferences.edit().putBoolean(TEXT_CAPS, isChecked).apply();
            mChip.setUpperCase(isChecked);
            Log.d(TAG, "to upper case "+ isChecked);

        }
    };

    private final View.OnClickListener ChipListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Tapped the chip ", Toast.LENGTH_SHORT).show();
        }
    };
}
