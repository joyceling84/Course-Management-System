import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String id;
    private List<Course> coursesEnrolled;

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Student(String name, String id, List<Course> coursesEnrolled) {
        this.name = name;
        this.id = id;
        this.coursesEnrolled = coursesEnrolled;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Course> getCoursesEnrolled() {
        coursesEnrolled = new ArrayList<Course>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("students.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                for (int i = 3; i < items.length; i++) {
                    if (items[1].equals(this.id))
                        this.coursesEnrolled.add(new Course((items[i]))); 
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return coursesEnrolled;
    }

    // public String toCSVString() {
    //     return name + "," + id+",";
    // }


    public static String matchStudentIDtoName(String id) { 
        List<Student> students = Enroll.readStudentsFromFile();
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student.getName();
            }
        }
        return null;
    }

    public static Student getStudentFromFile(String id) {
        List<Course> coursesEnrolled = new ArrayList<>();
        Student student = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get("students.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                    if (items[1].equals(id)){
                        for (int i = 3; i < items.length; i++) {
                            coursesEnrolled.add(new Course((items[i]))); 
                        }
                        student= new Student (items[0], items[1], coursesEnrolled);
                        break;
                    }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return student;
    }

    // public String toCSVString() {
    //     return name + "," + id+",";
    // }
}
