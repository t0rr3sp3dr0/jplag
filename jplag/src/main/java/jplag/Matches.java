package jplag;

import java.util.ArrayList;

public class Matches extends ArrayList<Match> {
    @Override
    public final boolean add(Match match) {
        for (Match m : this)
            if (m.overlap(match))
                return false;

        return super.add(match);
    }
}
