import org.jkarma.mining.joiners.Joiner;
import org.jkarma.mining.providers.Context;

import models.ClimateData;


public class MixedClimateJoiner implements Joiner<ClimateData> {

    @Override
    public boolean testPrecondition(ClimateData p1, ClimateData p2, Context ctx, int length) {
        return true;
    }

    @Override
    public ClimateData apply(ClimateData c1, ClimateData c2, int length) {
        ClimateData result = new ClimateData(c2);
        if (c1.samePeriod && c2.samePeriod) {
            if (length > 1) {
                result.samePeriod = this.areSamePeriod(c1, c2);
            } else {
                result.samePeriod = true;
            }
        } else {
            result.samePeriod = this.areSamePeriod(c1, c2);
        }
        return result;
    }

    @Override
    public boolean testPostcondition(ClimateData p, Context ctx, int length) {
        boolean result = true;
        if (length > 1) {
            result = p.getPrcp() != null;
        }
        return result;
    }

    //same period is intended as same month of the year
    private boolean areSamePeriod(ClimateData c1, ClimateData c2) {
        return c1.getPeriod().equals(c2.getPeriod());
    }
}