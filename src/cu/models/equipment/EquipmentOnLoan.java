package cu.models.equipment;

import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import cu.models.utilities.EmailService;
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

    private Student student;
    private List<Equipment> equipment = new ArrayList<>();
    private LocalTime leaseStartTime;
    private Duration leaseTimeLeft;
    private String remarks;
    private SimpleStringProperty leaseTimeLeftWrapper = new SimpleStringProperty();
    private boolean stopTimer = false;
    private long secondsPassed = 0;
    private StudentDatabase studentDatabase;
    private EmailService emailService = new EmailService();

    /**
     * EquipmentOnLoan constructor
     * @param studentDatabase a reference to the students database
     * @param student the student to lease to
     * @param equipment list of equipment (scanned by user)
     * @param hours lease duration
     * @param remarks any remarks e.g. more time for a reason...
     */
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

    /**
     * method used to stop the Timer thread
     * @param stopTimer boolean flag, false stops the thread.
     */
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

    private void setLeaseStartTime()
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

    /**
     * sets the time left for lease and counts down until times out, if the item is not returned by then an email is sent to the student
     * @param hours the lease's time
     */
    private void setLeaseTimeLeft(int hours)
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
                        student.setReturnNotOnTime(student.getReturnNotOnTime() + 1);
                        studentDatabase.editStudentEntry(student);
                        emailService.sendEmail(student.getStudentEmail(),"Late", "You have missed the deadline for returning the borrowed items!\nThe current number of times you missed the deadline is now: " +student.getReturnNotOnTime());
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

    private void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    @Override
    public String toString()
    {
        return student.getStudentName() + equipment.toString() + leaseStartTime + leaseTimeLeft + remarks;
    }
}
