import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private int level;
    private String type;
    private String name;
    private double fee;

    public Course(int level, String type, String name, double fee) {
        this.level = level;
        this.type = type;
        this.name = name;
        this.fee = fee;
    }

    public Course(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public double getFee() {
        return fee;
    }

    public static List<Course> readCoursesFromFile() {
        List <Course> courses = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("courses.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                int level = Integer.parseInt(items[0]);
                String type = items[1];
                String name = items[2];
                double fee = Double.parseDouble(items[3]);
                courses.add(new Course(level, type, name, fee));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return courses;
    }

    public static Course matchCourseFromFile(String courseName) {
        Course course = null;
        try {
            List<String> lines = Files.readAllLines(Paths.get("courses.csv"));
            for (String line : lines) {
                String[] items = line.split(",");
                    if (items[2].equals(courseName)){
                        course = new Course(Integer.parseInt(items[0]), items[1], items[2], Double.parseDouble(items[3]));
                        break;
                    }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return course;
    }

    public static void enrollCourse(String id, Course course) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("students.csv"));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] items = line.split(","); // Split the line by comma

                if (items.length > 0 && items[1].equals(id)) {
                    // Append the course code to the current line
                    StringBuilder updatedLine = new StringBuilder(line);
                    updatedLine.append(course.getName()).append(",");
                    updatedLines.add(updatedLine.toString());
                } 
                else {
                    updatedLines.add(line); // Keep the line unchanged
                }
            }
            // Write the updated content back to the file
            Files.write(Paths.get("students.csv"), updatedLines);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void clearCurrentEnrolledCourse(String id) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("students.csv"));
            List<String> updatedLines = new ArrayList<>();
            for (String line : lines) {
                String[] items = line.split(",");
                if (items.length > 0 && items[1].equals(id)) {
                    StringBuilder updatedLine = new StringBuilder(items[0] + "," + items[1] + "," + items[2] + ",");
                    updatedLines.add(updatedLine.toString());
                } else {
                    updatedLines.add(line);
                }
            }
            Files.write(Paths.get("students.csv"), updatedLines);
        } catch (IOException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
    }

    public static void storePastEnrolledCourse(String id) {
        try {
            List<String> studentLines = Files.readAllLines(Paths.get("students.csv"));
            List<String> pastEnrollmentLines = Files.readAllLines(Paths.get("studentPastEnrollment.csv"));
            List<String> updatedLines = new ArrayList<>();
            boolean idExists = false;

            for (String pastEnrollmentLine : pastEnrollmentLines) {
                String[] items = pastEnrollmentLine.split(",");
                if (items.length > 1 && items[1].equals(id)) {
                    idExists = true;
                    for (String studentLine : studentLines) {
                        String[] studentItems = studentLine.split(",");
                        if (studentItems.length > 1 && studentItems[1].equals(id)) {
                            for (int i = 3; i < studentItems.length; i++) {
                                pastEnrollmentLine += "," + studentItems[i];
                            }
                            break;
                        }
                    }
                }
                updatedLines.add(pastEnrollmentLine);
            }

            if (!idExists) {
                for (String studentLine : studentLines) {
                    String[] items = studentLine.split(",");
                    if (items.length > 1 && items[1].equals(id)) {
                        StringBuilder newLine = new StringBuilder();
                        newLine.append(items[0]).append(",").append(items[1]);
                        for (int i = 3; i < items.length; i++) {
                            newLine.append(",").append(items[i]);
                        }
                        updatedLines.add(newLine.toString());
                        break;
                    }
                }
            }

            Files.write(Paths.get("studentPastEnrollment.csv"), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String toCSVString() {
        return  level + "," + type + "," + name + "," + fee;
    }
}
