package cu.models;

/**
 * Created by T on 05/10/2015.
 */
public class Student
{
    private String cardUID = "";
    private String studentName = "";
    private String studentEmail = "";
    private String studentCourse = "";
    private String studentPhoneNumber;
    private int studentID;

    public Student(String cardUID, String studentName, int studentID, String studentEmail, String studentCourse, String studentPhoneNumber)
    {
        setUID(cardUID);
        setName(studentName);
        setID(studentID);
        setCourse(studentCourse);
        setEmail(studentEmail);
        setPhoneNumber(studentPhoneNumber);
    }

    public void setUID(String cardUID)
    {
        this.cardUID = cardUID;
    }

    public void setEmail(String studentEmail)
    {
        this.studentEmail = studentEmail;
    }

    public void setCourse(String studentCourse)
    {
        this.studentCourse = studentCourse;
    }

    public void setPhoneNumber(String studentPhoneNumber)
    {
        this.studentPhoneNumber = studentPhoneNumber;
    }

    public void setName(String studentName)
    {
        this.studentName = studentName;
    }

    public void setID(int studentID)
    {
        this.studentID = studentID;
    }

    public String getCardUID()
    {
        return cardUID;
    }
    public String getStudentName()
    {
        return studentName;
    }
    public String getStudentEmail()
    {
        return studentEmail;
    }
    public String getStudentCourse()
    {
        return studentCourse;
    }
    public String getStudentPhoneNumber()
    {
        return studentPhoneNumber;
    }
    public int getStudentID()
    {
        return studentID;
    }
}