package odms.controller;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.HLAType;

@Slf4j
public class HlaController {
    /**
     * Returns the best match of the given antigens of the given gene
     * @param gene the gene to be matched
     * @param hlaA the first antigen
     * @param hlaB the second antigen
     * @return number of matching antigens (0-2)
     */
    private static Integer calcMatch(String gene, HLAType hlaA, HLAType hlaB) {
        Integer xa = hlaA.getGroupX().get(gene);
        Integer xb = hlaA.getGroupY().get(gene);
        Integer ya = hlaB.getGroupX().get(gene);
        Integer yb = hlaB.getGroupY().get(gene);

        // try matching same groups
        Integer numMatchingSame = 0;
        if (xa == xb) {
            numMatchingSame++;
        }
        if (ya == yb) {
            numMatchingSame++;
        }

        // try matching cross groups
        Integer numMatchingCross = 0;
        if (xa == ya) {
            numMatchingCross++;
        }
        if (ya == xb) {
            numMatchingCross++;
        }

        return Math.max(numMatchingSame, numMatchingCross);
    }

    /**
     * Returns with a score of match fit as a percentage.
     *
     * @param hlaA first HLA to compare
     * @param hlaB second HLA to compare
     * @return match fit as a percentage
     */
    public static Integer matchScore(HLAType hlaA, HLAType hlaB) {
        final float MATCH_MULTIPLIER = 100f / 12f;
        float score = 0;
        int numMatchingAnitgens = 0;

        for (String gene : HLAType.getPrimaryGeneList()) {
            numMatchingAnitgens += calcMatch(gene, hlaA, hlaB);
        }

        score = numMatchingAnitgens * MATCH_MULTIPLIER;
        return (int) score;
    }

    public static void main (String[] args) {
        HLAType donor = new HLAType(41, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40);
        HLAType receiver = new HLAType(40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40);
        System.out.println("match score " + matchScore(donor, receiver));
    }
}
