package jplag;

public class Comparison extends Matches implements Comparable<Comparison> {
    public final Submission a;
    public final Submission b;

    public Comparison(Submission a, Submission b) {
        super();

        this.a = a;
        this.b = b;
    }

    public int tokensMatched() {
        return this.stream().mapToInt(e -> e.length).sum();
    }

    public double similarity() {
        return (2 * (float) tokensMatched()) / ((a.struct.size() - a.files.length) + (b.struct.size() - b.files.length));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Comparison) && (Double.compare(((Comparison) obj).similarity(), this.similarity()) == 0);
    }

    @Override
    public String toString() {
        return a.name + " <-> " + b.name;
    }

    @Override
    public int compareTo(Comparison o) {
        return Double.compare(o.similarity(), this.similarity());
    }
}
