
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class FitnessTrackerApp extends JFrame {
private static final File USER_FILE = new File("users.dat");
private Map<String, User> users;
private String currentUsername;

private JTextField usernameField;
private JLabel usernameStatusLabel;
private JPasswordField passwordInputField;
private JLabel passwordStatusLabel;
private JCheckBox showPasswordCheckbox;

public FitnessTrackerApp() {
    users = User.loadUsers(USER_FILE);
    setupLoginPanel();
}

private void setupLoginPanel() {
    setTitle("Fitness Tracker - Login/Signup");
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 0; gbc.gridy = 0;

    JLabel usernameLabel = new JLabel("Username:");
    add(usernameLabel, gbc);

    gbc.gridx = 1;
    usernameField = new JTextField(20);
    add(usernameField, gbc);

    usernameStatusLabel = new JLabel();
    gbc.gridx = 2;
    add(usernameStatusLabel, gbc);

    usernameField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            checkUsernameAvailability();
        }
    });

    gbc.gridx = 0; gbc.gridy = 1;
    JLabel passwordLabel = new JLabel("Password:");
    add(passwordLabel, gbc);

    gbc.gridx = 1;
    passwordInputField = new JPasswordField(20);
    add(passwordInputField, gbc);

    passwordStatusLabel = new JLabel();
    gbc.gridx = 2;
    add(passwordStatusLabel, gbc);

    showPasswordCheckbox = new JCheckBox("Show Password");
    showPasswordCheckbox.addActionListener(e -> {
        if (showPasswordCheckbox.isSelected()) {
            passwordInputField.setEchoChar((char) 0);
        } else {
            passwordInputField.setEchoChar('•');
        }
    });
    gbc.gridx = 0; gbc.gridy = 2;
    add(showPasswordCheckbox, gbc);

    JButton loginButton = new JButton("Login");
    gbc.gridx = 0; gbc.gridy = 3;
    add(loginButton, gbc);

    JButton signupButton = new JButton("Signup");
    gbc.gridx = 1;
    add(signupButton, gbc);

    loginButton.addActionListener(e -> login());
    signupButton.addActionListener(e -> signup());

    passwordInputField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent evt) {
            checkPasswordCriteria();
        }
    });

    pack();
    setVisible(true);
}

private void checkUsernameAvailability() {
    String username = usernameField.getText().trim();
    if (!username.isEmpty() && users.containsKey(username)) {
        usernameStatusLabel.setForeground(Color.RED);
        usernameStatusLabel.setText("✖ Username taken");
    } else if (!username.isEmpty()) {
        usernameStatusLabel.setForeground(Color.GREEN);
        usernameStatusLabel.setText("✔ Username available");
    } else {
        usernameStatusLabel.setText("");
    }
}

private void checkPasswordCriteria() {
    String password = new String(passwordInputField.getPassword());
    boolean hasUppercase = password.matches(".*[A-Z].*");
    boolean hasLowercase = password.matches(".*[a-z].*");
    boolean hasDigit = password.matches(".*[0-9].*");
    boolean hasSpecialChar = password.matches(".*[@#$%^&+=!].*");
    boolean isValidLength = password.length() >= 8;

    StringBuilder criteriaStatus = new StringBuilder("<html>");
    criteriaStatus.append("Criteria:<br>");
    criteriaStatus.append("8 characters: ").append(isValidLength ? "✔" : "✖").append("<br>");
    criteriaStatus.append("1 uppercase: ").append(hasUppercase ? "✔" : "✖").append("<br>");
    criteriaStatus.append("1 lowercase: ").append(hasLowercase ? "✔" : "✖").append("<br>");
    criteriaStatus.append("1 digit: ").append(hasDigit ? "✔" : "✖").append("<br>");
    criteriaStatus.append("1 special char: ").append(hasSpecialChar ? "✔" : "✖").append("<br>");
    criteriaStatus.append("</html>");

    passwordStatusLabel.setText(criteriaStatus.toString());
    passwordStatusLabel.setForeground(isValidPassword(password) ? Color.GREEN : Color.RED);
}

private boolean isValidPassword(String password) {
    return password.length() >= 8
            && password.matches(".*[A-Z].*")
            && password.matches(".*[a-z].*")
            && password.matches(".*[0-9].*")
            && password.matches(".*[@#$%^&+=!].*");
}

private void login() {
    String username = usernameField.getText().trim();
    String password = new String(passwordInputField.getPassword());

    if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
        currentUsername = username;
        JOptionPane.showMessageDialog(this, "Login Successful!");
        setupFitnessGoalSelection(username);
    } else {
        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void signup() {
    String username = usernameField.getText().trim();
    String password = new String(passwordInputField.getPassword());

    if (username.isEmpty()) {
        usernameStatusLabel.setForeground(Color.RED);
        usernameStatusLabel.setText("✖ Username cannot be empty.");
        return;
    }

    if (users.containsKey(username)) {
        usernameStatusLabel.setForeground(Color.RED);
        usernameStatusLabel.setText("✖ Username already exists.");
        return;
    }

    if (!isValidPassword(password)) {
        usernameStatusLabel.setForeground(Color.RED);
        usernameStatusLabel.setText("Password does not meet criteria.");
        return;
    }

    users.put(username, new User(username, password, null));
    User.saveUsers(users, USER_FILE);
    JOptionPane.showMessageDialog(this, "Signup Successful! You can now login.");

    usernameField.setText("");
    passwordInputField.setText("");
    usernameStatusLabel.setText("");
    passwordStatusLabel.setText("");
}
private void setupFitnessGoalSelection(String username) {
    User user = users.get(username);
    if (user.getFitnessGoal() != null) {
        setupOptionsMenu();
        return;
    }

    // Clear previous components
    getContentPane().removeAll();
    setLayout(new GridBagLayout());
    revalidate();
    repaint();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 0; gbc.gridy = 0;

    // Step 1: Select Fitness Goal
    JLabel fitnessGoalsLabel = new JLabel("Your Fitness Goals:");
    fitnessGoalsLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(fitnessGoalsLabel, gbc);

    String[] goals = {"Lose Weight", "Gain Weight", "Gain Muscle"};
    JComboBox<String> goalComboBox = new JComboBox<>(goals);
    gbc.gridx = 1;
    add(goalComboBox, gbc);

    // Step 2: Enter Height, Current Weight, and Goal Weight
    gbc.gridx = 0; gbc.gridy = 1;
    JLabel heightLabel = new JLabel("Enter your height (cm):");
    add(heightLabel, gbc);

    gbc.gridx = 1;
    JTextField heightField = new JTextField(10);
    add(heightField, gbc);

    gbc.gridx = 0; gbc.gridy = 2;
    JLabel currentWeightLabel = new JLabel("Enter your current weight (kg):");
    add(currentWeightLabel, gbc);

    gbc.gridx = 1;
    JTextField currentWeightField = new JTextField(10);
    add(currentWeightField, gbc);

    gbc.gridx = 0; gbc.gridy = 3;
    JLabel goalWeightLabel = new JLabel("Enter your goal weight (kg):");
    add(goalWeightLabel, gbc);

    gbc.gridx = 1;
    JTextField goalWeightField = new JTextField(10);
    add(goalWeightField, gbc);

    // Step 3: Select Goal Button
    gbc.gridx = 0; gbc.gridy = 4;
    gbc.gridwidth = 2;
    JButton selectGoalButton = new JButton("Save and Continue");
    add(selectGoalButton, gbc);

    selectGoalButton.addActionListener(e -> {
        String selectedGoal = (String) goalComboBox.getSelectedItem();
        String heightText = heightField.getText().trim();
        String currentWeightText = currentWeightField.getText().trim();
        String goalWeightText = goalWeightField.getText().trim();

        // Input validation
        try {
            double height = Double.parseDouble(heightText);
            double currentWeight = Double.parseDouble(currentWeightText);
            double goalWeight = Double.parseDouble(goalWeightText);

            // Validate height
            if (height < 40 || height > 250) {
                JOptionPane.showMessageDialog(this, "Height must be between 40 cm and 250 cm.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate weights
            if (currentWeight < 1 || currentWeight > 450) {
                JOptionPane.showMessageDialog(this, "Current weight must be between 1 kg and 450 kg.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (goalWeight < 1 || goalWeight > 450) {
                JOptionPane.showMessageDialog(this, "Goal weight must be between 1 kg and 450 kg.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate weight relationships based on selected goal
            if ("Lose Weight".equals(selectedGoal) && currentWeight <= goalWeight) {
                JOptionPane.showMessageDialog(this, "For losing weight, current weight must be greater than goal weight.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (("Gain Weight".equals(selectedGoal) || "Gain Muscle".equals(selectedGoal)) && currentWeight >= goalWeight) {
                JOptionPane.showMessageDialog(this, "For gaining weight or muscle, current weight must be less than goal weight.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Set the user's details
            user.setFitnessGoal(selectedGoal);
            user.addWeightCheckIn("Start", currentWeight); // Initial weight check-in
            user.setHeight(height); // Assuming you add a setHeight method in User

            // Save users data
            User.saveUsers(users, USER_FILE);

            // Go to the main fitness tracking screen
            setupOptionsMenu();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for height and weights.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    pack();
    setVisible(true);
}

private void setupDailyCheckIn() {
    // Create a new JFrame for daily check-in
    JFrame dailyCheckInFrame = new JFrame("Daily Check-In");
    dailyCheckInFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    dailyCheckInFrame.setLayout(new GridBagLayout());
    dailyCheckInFrame.setSize(400, 400);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 0; gbc.gridy = 0;

    // Date label and text field
    JLabel dateLabel = new JLabel("Enter Date (YYYY-MM-DD):");
    dailyCheckInFrame.add(dateLabel, gbc);

    gbc.gridx = 1;
    JTextField dateField = new JTextField(10);
    dailyCheckInFrame.add(dateField, gbc);

    // Weight label and text field
    gbc.gridx = 0; gbc.gridy = 1;
    JLabel weightLabel = new JLabel("Enter Weight (kg):");
    dailyCheckInFrame.add(weightLabel, gbc);

    gbc.gridx = 1;
    JTextField weightField = new JTextField(10);
    dailyCheckInFrame.add(weightField, gbc);

    // Exercise label and text field
    gbc.gridx = 0; gbc.gridy = 2;
    JLabel exerciseLabel = new JLabel("Enter Exercise Type:");
    dailyCheckInFrame.add(exerciseLabel, gbc);

    gbc.gridx = 1;
    JTextField exerciseField = new JTextField(10);
    dailyCheckInFrame.add(exerciseField, gbc);

    // Duration label and text field
    gbc.gridx = 0; gbc.gridy = 3;
    JLabel durationLabel = new JLabel("Enter Duration (minutes):");
    dailyCheckInFrame.add(durationLabel, gbc);

    gbc.gridx = 1;
    JTextField durationField = new JTextField(10);
    dailyCheckInFrame.add(durationField, gbc);

    // Save Button
    gbc.gridx = 0; gbc.gridy = 4;
    JButton saveButton = new JButton("Save Check-In");
    dailyCheckInFrame.add(saveButton, gbc);

    saveButton.addActionListener(e -> {
        String dateText = dateField.getText().trim();
        String weightText = weightField.getText().trim();
        String exerciseText = exerciseField.getText().trim();
        String durationText = durationField.getText().trim();

        // Input validation
        try {
            double weight = Double.parseDouble(weightText);
            if (weight < 1 || weight > 450) {
                JOptionPane.showMessageDialog(dailyCheckInFrame, "Weight must be between 1 kg and 450 kg.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int duration = Integer.parseInt(durationText);
            if (duration < 1 || duration > 1440) {
                JOptionPane.showMessageDialog(dailyCheckInFrame, "Duration must be between 1 and 1440 minutes.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dailyCheckInFrame, "Please enter valid numerical values for weight and duration.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save the check-in data
        user.addDailyCheckIn(dateText, Double.parseDouble(weightText), exerciseText, Integer.parseInt(durationText));
        JOptionPane.showMessageDialog(dailyCheckInFrame, "Daily check-in saved!");

        // Clear fields
        dateField.setText("");
        weightField.setText("");
        exerciseField.setText("");
        durationField.setText("");
    });

    // See Progress Button
    gbc.gridx = 1;
    JButton seeProgressButton = new JButton("See Progress");
    dailyCheckInFrame.add(seeProgressButton, gbc);

    seeProgressButton.addActionListener(e -> showDailyProgress());

    dailyCheckInFrame.setVisible(true);
}

private void showDailyProgress() {
    // Create a new JFrame to display the progress history
    JFrame progressFrame = new JFrame("Daily Progress");
    progressFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    progressFrame.setLayout(new BorderLayout());

    String[] columnNames = {"Date", "Weight (kg)", "Exercise", "Duration (min)"};
    Object[][] data = user.getDailyCheckIns(); // Assuming this method returns a 2D Object array of check-in data

    JTable progressTable = new JTable(data, columnNames);
    JScrollPane scrollPane = new JScrollPane(progressTable);
    progressFrame.add(scrollPane, BorderLayout.CENTER);

    progressFrame.setSize(500, 300);
    progressFrame.setVisible(true);
}

private void setupOptionsMenu() {
    // Clear previous components
    getContentPane().removeAll();
    setLayout(new GridBagLayout());
    revalidate();
    repaint();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.gridx = 0; gbc.gridy = 0;

    JLabel welcomeLabel = new JLabel("Welcome, " + currentUsername + "!");
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(welcomeLabel, gbc);

    gbc.gridy++;
    JButton weightCheckInButton = new JButton("Daily Check-In");
    add(weightCheckInButton, gbc);

    gbc.gridy++;
    JButton logoutButton = new JButton("Logout");
    add(logoutButton, gbc);

    weightCheckInButton.addActionListener(e -> setupWeightCheckIn());
    logoutButton.addActionListener(e -> logout());

    pack();
    setVisible(true);
}

private void logout() {
    currentUsername = null;
    users.clear();
    setupLoginPanel();
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(FitnessTrackerApp::new);
}
}


