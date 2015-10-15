package cu.objects;

/**
 * Created by T on 05/10/2015.
 */
public class StudentTag
{
    String cardUID = "";
    String studentName = "";
    int studentID;

    public StudentTag(String cardUID, String studentName, int studentID)
    {
        setUID(cardUID);
        setStudentName(studentName);
        setStudentID(studentID);
    }
    public void setUID(String cardUID)
    {
        this.cardUID = cardUID;
    }
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }
    public void setStudentID(int studentID)
    {
        this.studentID = studentID;
    }
}
