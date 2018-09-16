package jplag;

public class Match implements Comparable<Match> {
    public final int startA;
    public final int startB;
    public final int length;

    public Match(int startA, int startB, int length) {
        this.startA = startA;
        this.startB = startB;
        this.length = length;
    }

    public final boolean overlap(Match match) {
        //noinspection unchecked
        for (Pair<Integer, Integer> p : new Pair[]{Pair.of(this.startA, match.startA), Pair.of(this.startB, match.startB)})
            if (p.fst < p.snd) {
                if ((p.snd - p.fst) < this.length)
                    return true;
            } else if ((p.fst - p.snd) < match.length)
                return true;
        return false;
    }

    @Override
    public int compareTo(Match o) {
        return Integer.compare(this.startA, o.startA);
    }
}
