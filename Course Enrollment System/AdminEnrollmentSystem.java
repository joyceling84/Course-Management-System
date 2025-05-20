import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class AdminEnrollmentSystem extends JPanel {

    private JTextField studentIDField, studentNameField, contactField;
    private JRadioButton singleProgramButton, stackupProgramButton;
    private JCheckBox level1RemedialCheckBox, level1MatriculationCheckBox, level2CheckBox, level3CheckBox;
    private JButton submitButton, backButton;
    private ItemListener singleListener;
    private ItemListener stackupListener;

    private JPanel subjectPanel;
    private List<JComboBox<String>> subjectLists;
    private JPanel mainPanel;
    private JScrollPane mainScrollPane;

    private List<String> selectedLevels;
    private String studentID;

    public AdminEnrollmentSystem(JFrame parentFrame) {

        selectedLevels = new ArrayList<>();
        subjectLists = new ArrayList<>();

        ImageIcon imageIcon = new ImageIcon("APPL_MMU_IMG_1.png");
        JLabel imageLabel = new JLabel(imageIcon);

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Admin - Student Registration"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add the image label at the top
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(imageLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;

        // Student ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        studentIDField = new JTextField(20);
        mainPanel.add(studentIDField, gbc);

        // Student Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Student Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        studentNameField = new JTextField(20);
        mainPanel.add(studentNameField, gbc);

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contactField = new JTextField(20);
        mainPanel.add(contactField, gbc);

        // Registration Type
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Registration Type:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPanel registrationTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleProgramButton = new JRadioButton("Single Program");
        stackupProgramButton = new JRadioButton("Stackup Program");
        // Add item listeners to the radio buttons
        singleProgramButton.addItemListener(e -> handleProgramSelection());
        stackupProgramButton.addItemListener(e -> handleProgramSelection());

        ButtonGroup registrationTypeGroup = new ButtonGroup();
        registrationTypeGroup.add(singleProgramButton);
        registrationTypeGroup.add(stackupProgramButton);
        registrationTypePanel.add(singleProgramButton);
        registrationTypePanel.add(stackupProgramButton);
        mainPanel.add(registrationTypePanel, gbc);

        // Select Level
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(new JLabel("Select Level:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPanel levelPanel = new JPanel(new GridLayout(4, 1));
        level1RemedialCheckBox = new JCheckBox("Level 1: Remedial courses");
        level1MatriculationCheckBox = new JCheckBox("Level 1: Matriculation");
        level2CheckBox = new JCheckBox("Level 2: Undergraduate");
        level3CheckBox = new JCheckBox("Level 3: Postgraduate");
        levelPanel.add(level1RemedialCheckBox);
        levelPanel.add(level1MatriculationCheckBox);
        levelPanel.add(level2CheckBox);
        levelPanel.add(level3CheckBox);
        mainPanel.add(levelPanel, gbc);

        // Subject Panel (initially hidden)
        subjectPanel = new JPanel(new GridLayout(10, 1, 10, 10));
        subjectPanel.setVisible(false); // Hide initially

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(subjectPanel, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            submitRegistration();
            if (!studentIDField.getText().isEmpty() && !studentNameField.getText().isEmpty()
                    && !contactField.getText().isEmpty())
                enrollSelectedCourses();
        });
        mainPanel.add(submitButton, gbc);

        // Back Button
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            parentFrame.setContentPane(AdminContentPane.getInstance(parentFrame));
            parentFrame.revalidate();
        });
        mainPanel.add(backButton, gbc);

        mainScrollPane = new JScrollPane(mainPanel);
        add(mainScrollPane);

        // Add item listeners to checkboxes to show/hide subject panel
        level1RemedialCheckBox.addItemListener(e -> toggleSubjectPanelVisibility());
        level1MatriculationCheckBox.addItemListener(e -> toggleSubjectPanelVisibility());
        level2CheckBox.addItemListener(e -> toggleSubjectPanelVisibility());
        level3CheckBox.addItemListener(e -> toggleSubjectPanelVisibility());
    }

    private void toggleSubjectPanelVisibility() {
        boolean anyCheckboxSelected = level1RemedialCheckBox.isSelected() ||
                level1MatriculationCheckBox.isSelected() ||
                level2CheckBox.isSelected() ||
                level3CheckBox.isSelected();

        if (anyCheckboxSelected) {
            updateSubjectPanel();
        } else {
            subjectPanel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    private void updateSubjectPanel() {
        subjectPanel.removeAll();
        subjectLists.clear();

        if (level1RemedialCheckBox.isSelected()) {
            addSubjectList("Remedial");
        }
        if (level1MatriculationCheckBox.isSelected()) {
            addSubjectList("Matriculation");
        }
        if (level2CheckBox.isSelected()) {
            addSubjectList("Undergraduate");
        }
        if (level3CheckBox.isSelected()) {
            addSubjectList("Postgraduate");
        }

        subjectPanel.setVisible(true);
        subjectPanel.revalidate();
        subjectPanel.repaint();
    }

    private void removeAllCheckboxListeners() {
        for (ItemListener listener : level1RemedialCheckBox.getItemListeners()) {
            level1RemedialCheckBox.removeItemListener(listener);
        }
        for (ItemListener listener : level1MatriculationCheckBox.getItemListeners()) {
            level1MatriculationCheckBox.removeItemListener(listener);
        }
        for (ItemListener listener : level2CheckBox.getItemListeners()) {
            level2CheckBox.removeItemListener(listener);
        }
        for (ItemListener listener : level3CheckBox.getItemListeners()) {
            level3CheckBox.removeItemListener(listener);
        }
    }

    private void handleProgramSelection() {
        clearCheckboxSelections();
        ActionListener programChangeListener = e -> {
            if (singleProgramButton.isSelected()) {
                stackupProgramButton.setSelected(false);
                removeAllCheckboxListeners();
                setSingleProgramSelection();
            } else if (stackupProgramButton.isSelected()) {
                singleProgramButton.setSelected(false);
                removeAllCheckboxListeners();
                setStackupProgramSelection();
            }
            toggleSubjectPanelVisibility();
        };

        singleProgramButton.addActionListener(programChangeListener);
        stackupProgramButton.addActionListener(programChangeListener);
    }

    private void setSingleProgramSelection() {
        singleListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox selectedCheckBox = (JCheckBox) e.getSource();
                if (selectedCheckBox.isSelected()) {
                    level1RemedialCheckBox.removeItemListener(this);
                    level1MatriculationCheckBox.removeItemListener(this);
                    level2CheckBox.removeItemListener(this);
                    level3CheckBox.removeItemListener(this);

                    if (level1RemedialCheckBox != selectedCheckBox)
                        level1RemedialCheckBox.setSelected(false);
                    if (level1MatriculationCheckBox != selectedCheckBox)
                        level1MatriculationCheckBox.setSelected(false);
                    if (level2CheckBox != selectedCheckBox)
                        level2CheckBox.setSelected(false);
                    if (level3CheckBox != selectedCheckBox)
                        level3CheckBox.setSelected(false);

                    level1RemedialCheckBox.addItemListener(this);
                    level1MatriculationCheckBox.addItemListener(this);
                    level2CheckBox.addItemListener(this);
                    level3CheckBox.addItemListener(this);

                    toggleSubjectPanelVisibility();
                }
            }
        };

        level1RemedialCheckBox.addItemListener(singleListener);
        level1MatriculationCheckBox.addItemListener(singleListener);
        level2CheckBox.addItemListener(singleListener);
        level3CheckBox.addItemListener(singleListener);
    }

    private void setStackupProgramSelection() {

        stackupListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox selectedCheckBox = (JCheckBox) e.getSource();
                int selectedCount = 0;
                if (level1RemedialCheckBox.isSelected())
                    selectedCount++;
                if (level1MatriculationCheckBox.isSelected())
                    selectedCount++;
                if (level2CheckBox.isSelected())
                    selectedCount++;
                if (level3CheckBox.isSelected())
                    selectedCount++;

                if (selectedCheckBox.isSelected() && selectedCount > 3) {
                    JOptionPane.showMessageDialog(AdminEnrollmentSystem.this,
                            "You can select up to 3 levels for stackup program.", "Selection Limit",
                            JOptionPane.WARNING_MESSAGE);
                    selectedCheckBox.setSelected(false);
                }
                toggleSubjectPanelVisibility();
            }
        };

        level1RemedialCheckBox.addItemListener(stackupListener);
        level1MatriculationCheckBox.addItemListener(stackupListener);
        level2CheckBox.addItemListener(stackupListener);
        level3CheckBox.addItemListener(stackupListener);
    }

    private void clearCheckboxSelections() {
        level1RemedialCheckBox.setSelected(false);
        level1MatriculationCheckBox.setSelected(false);
        level2CheckBox.setSelected(false);
        level3CheckBox.setSelected(false);
        toggleSubjectPanelVisibility();
    }

    private void addSubjectList(String level) {
        JComboBox<String> subjectList = new JComboBox<>();
        List<Course> courses = Course.readCoursesFromFile();

        for (Course course : courses) {
            if (course.getType().equals(level)) {
                subjectList.addItem(course.getName());
            }
        }

        subjectLists.add(subjectList);
        subjectPanel.add(new JLabel(level));
        subjectPanel.add(subjectList);
    }

    private void enrollSelectedCourses() {
        Course.storePastEnrolledCourse(studentID);
        Course.clearCurrentEnrolledCourse(studentID);

        for (JComboBox<String> subjectList : subjectLists) {
            String selectedCourseName = (String) subjectList.getSelectedItem();
            if (selectedCourseName != null) {
                Course selectedCourse = Course.matchCourseFromFile(selectedCourseName);
                enrollSelectedCourse(selectedCourse);
            }
        }
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

    private void submitRegistration() {
        studentID = studentIDField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String contact = contactField.getText().trim();
        boolean isStackup = stackupProgramButton.isSelected();

        if (studentID.isEmpty() || studentName.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Enroll.writeStudentToFile(studentName, studentID);
        Enroll.updateStudentFile(studentID, isStackup ? "stackup" : "single");

        selectedLevels.clear();
        if (level1RemedialCheckBox.isSelected())
            selectedLevels.add("Remedial");
        if (level1MatriculationCheckBox.isSelected())
            selectedLevels.add("Matriculation");
        if (level2CheckBox.isSelected())
            selectedLevels.add("Undergraduate");
        if (level3CheckBox.isSelected())
            selectedLevels.add("Postgraduate");

        if (selectedLevels.isEmpty()) {
            JOptionPane.showMessageDialog(this, "At least one level must be selected.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Student ID: ").append(studentID).append("\n");
        message.append("Student Name: ").append(studentName).append("\n");
        message.append("Contact: ").append(contact).append("\n");
        message.append("Registration Type: ").append(isStackup ? "Stackup Program" : "Single Program").append("\n");
        message.append("Selected Levels: ").append(String.join(", ", selectedLevels)).append("\n");

        JOptionPane.showMessageDialog(this, message.toString(), "Registration Details",
                JOptionPane.INFORMATION_MESSAGE);

        toggleSubjectPanelVisibility();
    }

}
