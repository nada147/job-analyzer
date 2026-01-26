package ml.matching;

public class MatchResult {

    private int jobId;
    private String jobTitle;
    private double score; // 0–1

    public MatchResult(int jobId, String jobTitle, double score) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.score = score;
    }

    public int getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public double getScore() {
        return score;
    }
}