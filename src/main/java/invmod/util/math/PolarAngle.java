package invmod.util.math;

public interface PolarAngle {
    int getAngle();

    static PolarAngle of(int angle) {
        return new Fixed(angle);
    }

    public record Fixed(int angle) implements PolarAngle {
        @Override
        public int getAngle() {
            return this.angle;
        }
    }

}