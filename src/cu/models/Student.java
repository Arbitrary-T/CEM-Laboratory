package cu.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by T on 05/10/2015.
 */
public class Student
{
    private SimpleStringProperty cardUID ;
    private SimpleStringProperty studentName ;
    private SimpleStringProperty studentEmail ;
    private SimpleStringProperty studentCourse ;
    private SimpleStringProperty studentPhoneNumber;
    private SimpleIntegerProperty studentID;

    public Student(String cardUID, String studentName, int studentID, String studentEmail, String studentCourse, String studentPhoneNumber)
    {
        this.cardUID = new SimpleStringProperty(cardUID);
        this.studentID = new SimpleIntegerProperty(studentID);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.studentCourse = new SimpleStringProperty(studentCourse);
        this.studentPhoneNumber = new SimpleStringProperty(studentPhoneNumber);
    }

    public void setCardUID(String cardUID)
    {
        this.cardUID.set(cardUID);
    }

    public void setStudentEmail(String studentEmail)
    {
        this.studentEmail.set(studentEmail);
    }

    public void setStudentCourse(String studentCourse)
    {
        this.studentCourse.set(studentCourse);
    }

    public void setPhoneNumber(String studentPhoneNumber)
    {
        this.studentPhoneNumber.set(studentPhoneNumber);
    }

    public void setStudentName(String studentName)
    {
        this.studentName.set(studentName);
    }

    public void setID(int studentID)
    {
        this.studentID.set(studentID);
    }

    public String getCardUID()
    {
        return cardUID.get();
    }
    public String getStudentName()
    {
        return studentName.get();
    }
    public String getStudentEmail()
    {
        return studentEmail.get();
    }
    public String getStudentCourse()
    {
        return studentCourse.get();
    }
    public String getStudentPhoneNumber()
    {
        return this.studentPhoneNumber.get();
    }
    public int getStudentID()
    {
        return this.studentID.get();
    }
}
