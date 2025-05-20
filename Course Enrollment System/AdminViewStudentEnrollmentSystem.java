import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class AdminViewStudentEnrollmentSystem extends JPanel {
    private JPanel studentEnrollmentPanel, mainPanel;
    private JTextField studentIDField;
    private JButton submitIDButton, backButton;
    private JPanel submitBackPanel;
    private boolean studentIDEntered = false;
    private JFrame parentFrame;

    public AdminViewStudentEnrollmentSystem(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setSize(800, 600);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        ImageIcon imageIcon = new ImageIcon("APPL_MMU_IMG_1.png");
        JLabel imageLabel = new JLabel(imageIcon);

        studentEnrollmentPanel = new JPanel();
        studentEnrollmentPanel.setLayout(new BoxLayout(studentEnrollmentPanel, BoxLayout.Y_AXIS));
        studentEnrollmentPanel.setBorder(BorderFactory.createTitledBorder("Student Enrollment Details: "));
        studentEnrollmentPanel.setVisible(true);

        JPanel studentIDPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        studentIDPanel.setBorder(BorderFactory.createTitledBorder("Enter Student ID"));

        studentIDField = new JTextField(20);
        submitIDButton = new JButton("Submit ID");

        submitIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDField.getText().trim();
                if (!Enroll.findStudentID(studentID)) {
                    JOptionPane.showMessageDialog(AdminViewStudentEnrollmentSystem.this,
                            "Please enter a valid Student ID.", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    studentIDField.setText("");
                } else {
                    studentIDEntered = true;
                    displayEnrollmentDetails(studentID);
                }
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            parentFrame.setContentPane(AdminContentPane.getInstance(parentFrame));
            parentFrame.revalidate();
        });

        submitBackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitBackPanel.add(submitIDButton);
        submitBackPanel.add(backButton);

        studentIDPanel.add(new JLabel("Student ID:"));
        studentIDPanel.add(studentIDField, BorderLayout.WEST);
        studentIDPanel.add(submitBackPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        mainPanel.add(imageLabel, BorderLayout.NORTH);
        mainPanel.add(studentIDPanel, BorderLayout.CENTER);
        JScrollPane studentEnrollmentScrollPane = new JScrollPane(studentEnrollmentPanel);
        studentEnrollmentScrollPane.setPreferredSize(new Dimension(800, 500));
        mainPanel.add(studentEnrollmentScrollPane, BorderLayout.SOUTH);
        add(new JScrollPane(mainPanel));
    }

    private void displayEnrollmentDetails(String studentID) {
        studentEnrollmentPanel.removeAll();
        Student student = Student.getStudentFromFile(studentID);
        List<Course> coursesEnrolled = student.getCoursesEnrolled();

        JLabel studentIDLabel = new JLabel("Student ID: " + student.getId());
        JLabel studentNameLabel = new JLabel("Student Name: " + student.getName());
        studentEnrollmentPanel.add(studentIDLabel);
        studentEnrollmentPanel.add(studentNameLabel);

        JLabel courseTakenLabel = new JLabel("---Course Taken---");
        studentEnrollmentPanel.add(courseTakenLabel);

        Map<String, List<Course>> coursesByType = new HashMap<>();
        for (Course course : coursesEnrolled) {
            Course matchedCourse = Course.matchCourseFromFile(course.getName());
            if (matchedCourse != null) {
                String courseType = matchedCourse.getType();
                coursesByType.computeIfAbsent(courseType, k -> new ArrayList<>()).add(matchedCourse);
            }
        }

        for (Map.Entry<String, List<Course>> entry : coursesByType.entrySet()) {
            String type = entry.getKey();
            List<Course> courseList = entry.getValue();

            StringBuilder typeInfo = new StringBuilder(type + ": ");
            for (Course course : courseList) {
                typeInfo.append(course.getName()).append(", ");
            }

            if (typeInfo.length() > 0) {
                typeInfo.setLength(typeInfo.length() - 2);
            }

            JLabel typeLabel = new JLabel(typeInfo.toString());
            studentEnrollmentPanel.add(typeLabel);
        }

        studentEnrollmentPanel.revalidate();
        studentEnrollmentPanel.repaint();
    }

}
