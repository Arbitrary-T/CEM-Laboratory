package cu.models;

import javafx.beans.property.SimpleStringProperty;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by T on 24/03/2016.
 */
public class EquipmentOnLoan
{
    //should have items....
    private Student student;
    private List<Equipment> equipment = new ArrayList<>();
    private LocalTime leaseStartTime;
    private Duration leaseTimeLeft;
    private String remarks;
    private SimpleStringProperty leaseTimeLeftWrapper = new SimpleStringProperty();
    private boolean stopTimer = false;
    private long secondsPassed = 0;
    private StudentDatabase studentDatabase;

    public EquipmentOnLoan(StudentDatabase studentDatabase, Student student, List<Equipment> equipment, int hours, String remarks)
    {
        this.studentDatabase = studentDatabase;
        setStudent(student);
        setEquipment(equipment);
        setLeaseStartTime();
        setLeaseTimeLeft(hours);
        setRemarks(remarks);
    }

    public Student getStudent()
    {
        return student;
    }
    public void setStudent(Student student)
    {
        this.student = student;
    }
    public void stopTimer(boolean stopTimer)
    {
        this.stopTimer = stopTimer;
    }
    public List<Equipment> getEquipmentIDs()
    {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment)
    {
        this.equipment.clear();
        this.equipment.addAll(equipment);
    }

    public SimpleStringProperty getLeaseStartTime()
    {
        return new SimpleStringProperty(leaseStartTime.getHour()+":"+leaseStartTime.getMinute()+":"+leaseStartTime.getSecond());
    }

    public void setLeaseStartTime()
    {
        this.leaseStartTime = LocalTime.now();
    }
    public long getLeaseTimeDuration()
    {
        return secondsPassed;
    }
    public SimpleStringProperty getLeaseTimeLeft()
    {
        return leaseTimeLeftWrapper;
    }

    public void setLeaseTimeLeft(int hours)
    {
        leaseTimeLeft = Duration.ofHours(hours);
        leaseTimeLeftWrapper.set(String.format("%d:%02d:%02d", hours*60*60 / 3600, (hours*60*60 % 3600) / 60, (hours*60*60 % 60)));
        Timer updateTimeLeft = new Timer();
        updateTimeLeft.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if(!leaseTimeLeft.isZero() || stopTimer)
                {
                    leaseTimeLeft = leaseTimeLeft.minusSeconds(1);
                    int seconds = (int) leaseTimeLeft.getSeconds();
                    secondsPassed++;
                    leaseTimeLeftWrapper.set(String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60)));
                }
                else
                {

                    if(leaseTimeLeft.isZero() || leaseTimeLeft.isNegative())
                    {
                        student.setReturnNotOnTime(student.getReturnNotOnTime());
                        studentDatabase.editStudentEntry(student);
                        //Call method to increase the student's counter in terms of not successfully returning an item
                        //send email to student
                    }
                    updateTimeLeft.cancel();
                    updateTimeLeft.purge();
                    return;
                }
            }
        }, 1000, 1000);
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    @Override
    public String toString()
    {
        return student.getStudentName() + equipment.toString() + leaseStartTime + leaseTimeLeft + remarks;
    }
}
