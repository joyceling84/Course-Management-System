import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminContentPane extends JPanel {
   
private static AdminContentPane instance;
private JFrame parentFrame;

    private AdminContentPane(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Image
        ImageIcon imageIcon = new ImageIcon("APPL_MMU_IMG_1.png");
        JLabel imageLabel = new JLabel(imageIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(imageLabel, gbc);

        // Enroll new student button
        JButton enrollButton = new JButton("Enroll New Student");
        enrollButton.setPreferredSize(new Dimension(200, 40)); // Fixed size
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnrollment();
            }
        });
        add(enrollButton, gbc);

        // Show student enrollment button
        JButton showEnrollmentButton = new JButton("Show Student Enrollment");
        showEnrollmentButton.setPreferredSize(new Dimension(200, 40)); // Fixed size
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        showEnrollmentButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleShowEnrollment();
            }
        });
        add(showEnrollmentButton, gbc);

        // Accommodation registration button
        JButton accommodationButton = new JButton("Accommodation Registration");
        accommodationButton.setPreferredSize(new Dimension(200, 40)); // Fixed size
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        accommodationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAccommodation();
            }
        });
        add(accommodationButton, gbc);

        // View bills button
        JButton viewBillsButton = new JButton("View Bills");
        viewBillsButton.setPreferredSize(new Dimension(200, 40)); // Fixed size
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(viewBillsButton, gbc);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(200, 40)); // Fixed size
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        add(logoutButton, gbc);
    }

   

    private void handleLogout() {
        // Return to login pane
        AdminLoginPane loginPane = new AdminLoginPane(parentFrame);
        parentFrame.setContentPane(loginPane);
        parentFrame.revalidate();
    }

    private void handleEnrollment(){
        AdminEnrollmentSystem enrollmentPane = new AdminEnrollmentSystem(parentFrame);

        JScrollPane scrollPane = new JScrollPane(enrollmentPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        parentFrame.setContentPane(scrollPane);
        parentFrame.revalidate();
    }

    private void handleAccommodation(){
        AdminAccommodationSystem accommodationPane = new AdminAccommodationSystem(parentFrame);
        JScrollPane scrollPane = new JScrollPane(accommodationPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        parentFrame.setContentPane(scrollPane);
        parentFrame.revalidate();
    }

    private void handleShowEnrollment(){
        AdminViewStudentEnrollmentSystem viewEnrollmentPane = new AdminViewStudentEnrollmentSystem(parentFrame);
        JScrollPane scrollPane = new JScrollPane(viewEnrollmentPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        parentFrame.setContentPane(scrollPane);
        parentFrame.revalidate();
    }

    public static AdminContentPane getInstance(JFrame parentFrame) {
        if (instance == null) {
            instance = new AdminContentPane(parentFrame);
        }
        return instance;
    }
}
