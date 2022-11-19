import models.ClimateData;
import org.jkarma.mining.joiners.Joiner;
import org.jkarma.mining.providers.Context;

public class MixedClimateJoiner implements Joiner<ClimateData> {

    @Override
    public boolean testPrecondition(ClimateData p1, ClimateData p2, Context ctx, int length) {
        return true;
    }

    @Override
    public ClimateData apply(ClimateData c1, ClimateData c2, int length) {
        ClimateData result = c2;
        if (c1.isSamePeriod() && c2.isSamePeriod()) {
            if (length > 1) {
                result.setSamePeriod(this.areSamePeriod(c1, c2));
            } else {
                result.setSamePeriod(true);
            }
        } else {
            result.setSamePeriod(this.areSamePeriod(c1, c2));
        }
        if (c1.getPrcp() == null || c2.getPrcp() == null)
            result = c1;

        return result;
    }

    @Override
    public boolean testPostcondition(ClimateData p, Context ctx, int length) {
        boolean result = true;
        if (length > 1) {
            result = p.isSamePeriod();
        }
        return result;
    }

    //same period is intended as same month of the year
    private boolean areSamePeriod(ClimateData c1, ClimateData c2) {
        return c1.getDate().equals(c2.getDate());
    }
}