package com.nooverlap314.uncertaintycalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import java.util.HashMap;

/**
 * The main (calculator) activity
 */
public class MyActivity extends AppCompatActivity {
	private static double MAX_NOMINAL_VALUE = 1E299;
	private static double MIN_NOMINAL_VALUE = 1E-298;

    private static Number currentResult;
    private static HashMap<Character, Number> savedResults;
    private static String[] listEntries;
    private static ArrayAdapter<String> adapter; // For save/saved dialogs
    private static AlertDialog.Builder dialogBuilder; // The save/saved dialog
    private static int dialogueType; // 0 for save, 1 for insert
    private static String inputText;
    private static String outputText;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my);
        initListEntries();

        if (savedResults == null) {
            savedResults = new HashMap<>();
        }
        adapter = new ArrayAdapter<>(this, R.layout.list_view_layout, R.id.list_view_text_views, listEntries);

        initDialogue();
        initEditText();
        setResultViewContents(getResources().getString(R.string.preview_placeholder));

        super.onCreate(savedInstanceState);
    }

    private void initListEntries() {
        listEntries = new String[25];
        for (int i = 0; i < 25; i++) {
            char slotChar = (char) (i + 'A');
            if (slotChar > 'D')
                slotChar++;
            listEntries[i] = slotChar + getResources().getString(R.string.list_letter_number_separator) + "Empty";
        }
    }

    private void initDialogue() {
        dialogueType = 0;

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (dialogueType == 0) {
                    saveResult(selected);
                } else if (dialogueType == 1) {
                    insertPastResult(selected);
                }
            }
        });
    }


    private void initEditText() {
        final EditText editText = (EditText) findViewById(R.id.equation_entry);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    evaluate(null);
                    handled = true;
                }
                return handled;
            }
        });

        editText.setHorizontallyScrolling(true);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                afterTextChangedHandler(s.toString());
            }
        });
    }

    private void afterTextChangedHandler(String newString) {
        if (newString.equals("")) {
            setResultViewContents(getResources().getString(R.string.preview_placeholder));
        } else {
            String enteredEquation = substituteVariablesReadable(newString);

            //Check has no unrecognised characters
            if (Calculator.isOnlyValidCharacters(enteredEquation)) {
                try {
                    setResultViewContents(Calculator.toFormula(enteredEquation));
                } catch (Exception e) {} //Change nothing if something went wrong
            } else {
                setResultViewContents("Unrecognised character(s)");
            }
        }
    }

    private String substituteVariablesReadable(String formula) {
        for(char savedResultSymbol : savedResults.keySet()) {
            String letterAsString = Character.toString(savedResultSymbol);
            String replacementAsString = "(" + savedResultSymbol + ")";
            formula = formula.replaceAll(letterAsString, replacementAsString);
        }

        return formula;
    }

	private String substituteVariablesNumeric(String formula) {
		for(char savedResultSymbol : savedResults.keySet()) {
			String letterAsString = Character.toString(savedResultSymbol);
			String replacementAsString = "(" + savedResults.get(savedResultSymbol) + ")";
			formula = formula.replaceAll(letterAsString, replacementAsString);
		}

		return formula;
	}

    @Override
    protected void onStart() {
        if (savedResults == null) {
            savedResults = new HashMap<>(); //Lost data, but won't crash
        } else {
            populateSaveEntryList();
        }

        if (inputText != null) {
            populateInputTextEdit();
        }

        // Just do the webview setup once
        WebView webView = (WebView)findViewById(R.id.main_activity_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setBackgroundColor(0x00FFFFFF);

        if (outputText != null) {
            setResultViewContents(outputText);
        }

        super.onStart();
    }

    private void populateSaveEntryList() {
        for (char varName : savedResults.keySet()) {
            int listIndex = varName;
            if (listIndex > 'D') { // account for missing 'E'
                listIndex--;
            }
            listIndex -= 'A';
            listEntries[listIndex] = Character.toString(varName) +
                    getResources().getString(R.string.list_letter_number_separator) +
                    UMath.formattedNumber(savedResults.get(varName));
        }
        adapter.notifyDataSetChanged();
    }

    private void populateInputTextEdit() {
        EditText editText = (EditText) findViewById(R.id.equation_entry);
        editText.setText(inputText);
        editText.setSelection(inputText.length());
    }

    @Override
    protected void onStop() {
        EditText editText = (EditText) findViewById(R.id.equation_entry);
        inputText = editText.getText().toString();

        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent intent = new Intent(this, InformationActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Called when the user clicks the '=' button
     */
    public void evaluate(View view) {
        EditText editText = (EditText) findViewById(R.id.equation_entry);
        String message = editText.getText().toString();

        if (Calculator.isOnlyValidCharacters(message)) {
            message = substituteVariablesNumeric(message);

            try {
                currentResult = Calculator.equationProcessor(message);
                message = currentResult.toString();
                if (currentResult instanceof Uncertainty)
                    message += "<br/><p style=\"display:inline;font-size:80%\">(\u00B1 " + ((Uncertainty) currentResult).getRatioUncertainty() * 100 + "%)</p>";
            } catch (IllegalArgumentException iae) {
                message = getResources().getString(R.string.result_message_argument_failed);
            } catch (Exception e) { // Woah, this is sherking a lot of responsibility
                message = getResources().getString(R.string.result_message_failed);
            }
        } else {
            message = "Unrecognised character(s)"; //Should already be set
        }

        outputText = message;
        setResultViewContents(message);
    }


    /**
     * Called when the user clicks the Format button
     */
    public void formatResult(View view) {
		if (isReasonableNumber(currentResult)) {
			outputText = UMath.formattedNumber(currentResult); //Will be treated as an equation
			setResultViewContents(outputText);
		} else {
			setResultViewContents("Result could not be formatted");
		}
    }

	private boolean isReasonableNumber(Number number) {
		if (number == null) {
			return false;
		}

		if (Math.abs(number.doubleValue()) > MAX_NOMINAL_VALUE) {
			return false;
		}

		if (Math.abs(number.doubleValue()) < MIN_NOMINAL_VALUE) {
			return false;
		}

		String numberString = number.toString();
		if (numberString.contains("NaN")) {
			return false;
		}

		if (numberString.contains("Infinity")) {
			return false;
		}

		return true;
	}


    private void showSlotDialogue(String prompt) {
        dialogBuilder.setTitle(prompt);

        dialogBuilder.show();
    }


    /**
     * Called when the user clicks the uncertainty sign button
     */
    public void insertUnc(View view) {
        insertInEquation("\u00B1");
    }


    /**
     * Called when the user clicks the pi button
     */
    public void insertPi(View view) {
        insertInEquation("\u03C0");
    }

    /**
     * Called when the user clicks the pi button
     */
    public void insertE(View view) {
        insertInEquation("e");
    }


    /**
     * Places 'content' where the user has the cursor in the edittext widget, or at the end if it is
     * not being used.
     */
    private void insertInEquation(String content) {
        EditText editText = (EditText) findViewById(R.id.equation_entry);
        String message = editText.getText().toString();

        int lowerSelection = editText.getSelectionStart();
        int upperSelection = editText.getSelectionEnd();

        if (upperSelection < 0) { // No current selection
            editText.append(content);
        } else if (lowerSelection < 0) { // Typing
            String newMessage = message.substring(0, upperSelection);
            newMessage += content;
            newMessage += message.substring(upperSelection);
            editText.setText(newMessage);
            editText.setSelection(upperSelection + 1);
        } else { // Selection beginning, end
            String newMessage = message.substring(0, Math.min(lowerSelection, upperSelection));
            newMessage += content;
            newMessage += message.substring(Math.max(lowerSelection, upperSelection));
            editText.setText(newMessage);
            editText.setSelection(Math.min(lowerSelection, upperSelection) + 1);
        }
    }



    /**
     * Called when the user clicks the Save button
     */
    public void savePrompt(View view) {
        if (currentResult != null) {
            if (isReasonableNumber(currentResult)) {
                dialogueType = 0;
                showSlotDialogue(getResources().getString(R.string.dialog_save_title));
            } else {
				setResultViewContents("Result could not be saved");
			}
        }
    }


    /**
     * Other functions should have set this one up for easy sailing.
     * Brings up the dialog box and sets the type variable so that the menu calls the saveResult
     * function on select.
     * @param view I can't describe this parameter! YOU wanted it here.
     */
    public void insertPrompt(View view) {
        dialogueType = 1;
        showSlotDialogue(getResources().getString(R.string.dialog_insert_title));
    }


    /**
     * Saves the current result in the selected slot. This should only be called by the
     * dialogBuilder object.
     */
    private void saveResult(int index) {
        char currentLetter = listEntries[index].charAt(0);
        listEntries[index] = Character.toString(currentLetter) +
                getResources().getString(R.string.list_letter_number_separator) +
                UMath.formattedNumber(currentResult);

        savedResults.put(currentLetter, currentResult);
        adapter.notifyDataSetChanged();
    }


    /**
     * Just puts the selected letter into the user's equation at the cursor position.
     */
    private void insertPastResult(int selected) {
        insertInEquation(listEntries[selected].substring(0, 1));
    }


    public void setResultViewContents(String input){
        WebView webView = (WebView)findViewById(R.id.main_activity_webview);

        String path="file:///android_asset/JQMath/";
        String js = "<html><head><meta charset=\"utf-8\">" +
                "<link rel=\"stylesheet\" href=\"jqmath-0.4.3.css\">" +
                "<script src=\"jquery-1.4.3.min.js\"></script>" +
                "<script src=\"jqmath-etc-0.4.5.min.js\" charset=\"utf-8\"></script>" +
                "</head><body>" +
                "<table style=\"height:100%;width:100%; position: absolute; top:0; bottom:0; left:0; right:0\">" +
                "<td style=\"width:100%;text-align:center\">" +
                "<p style=\"font-size:120%\">" +
                input +
                "</p></td></table></body></html>";
        webView.loadDataWithBaseURL(path, js, "text/html",  "UTF-8", null);
    }
}
