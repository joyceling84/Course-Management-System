import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAccommodationSystem extends JPanel {
    private JPanel accommodationPanel;
    private ButtonGroup buttonGroup;
    private JRadioButton yesAccommodationButton;
    private JRadioButton noAccommodationButton;
    private JPanel buttonPanel, proceedBackPanel;
    private JPanel accommodationTypeButtonPanel;
    private ButtonGroup accommodationTypeButtonGroup;
    private Map<JRadioButton, Fee> accommodationButtonMap;
    private JButton proceedButton, backButton;
    private JTextField studentIDField;
    private JButton submitIDButton;

    private JFrame parentFrame;
    private boolean studentIDEntered = false;

    public AdminAccommodationSystem(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); 

        ImageIcon imageIcon = new ImageIcon("APPL_MMU_IMG_1.png");
        JLabel logoImageLabel = new JLabel(imageIcon);
        mainPanel.add(logoImageLabel, BorderLayout.NORTH); // Add logo at the top

        JPanel studentIDPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        studentIDPanel.setBorder(BorderFactory.createTitledBorder("Enter Student ID"));

        studentIDField = new JTextField(20);
        submitIDButton = new JButton("Submit ID");

        submitIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDField.getText().trim();
                if (!Enroll.findStudentID(studentID)) {
                    JOptionPane.showMessageDialog(AdminAccommodationSystem.this,
                            "Please enter a valid Student ID.", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    studentIDField.setText("");
                } else {
                    studentIDEntered = true;
                }
            }
        });

        studentIDPanel.add(new JLabel("Student ID:"));
        studentIDPanel.add(studentIDField);
        studentIDPanel.add(submitIDButton);

        mainPanel.add(studentIDPanel, BorderLayout.CENTER); 

        accommodationPanel = new JPanel(new BorderLayout(10, 10));
        accommodationPanel.setBorder(BorderFactory.createTitledBorder("Accommodation"));

        buttonGroup = new ButtonGroup();
        yesAccommodationButton = new JRadioButton("Yes");
        noAccommodationButton = new JRadioButton("No");
        buttonGroup.add(yesAccommodationButton);
        buttonGroup.add(noAccommodationButton);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(new JLabel("Accommodation Choice of Student: "));
        buttonPanel.add(yesAccommodationButton);
        buttonPanel.add(noAccommodationButton);

        accommodationTypeButtonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        accommodationTypeButtonPanel.setVisible(false);
        accommodationTypeButtonGroup = new ButtonGroup();
        accommodationButtonMap = new HashMap<>();

        List<Fee> accommodationType = Fee.getAccommodation();
        for (Fee accommodation : accommodationType) {
            String option = accommodation.getName() + " - RM" + accommodation.getAmount();
            JRadioButton accommodationTypeButton = new JRadioButton(option);
            accommodationTypeButtonGroup.add(accommodationTypeButton);

            JPanel individualAccommodationPanel = new JPanel();
            individualAccommodationPanel.setLayout(new BoxLayout(individualAccommodationPanel, BoxLayout.Y_AXIS));
            individualAccommodationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            accommodationTypeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            individualAccommodationPanel.add(accommodationTypeButton);

            JLabel imageLabel = new JLabel();
            ImageIcon icon = new ImageIcon(getScaledImage(getAccommodationImage(accommodation.getName()), 300, 300));
            imageLabel.setIcon(icon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            individualAccommodationPanel.add(imageLabel);

            accommodationTypeButtonPanel.add(individualAccommodationPanel);
            accommodationButtonMap.put(accommodationTypeButton, accommodation);
        }

        ActionListener yesNoButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!studentIDEntered) {
                    JOptionPane.showMessageDialog(AdminAccommodationSystem.this,
                            "Please enter a Student ID first.", "Student ID Required",
                            JOptionPane.ERROR_MESSAGE);
                    buttonGroup.clearSelection();
                } else {
                    if (yesAccommodationButton.isSelected()) {
                        accommodationTypeButtonPanel.setVisible(true);
                    } else {
                        accommodationTypeButtonPanel.setVisible(false);
                    }
                    revalidate();
                    repaint();
                }
            }
        };

        yesAccommodationButton.addActionListener(yesNoButtonListener);
        noAccommodationButton.addActionListener(yesNoButtonListener);

        proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(e -> {
            if (!studentIDEntered) {
                JOptionPane.showMessageDialog(AdminAccommodationSystem.this,
                        "Please enter a Student ID first.", "Student ID Required",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean isAccommodationSelected = yesAccommodationButton.isSelected();
            String selectedAccommodationName = "no accommodation";

            // for (JRadioButton accommodationTypeButton : accommodationButtonMap.keySet()) {
            //     if (accommodationTypeButton.isSelected()) {
            //         boolean isAccommodationTypeSelected = false;
            //         selectedAccommodationName = accommodationButtonMap.get(accommodationTypeButton).getName();
            //         isAccommodationTypeSelected = true;
            //         break;
            //     }
            // }

            if (isAccommodationSelected) {
                boolean isAccommodationTypeSelected = false;
                for (JRadioButton accommodationTypeButton : accommodationButtonMap.keySet()) {
                    if (accommodationTypeButton.isSelected()) {
                        selectedAccommodationName = accommodationButtonMap.get(accommodationTypeButton).getName();
                        isAccommodationTypeSelected = true;
                    }
                }
                if (!isAccommodationTypeSelected) {
                    JOptionPane.showMessageDialog(AdminAccommodationSystem.this,
                    "Please select an accommodation type.", "Accommodation Type Required",
                    JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JOptionPane.showMessageDialog(this, "Accommodation selected: " + selectedAccommodationName, "Success",
                JOptionPane.INFORMATION_MESSAGE);
            } 
            else {
                JOptionPane.showMessageDialog(this, "No accommodation selected.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            
            

            String studentID = studentIDField.getText().trim(); // Use entered student ID
            Billing.updateBillFile(studentID, isAccommodationSelected ? "accommodation" : "no accommodation",
                    selectedAccommodationName);
        });

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            parentFrame.setContentPane(AdminContentPane.getInstance(parentFrame));
            parentFrame.revalidate();
        });

        proceedBackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        proceedBackPanel.add(proceedButton);
        proceedBackPanel.add(backButton);

        accommodationPanel.add(buttonPanel, BorderLayout.NORTH);
        accommodationPanel.add(accommodationTypeButtonPanel, BorderLayout.CENTER);
        accommodationPanel.add(proceedBackPanel, BorderLayout.SOUTH);

        mainPanel.add(accommodationPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private String getAccommodationImage(String accommodationName) {
        switch (accommodationName) {
            case "single room - without air-conditioning":
                return "Single wo Aircond.jpeg";
            case "single room - with air-conditioning":
                return "Single w Aircond.jpeg";
            case "twin-sharing room - without air-conditioning":
                return "Twin Sharing wo Aircond.jpeg";
            case "twin-sharing room - with air-conditioning":
                return "Twin Sharing w Aircond.jpeg";
            default:
                return null;
        }
    }

    private Image getScaledImage(String imagePath, int width, int height) {
        if (imagePath == null)
            return null;
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            int imgWidth = img.getWidth();
            int imgHeight = img.getHeight();
            double scale = Math.min((double) width / imgWidth, (double) height / imgHeight);
            int newWidth = (int) (imgWidth * scale);
            int newHeight = (int) (imgHeight * scale);
            Image scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            return scaledImage;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}