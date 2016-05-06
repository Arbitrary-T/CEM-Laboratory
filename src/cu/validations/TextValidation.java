package cu.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by T on 24/12/2015.
 */
public class TextValidation
{
    public TextValidation()
    {

    }

    /**
     * Checks whether an email entered is a valid cov. uni email.
     * @param email the email to be validated
     * @return true if valid
     */
    public boolean checkValidEmail(String email)
    {
        return email.endsWith("@uni.coventry.ac.uk");
    }

    /**
     * basic check that a number is a 'valid' (not necessary working) UK number
     * @param phoneNumber the phone number to be validated
     * @return true if valid
     */

    public boolean isValidPhoneNumber(String phoneNumber)
    {
        return !phoneNumber.isEmpty() && phoneNumber.matches("\\d{11}");
    }

    /**
     * checks whether a student ID is valid
     * @param studentID the id to check
     * @return true if valid
     */
    public boolean isValidStudentID(String studentID)
    {
        return !studentID.isEmpty() && studentID.matches("\\d*") && studentID.length() < 8;
    }

    /**
     * extracts the first integer in a string
     * @param text the text to 'filter'
     * @return the first integer in the string
     */
    public int textToFirstInt(String text)
    {
        Pattern intsOnly = Pattern.compile("^[\\d]*");
        Matcher makeMatch = intsOnly.matcher(text);
        makeMatch.find();
        String inputInt = makeMatch.group();
        int inputToInt = -1;
        try
        {
            inputToInt = Integer.parseInt(inputInt);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return inputToInt;
    }
}