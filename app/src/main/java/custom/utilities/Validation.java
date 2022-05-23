package custom.utilities;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

public class Validation
{
    //Actions to take for each input field after user presses submit button (such as setting error messages, etc).
    public void SubmitAction(EditText _editTextInput, TextView _errorTextView, Drawable _backgroundForEditText, String _message)
    {
        _editTextInput.setBackgroundDrawable(_backgroundForEditText);
        _errorTextView.setText(_message);
    }

    //Ensures fields are not empty.
    public boolean Validation_IsInputEmpty(EditText _editTextInput, TextView _errorTextView, Drawable _errorBackgroundForEditText, Drawable _correctBackgroundForEditText)
    {
        Drawable backgroundForEditText;
        String message;
        boolean isError;

        if(TextUtils.isEmpty(_editTextInput.getText()))
        {
            message = "Field cannot be empty!";
            backgroundForEditText = _errorBackgroundForEditText;
            isError = true;
        }
        else
        {
            message = "";
            backgroundForEditText = _correctBackgroundForEditText;
            isError = false;
        }

        SubmitAction(_editTextInput, _errorTextView, backgroundForEditText, message);

        return isError;
    }

    //Ensures passwords match.
    public boolean Validation_ArePasswordsDifferent(EditText _editTextPassword, TextView _textViewError, EditText _editTextPasswordConfirm, TextView _textViewErrorConfirm, Drawable _errorBackgroundForEditText, Drawable _correctBackgroundForEditText)
    {
        Drawable backgroundForEditText;
        String message;
        boolean isError;

        if(!TextUtils.isEmpty(_editTextPassword.getText()) && !TextUtils.isEmpty(_editTextPasswordConfirm.getText()))
        {
            if(!_editTextPassword.getText().toString().equals(_editTextPasswordConfirm.getText().toString()))
            {
                backgroundForEditText = _errorBackgroundForEditText;
                message = "Passwords do not match!";
                isError = true;
            }
            else
            {
                backgroundForEditText = _correctBackgroundForEditText;
                message = "";
                isError = false;
            }
        }
        else
        {
            return true;
        }

        SubmitAction(_editTextPassword, _textViewError, backgroundForEditText, message);
        SubmitAction(_editTextPasswordConfirm, _textViewErrorConfirm, backgroundForEditText, message);

        return isError;
    }

    //Ensures passwords are more than 6 characters (Firebase Auth requirement)
    public boolean Validation_ArePasswordsLessThanSixCharacters(EditText _editTextPassword, TextView _textViewError, EditText _editTextPasswordConfirm, TextView _textViewErrorConfirm, Drawable _errorBackgroundForEditText, Drawable _correctBackgroundForEditText)
    {
        Drawable backgroundForEditText;
        boolean isPasswordError;
        boolean isPasswordConfirmError;
        String message;

        if(_editTextPassword.getText().toString().length() < 6)
        {
            backgroundForEditText = _errorBackgroundForEditText;
            isPasswordError = true;
            message = "Password is less than 6 characters!";
        }
        else
        {
            backgroundForEditText = _correctBackgroundForEditText;
            isPasswordError = false;
            message = "";
        }

        SubmitAction(_editTextPassword, _textViewError, backgroundForEditText, message);

        if(_editTextPasswordConfirm.getText().toString().length() < 6)
        {
            backgroundForEditText = _errorBackgroundForEditText;
            isPasswordConfirmError = true;
            message = "Password is less than 6 characters!";
        }
        else
        {
            backgroundForEditText = _correctBackgroundForEditText;
            isPasswordConfirmError = false;
            message = "";
        }

        SubmitAction(_editTextPasswordConfirm, _textViewErrorConfirm, backgroundForEditText, message);

        return (isPasswordError || isPasswordConfirmError);
    }

    public boolean Validation_ArePasswordsIncorrect(EditText _editTextPassword, TextView _textViewError, TextView _sixCharacterPasswordTextView, EditText _editTextPasswordConfirm, TextView _textViewErrorConfirm, TextView _sixCharacterPasswordConfirmTextView, Drawable _errorBackgroundForEditText, Drawable _correctBackgroundForEditText)
    {
        boolean arePasswordsDifferent = Validation_ArePasswordsDifferent(_editTextPassword, _textViewError, _editTextPasswordConfirm, _textViewErrorConfirm, _errorBackgroundForEditText, _correctBackgroundForEditText);
        boolean arePasswordsLessThanSixCharacters = Validation_ArePasswordsLessThanSixCharacters(_editTextPassword, _sixCharacterPasswordTextView, _editTextPasswordConfirm, _sixCharacterPasswordConfirmTextView, _errorBackgroundForEditText, _correctBackgroundForEditText);

        return (arePasswordsDifferent || arePasswordsLessThanSixCharacters);
    }
}
