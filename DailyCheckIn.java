
public class DailyCheckIn {
    private String date;
    private double weight;
    private String exerciseType;
    private int duration;

    // Constructor
    public DailyCheckIn(String date, double weight, String exerciseType, int duration) {
        this.date = date;
        this.weight = weight;
        this.exerciseType = exerciseType;
        this.duration = duration;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Weight: " + weight + " kg, Exercise: " + exerciseType + ", Duration: " + duration + " min";
    }
}
