import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FitnessGoalSelection extends JFrame {
    private String selectedGoal;

    public FitnessGoalSelection() {
        setTitle("Select Your Fitness Goal");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Open in full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Your Fitness Goals:", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, BorderLayout.NORTH);

        JPanel goalsPanel = new JPanel();
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        goalsPanel.setBackground(Color.WHITE);
        
        String[] goals = {"Lose Weight", "Gain Weight", "Gain Muscle"};

        // Create buttons for each fitness goal
        for (String goal : goals) {
            JButton goalButton = new JButton(goal);
            goalButton.setFont(new Font("Arial", Font.PLAIN, 24));
            goalButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedGoal = goal; // Store the selected goal
                    openMainMenu(); // Open the main menu
                }
            });
            goalsPanel.add(goalButton);
        }

        // Add a button to change the goal (visible in main menu)
        JButton changeGoalButton = new JButton("Change Fitness Goal");
        changeGoalButton.setFont(new Font("Arial", Font.PLAIN, 24));
        changeGoalButton.addActionListener(e -> openFitnessGoalSelection());
        goalsPanel.add(changeGoalButton);

        add(goalsPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void openMainMenu() {
        MainMenu mainMenu = new MainMenu(selectedGoal); // Pass the selected goal to the main menu
        mainMenu.setVisible(true);
        this.dispose(); // Close the current window
    }

    private void openFitnessGoalSelection() {
        this.setVisible(false); // Hide current window
        new FitnessGoalSelection(); // Open goal selection again
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FitnessGoalSelection::new);
    }
}
