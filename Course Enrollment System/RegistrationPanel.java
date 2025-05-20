import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class RegistrationPanel extends JPanel {
    private JTextField nameField;
    private JTextField contactField;
    private JRadioButton yesButton;
    private JRadioButton noButton;
    private JButton nextButton;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String studentID;
    private List<JComboBox<String>> subjectLists = new ArrayList<>();
    private JPanel subjectPanel;

    public RegistrationPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // First page: Applicant details
        JPanel applicantPanel = new JPanel(new GridBagLayout());
        applicantPanel.setBorder(BorderFactory.createTitledBorder("Applicant Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        applicantPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        applicantPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        applicantPanel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contactField = new JTextField(20);
        applicantPanel.add(contactField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        applicantPanel.add(new JLabel("Are you an MMU student before?"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPanel mmuStudentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yesButton = new JRadioButton("Yes");
        noButton = new JRadioButton("No");
        ButtonGroup group = new ButtonGroup();
        group.add(yesButton);
        group.add(noButton);
        mmuStudentPanel.add(yesButton);
        mmuStudentPanel.add(noButton);
        applicantPanel.add(mmuStudentPanel, gbc);

        List<Student> students = Enroll.readStudentsFromFile();

        // Action listener for yesButton to prompt for student ID
        yesButton.addActionListener(e -> {
            // Retrieve name and contact information
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            while (yesButton.isSelected()) {
                String temp = JOptionPane.showInputDialog(applicantPanel, "Enter your MMU Student ID:");

                if (temp == null) {
                    // User cancelled the input, exit the loop
                    break;
                }

                // Check if the student ID is found
                if (Enroll.findStudentID(temp)) {
                    boolean nameMatches = false;

                    // Check if the student ID matches the entered name
                    for (Student student : students) {
                        if (student.getId().equals(temp) && student.getName().equals(name)) {
                            nameMatches = true;
                            break;
                        }
                    }

                    if (nameMatches) {
                        JOptionPane.showMessageDialog(applicantPanel, "Welcome Back to MMU.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        studentID = temp;
                        break; // Exit the loop when the name matches
                    } else {
                        int response = JOptionPane.showConfirmDialog(applicantPanel,
                                "Existing ID does not match the name entered, try again.", "Error",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        if (response == JOptionPane.CANCEL_OPTION) {
                            break; // Exit the loop if the user chooses to cancel
                        }
                    }
                } else {
                    int response = JOptionPane.showConfirmDialog(applicantPanel,
                            "Student ID not found, try again.", "Error", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                    if (response == JOptionPane.CANCEL_OPTION) {
                        break; // Exit the loop if the user chooses to cancel
                    }
                }
            }
        });

        noButton.addActionListener(e -> {
            // Retrieve name and contact information
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(applicantPanel, "Name and contact number cannot be empty.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {

            if (noButton.isSelected())
                studentID = Enroll.generateStudentID(studentID);

            if (studentID == null) {
                JOptionPane.showMessageDialog(applicantPanel, "Please select no if you are not MMU student before.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String name = nameField.getText().trim();

                JOptionPane.showMessageDialog(applicantPanel,
                        "Successful Registration. Your Student ID is: " + studentID,
                        "Student ID",
                        JOptionPane.INFORMATION_MESSAGE);

                // save ID and name to file
                if (!Enroll.findStudentID(studentID)) {
                    Enroll.writeStudentToFile(name, studentID);
                }

                showCourseRegistrationPanel();
            }

        });
        applicantPanel.add(nextButton, gbc);

        // Second page: Course registration
        JPanel coursePanel = new JPanel(new GridLayout(10, 1, 10, 10));
        coursePanel.setBorder(BorderFactory.createTitledBorder("Course Registration"));

        JLabel discountLabel = new JLabel(
                "<html>You will be entitled to a <b><font color='red'>10% discount</font></b> on the overall fees of the next course if you choose to have a Stackup program registration.</html>");
        coursePanel.add(discountLabel);
        JLabel registrationTypeLabel = new JLabel("Registration Type:");
        JRadioButton singleProgramButton = new JRadioButton("Single Program");
        JRadioButton stackupProgramButton = new JRadioButton("Stackup Program");
        ButtonGroup registrationTypeGroup = new ButtonGroup();
        registrationTypeGroup.add(singleProgramButton);
        registrationTypeGroup.add(stackupProgramButton);

        JLabel levelLabel = new JLabel("Select Level:");
        JCheckBox level1RemedialCheckBox = new JCheckBox("Level 1: Remedial courses");
        JCheckBox level1MatriculationCheckBox = new JCheckBox("Level 1: Matriculation");
        JCheckBox level2CheckBox = new JCheckBox("Level 2: Undergraduate");
        JCheckBox level3CheckBox = new JCheckBox("Level 3: Postgraduate");

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submitRegistration(singleProgramButton, stackupProgramButton,
                level1RemedialCheckBox, level1MatriculationCheckBox, level2CheckBox, level3CheckBox));

        coursePanel.add(registrationTypeLabel);
        coursePanel.add(singleProgramButton);
        coursePanel.add(stackupProgramButton);
        coursePanel.add(levelLabel);
        coursePanel.add(level1RemedialCheckBox);
        coursePanel.add(level1MatriculationCheckBox);
        coursePanel.add(level2CheckBox);
        coursePanel.add(level3CheckBox);
        coursePanel.add(submitButton);

        // Third page: Subject Registration
        // JPanel subjectPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        subjectPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        subjectPanel.setBorder(BorderFactory.createTitledBorder("Subject Registration"));

        // originally enroll button here

        
//Fourth page: Accommodation Registration
        JPanel accommodationPanel = new JPanel(new BorderLayout(10, 10));
        accommodationPanel.setBorder(BorderFactory.createTitledBorder("Accommodation"));
        
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton yesAccommodationButton = new JRadioButton("Yes");
        JRadioButton noAccommodationButton = new JRadioButton("No");
        buttonGroup.add(yesAccommodationButton);
        buttonGroup.add(noAccommodationButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new Label("Accommocation Choice of Student: "));
        buttonPanel.add(yesAccommodationButton);
        buttonPanel.add(noAccommodationButton);

        JPanel accommodationTypeButtonPanel = new JPanel(new GridLayout(0, 1));
        accommodationTypeButtonPanel.setVisible(false);
        ButtonGroup accommodationTypeButtonGroup = new ButtonGroup();
        Map<JRadioButton, Fee> accommodationButtonMap = new HashMap<>();

        List<Fee> accommodationType = Fee.getAccommodation();
        for (Fee accommodation : accommodationType) {
            String option = accommodation.getName() + " - RM" + accommodation.getAmount();
            JRadioButton accommodationTypeButton = new JRadioButton(option);
            accommodationTypeButtonGroup.add(accommodationTypeButton);
            accommodationTypeButtonPanel.add(accommodationTypeButton);
            accommodationButtonMap.put(accommodationTypeButton, accommodation);
        }

        ActionListener yesNoButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (yesAccommodationButton.isSelected()) {
                    accommodationTypeButtonPanel.setVisible(true);
                } else {
                    accommodationTypeButtonPanel.setVisible(false);
                }
                revalidate();
                repaint();
            }
        };
        
        yesAccommodationButton.addActionListener(yesNoButtonListener);
        noAccommodationButton.addActionListener(yesNoButtonListener);

        JButton proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(e -> {
            boolean isAccommodationSelected = yesAccommodationButton.isSelected();
            String selectedAccommodationName = "no accommodation";

            for (JRadioButton accommodationTypeButton : accommodationButtonMap.keySet()) {
                if (accommodationTypeButton.isSelected()) {
                    selectedAccommodationName = accommodationButtonMap.get(accommodationTypeButton).getName();
                    break;
                }
            }
            //if yesAccommodation Button is selected, output message dialog
            if (isAccommodationSelected) {
                JOptionPane.showMessageDialog(this, "Accommodation succeselected: " + selectedAccommodationName, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No accommodation selected.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            Billing.updateBillFile(studentID, isAccommodationSelected ? "accommodation" : "no accommodation", selectedAccommodationName);

        });
        accommodationPanel.add(buttonPanel, BorderLayout.NORTH);
        accommodationPanel.add(accommodationTypeButtonPanel, BorderLayout.CENTER);
        accommodationPanel.add(proceedButton, BorderLayout.SOUTH);

        mainPanel.add(applicantPanel, "ApplicantPanel");
        mainPanel.add(coursePanel, "CoursePanel");
        mainPanel.add(subjectPanel,"SubjectPanel");
        mainPanel.add(accommodationPanel, "AccommocationPanel");

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        cardLayout.show(mainPanel, "ApplicantPanel");
    }

    private void enrollSelectedCourse(Course selectedCourse) {
        if (selectedCourse != null) {
            Course.enrollCourse(studentID, selectedCourse);
            JOptionPane.showMessageDialog(this, "Enrolled in: " + selectedCourse.getName(), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No course selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCourseRegistrationPanel() {
        String name = nameField.getText();
        String contact = contactField.getText();

        if (name.isEmpty() || contact.isEmpty() || (!yesButton.isSelected() && !noButton.isSelected())) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            cardLayout.show(mainPanel, "CoursePanel");
        }
    }

    private String selectedProgramType;
    private List<String> selectedLevels = new ArrayList<>();

    private void submitRegistration(JRadioButton singleProgramButton, JRadioButton stackupProgramButton,
            JCheckBox level1RemedialCheckBox, JCheckBox level1MatriculationCheckBox, JCheckBox level2CheckBox,
            JCheckBox level3CheckBox) {
        boolean isSingleProgram = singleProgramButton.isSelected();
        boolean isStackupProgram = stackupProgramButton.isSelected();
        selectedLevels.clear();

        int selectedLevelsCount = 0;
        if (level1RemedialCheckBox.isSelected()) {
            selectedLevelsCount++;
            selectedLevels.add("Remedial");
        }
        if (level1MatriculationCheckBox.isSelected()) {
            selectedLevelsCount++;
            selectedLevels.add("Matriculation");
        }
        if (level2CheckBox.isSelected()) {
            selectedLevelsCount++;
            selectedLevels.add("Undergraduate");
        }
        if (level3CheckBox.isSelected()) {
            selectedLevelsCount++;
            selectedLevels.add("Postgraduate");
        }

        if (isSingleProgram) {
            if (selectedLevelsCount == 1) {
                selectedProgramType = "Single Program";
                JOptionPane.showMessageDialog(this, "Registration successful for Single Program.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "SubjectPanel");
            } else {
                JOptionPane.showMessageDialog(this, "Please select exactly one level for Single Program.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (isStackupProgram) {
            if (selectedLevelsCount > 0 && selectedLevelsCount <= 3) {
                selectedProgramType = "Stackup Program";
                JOptionPane.showMessageDialog(this, "Registration successful for Stackup Program.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "SubjectPanel");
            } else {
                JOptionPane.showMessageDialog(this, "Please select up to 3 levels for Stackup Program.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a registration type.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        Enroll.updateStudentFile(studentID, isStackupProgram ? "stackup" : "single");
        // getSelectedProgram(selectedPrograms);
        loadCoursesBasedOnSelection();
    }

    private void loadCoursesBasedOnSelection() {
        List<Course> allCourses = Course.readCoursesFromFile();
        subjectLists.clear();

        // Create and populate JComboBoxes based on selected levels
        for (String selectedLevel : selectedLevels) {
            JComboBox<String> subjectList = new JComboBox<>();
            for (Course course : allCourses) {
                String[] courseLevels = course.getType().split(", ");
                for (String courseLevel : courseLevels) {
                    if (selectedLevel.equalsIgnoreCase(courseLevel.trim())) {
                        subjectList.addItem(course.getName());
                        break;
                    }
                }
            }
            subjectLists.add(subjectList);
        }

        subjectPanel.removeAll(); // Clear previous components
        for (JComboBox<String> subjectList : subjectLists) {
            subjectPanel.add(subjectList);
        }

        JButton enrollButton = new JButton("Enroll");
        enrollButton.addActionListener(e -> {
            Course.storePastEnrolledCourse(studentID);
            Course.clearCurrentEnrolledCourse(studentID);
            for (JComboBox<String> subjectList : subjectLists) {
                String selectedCourseName = (String) subjectList.getSelectedItem();
                if (selectedCourseName != null) {
                    Course selectedCourse = Course.matchCourseFromFile(selectedCourseName);

                    enrollSelectedCourse(selectedCourse);
                }
            }
            cardLayout.show(mainPanel, "AccommocationPanel");
        });

        // subjectPanel.add(subjectList);
        subjectPanel.add(enrollButton);
        subjectPanel.revalidate();
        subjectPanel.repaint();
    }

    // private StringBuilder getSelectedProgram(StringBuilder sp) {
    // return sp;
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MMU Enrollment System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new RegistrationPanel());
            frame.setVisible(true);
        });
    }
}
