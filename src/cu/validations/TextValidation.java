package cu.validations;

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
        return !email.isEmpty() && email.endsWith("@coventry.ac.uk");
    }

    public boolean isValidPhoneNumber(String phoneNumber)
    {
        return !phoneNumber.isEmpty() && phoneNumber.matches("\\d{11}");
    }

    public boolean isValidStudentID(String studentID)
    {
        return !studentID.isEmpty() && studentID.matches("\\d*") && studentID.length() < 8;
    }
}
