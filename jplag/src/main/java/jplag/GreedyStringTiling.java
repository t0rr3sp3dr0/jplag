package jplag;

import java.util.Collections;

public class GreedyStringTiling {
    public static Comparison compare(Submission a, Submission b, int minimumMatchLength) {
        if ((b.struct.table == null && a.struct.table != null) || (a.struct.size() > b.struct.size())) {
            Submission s = a;
            a = b;
            b = s;
        }

        Structure structA = a.struct;
        Structure structB = b.struct;

        Token[] tokensA = structA.tokens;
        Token[] tokensB = structB.tokens;

        int lengthA = structA.size() - 1;
        int lengthB = structB.size() - 1;

        Comparison comparison = new Comparison(a, b);

        if (lengthA < minimumMatchLength || lengthB < minimumMatchLength)
            return comparison;

        for (int i = 0; i <= lengthA; ++i)
            tokensA[i].marked = tokensA[i].type == TokenConstants.FILE_END || tokensA[i].type == TokenConstants.SEPARATOR_TOKEN;
        for (int i = 0; i <= lengthB; ++i)
            tokensB[i].marked = tokensB[i].type == TokenConstants.FILE_END || tokensB[i].type == TokenConstants.SEPARATOR_TOKEN;

        if (structA.hash_length != minimumMatchLength)
            initHashTable(structA, minimumMatchLength, false);
        if (structB.hash_length != minimumMatchLength || structB.table == null)
            initHashTable(structB, minimumMatchLength, true);

        int maxMatch;
        do {
            maxMatch = minimumMatchLength;
            Matches matches = new Matches();

            int[] elemsB;
            for (int x = 0; x <= lengthA - maxMatch; ++x) {
                if (tokensA[x].marked || tokensA[x].hash == -1 || (elemsB = structB.table.get(tokensA[x].hash)) == null)
                    continue;

                inner:
                for (int i = 1; i <= elemsB[0]; ++i) {
                    int y = elemsB[i];
                    if (tokensB[y].marked || maxMatch > lengthB - y)
                        continue;

                    int hx, hy;
                    for (int j = maxMatch - 1; j >= 0; --j)
                        if (tokensA[hx = x + j].type != tokensB[hy = y + j].type || tokensA[hx].marked || tokensB[hy].marked)
                            continue inner;

                    int j = maxMatch;
                    while (tokensA[hx = x + j].type == tokensB[hy = y + j].type && !tokensA[hx].marked && !tokensB[hy].marked)
                        j++;

                    if (j > maxMatch) {
                        matches.clear();
                        maxMatch = j;
                    }

                    matches.add(new Match(x, y, j));
                }
            }

            for (Match match : matches) {
                comparison.add(match);

                int x = match.startA;
                int y = match.startB;
                for (int i = 0; i < match.length; ++i)
                    tokensA[x++].marked = tokensB[y++].marked = true;
            }
        } while (maxMatch != minimumMatchLength);

        Collections.reverse(comparison);
        return comparison;
    }

    private static void initHashTable(Structure s, int hashLength, boolean makeTable) {
        if (hashLength < 1)
            hashLength = 1;
        hashLength = (hashLength < 26 ? hashLength : 25);

        if (s.size() < hashLength)
            return;

        int modulo = ((1 << 6) - 1);

        int loops = s.size() - hashLength;
        s.table = makeTable ? new Table(3 * loops) : null;

        int hash = 0;
        int hashedLength = 0;
        for (int i = 0; i < hashLength; ++i) {
            hash = (2 * hash) + (s.tokens[i].type & modulo);
            if (s.tokens[i].marked)
                hashedLength = 0;
            else
                hashedLength++;
        }

        int factor = hashLength != 1 ? 2 << (hashLength - 2) : 1;
        for (int i = 0; i < loops; ++i) {
            if (hashedLength >= hashLength) {
                s.tokens[i].hash = hash;
                if (makeTable)
                    s.table.add(hash, i);
            } else
                s.tokens[i].hash = -1;
            hash -= factor * (s.tokens[i].type & modulo);
            hash = (2 * hash) + (s.tokens[i + hashLength].type & modulo);
            if (s.tokens[i + hashLength].marked)
                hashedLength = 0;
            else
                hashedLength++;
        }
        s.hash_length = hashLength;
    }
}
