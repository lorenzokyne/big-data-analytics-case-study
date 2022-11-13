import models.ClimateData;
import org.jkarma.mining.joiners.Joiner;
import org.jkarma.mining.providers.Context;

public class MixedClimateJoiner implements Joiner<ClimateData> {

    @Override
    public boolean testPrecondition(ClimateData p1, ClimateData p2, Context ctx, int length) {
        return true;
    }

    @Override
    public ClimateData apply(ClimateData p1, ClimateData p2, int length) {
        ClimateData result = p2;
        if (p1.getPrcp() == null || p2.getPrcp() == null)
            if (p1.getAwnd() == null || p2.getAwnd() == null)
                result = p1;

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
}