package cu.models.students;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by T on 05/10/2015.
 */
public class Student
{
    //A generic pojo.
    private SimpleStringProperty cardUID = new SimpleStringProperty();
    private SimpleStringProperty studentName = new SimpleStringProperty();
    private SimpleStringProperty studentEmail = new SimpleStringProperty();
    private SimpleStringProperty studentCourse = new SimpleStringProperty();
    private SimpleStringProperty studentPhoneNumber = new SimpleStringProperty();
    private SimpleIntegerProperty studentID = new SimpleIntegerProperty();
    private int faultyReturns = 0;
    private int totalReturns = 0;
    private long equipmentUsageTime = 0;
    private int returnOnTime = 0;
    public Student(String cardUID, String studentName, int studentID, String studentEmail, String studentCourse, String studentPhoneNumber, int faultyReturns, int totalReturns, long equipmentUsageTime, int returnNotOnTime)
    {
        setCardUID(cardUID);
        setID(studentID);
        setStudentName(studentName);
        setStudentEmail(studentEmail);
        setStudentCourse(studentCourse);
        setPhoneNumber(studentPhoneNumber);
        setFaultyReturns(faultyReturns);
        setTotalReturns(totalReturns);
        setEquipmentUsageTime(equipmentUsageTime);
        setReturnNotOnTime(returnNotOnTime);
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



    public int getFaultyReturns() {
        return faultyReturns;
    }

    public void setFaultyReturns(int faultyReturns) {
        this.faultyReturns = faultyReturns;
    }

    public int getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(int totalReturns) {
        this.totalReturns = totalReturns;
    }

    public long getEquipmentUsageTime() {
        return equipmentUsageTime;
    }

    public void setEquipmentUsageTime(long equipmentUsageTime) {
        this.equipmentUsageTime = equipmentUsageTime;
    }

    public int getReturnNotOnTime() {
        return returnOnTime;
    }

    public void setReturnNotOnTime(int returnOnTime) {
        this.returnOnTime = returnOnTime;
    }
    @Override
    public String toString()
    {
        return cardUID.toString() + "\t" + studentID + "\t" + studentName.toString();
    }
    @Override
    public int hashCode()
    {
        return (7 * 31) + getStudentID();
    }
    @Override
    public boolean equals(Object object)
    {
        if(object == null || getClass() != object.getClass())
        {
            return false;
        }
        return this.getStudentID() == ((Student) object).getStudentID();
    }
}
