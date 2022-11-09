import models.Product;
import org.jkarma.mining.joiners.Joiner;
import org.jkarma.mining.providers.Context;

public class MixedProductJoiner implements Joiner<Product> {

    @Override
    public boolean testPrecondition(Product p1, Product p2, Context ctx, int length) {
        return true;
    }

    @Override
    public Product apply(Product p1, Product p2, int length) {
        Product result = new Product(p2.getName());
        if (p1.alreadyMixed && p2.alreadyMixed) {
            if (length > 1) {
                result.alreadyMixed = this.areMixed(p1, p2);
            } else {
                result.alreadyMixed = true;
            }
        } else {
            result.alreadyMixed = this.areMixed(p1, p2);
        }

        return result;
    }

    @Override
    public boolean testPostcondition(Product p, Context ctx, int length) {
        boolean result = true;
        if (length > 1) {
            result = p.alreadyMixed;
        }
        return result;
    }

    private boolean areMixed(Product p1, Product p2) {
        return p1.isDrink() && p2.isFood() || p1.isFood() && p2.isDrink();
    }
}