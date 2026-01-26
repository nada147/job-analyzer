package ml.matching;

import java.util.List;

public class RunMatching {

    public static void main(String[] args) {

        List<String> cvSkills = List.of(
                "java", "spring", "sql", "docker"
        );

        // Domaine ML (peut être null)
        String domainML = "Développement Backend";
        // String domainML = null;

        MatchingService service = new MatchingService();
        List<MatchResult> results =
                service.matchCVWithJobs(cvSkills, domainML);

        if (results.isEmpty()) {
            System.out.println("No matching jobs found.");
            return;
        }

        System.out.println("===== TOP JOB RECOMMENDATIONS =====");

        int i = 1;
        for (MatchResult r : results) {
            System.out.println(
                i + ". " + r.getJobTitle() +
                " → " + String.format("%.0f", r.getScore() * 100) + "%"
            );
            if (++i > 10) break;
        }
    }
}