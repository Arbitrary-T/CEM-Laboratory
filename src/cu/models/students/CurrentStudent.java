package cu.models.students;

/**
 * Created by T on 26/04/2016.
 */
public class CurrentStudent
{
    private Student loadedStudent = null;
    private static CurrentStudent instance;

    public CurrentStudent()
    {

    }

    public static synchronized CurrentStudent getInstance()
    {
        if(instance == null)
        {
            instance = new CurrentStudent();
            return instance;
        }
        else
        {
            return instance;
        }
    }

    public void setLoadedStudent(Student currentStudent)
    {
        loadedStudent = currentStudent;
    }

    public Student getLoadedStudent()
    {
        return loadedStudent;
    }
}
