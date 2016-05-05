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

    public boolean checkValidEmail(String email)
    {
        return email.endsWith("@uni.coventry.ac.uk");
    }

    public boolean isValidPhoneNumber(String phoneNumber)
    {
        return !phoneNumber.isEmpty() && phoneNumber.matches("\\d{11}");
    }

    public boolean isValidStudentID(String studentID)
    {
        return !studentID.isEmpty() && studentID.matches("\\d*") && studentID.length() < 8;
    }
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