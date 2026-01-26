package ml.matching;

import java.util.List;

public class SkillMatcher {

    public static double jaccard(List<String> jobTokens,
                                 List<String> cvSkills) {

        if (jobTokens == null || cvSkills == null) return 0.0;

        int intersection = 0;

        for (String cv : cvSkills) {
            for (String job : jobTokens) {
                if (job.contains(cv)) { // 🔥 MATCH INTELLIGENT
                    intersection++;
                    break;
                }
            }
        }

        int union = jobTokens.size() + cvSkills.size() - intersection;

        return union == 0 ? 0.0 : (double) intersection / union;
    }
}