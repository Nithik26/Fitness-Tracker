import java.io.*;
import java.util.*;

class User implements Serializable {
    private final String username;
    private final String password;
    private String fitnessGoal;
    private List<WeightCheckIn> weightCheckIns = new ArrayList<>();
    private List<ExerciseCheckIn> exerciseCheckIns = new ArrayList<>(); // New list for exercise check-ins
    private double height;        // New field for height in cm
    private double goalWeight;    // New field for goal weight in kg

    public User(String username, String password, String fitnessGoal) {
        this.username = username;
        this.password = password;
        this.fitnessGoal = fitnessGoal;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public void addWeightCheckIn(String date, double weight) {
        weightCheckIns.add(new WeightCheckIn(date, weight));
    }

    // New method to add a weight check-in with exercise details
    public void addDailyCheckIn(String date, double weight, String exerciseType, int duration) {
        weightCheckIns.add(new WeightCheckIn(date, weight, exerciseType, duration));
        exerciseCheckIns.add(new ExerciseCheckIn(date, exerciseType, duration));
    }

    public List<WeightCheckIn> getWeightCheckIns() {
        return weightCheckIns;
    }

    public List<ExerciseCheckIn> getExerciseCheckIns() {
        return exerciseCheckIns;
    }

    public static void saveUsers(Map<String, User> users, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, User> loadUsers(File file) {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // New getter and setter methods for height
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    // New getter and setter methods for goal weight
    public double getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(double goalWeight) {
        this.goalWeight = goalWeight;
    }

    // New method to display daily check-ins (weight + exercise)
    public void displayDailyCheckIns() {
        System.out.println("Daily Check-Ins for " + username + ":");
        for (WeightCheckIn checkIn : weightCheckIns) {
            System.out.printf("Date: %s, Weight: %.2f kg, Exercise: %s, Duration: %d min%n", 
                checkIn.getDate(), checkIn.getWeight(), 
                checkIn.getExerciseType() != null ? checkIn.getExerciseType() : "N/A", 
                checkIn.getDuration());
        }
    }
}

// Class for weight check-ins, including optional exercise details
class WeightCheckIn implements Serializable {
    private final String date;
    private final double weight;
    private String exerciseType; // New field for exercise type
    private int duration;        // New field for exercise duration in minutes

    // Constructor with exercise details
    public WeightCheckIn(String date, double weight, String exerciseType, int duration) {
        this.date = date;
        this.weight = weight;
        this.exerciseType = exerciseType;
        this.duration = duration;
    }

    // Existing constructor for weight-only check-in (backward compatibility)
    public WeightCheckIn(String date, double weight) {
        this.date = date;
        this.weight = weight;
        this.exerciseType = null;  // No exercise information provided
        this.duration = 0;         // Default duration
    }

    public String getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }

    // New getter methods for exercise type and duration
    public String getExerciseType() {
        return exerciseType;
    }

    public int getDuration() {
        return duration;
    }
}

// Separate class for handling ExerciseCheckIn entries
class ExerciseCheckIn implements Serializable {
    private final String date;
    private final String exerciseType;
    private final int duration; // Duration in minutes

    public ExerciseCheckIn(String date, String exerciseType, int duration) {
        this.date = date;
        this.exerciseType = exerciseType;
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public int getDuration() {
        return duration;
    }
}
