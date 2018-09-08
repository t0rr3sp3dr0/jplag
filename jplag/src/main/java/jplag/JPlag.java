package jplag;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class JPlag implements ProgramI, Runnable {
    private Options options;
    private List<Submission> submissions;

    public JPlag(String[] args) throws Exception {
        this.options = new Options(args, this);
    }

    private void compare() {
        int size = this.submissions.size();

        PriorityQueue<Comparison> comparisons = new PriorityQueue<>();
        for (int i = 0; i < size - 1; ++i) {
            Submission s1 = this.submissions.get(i);
            if (s1.struct == null)
                continue;

            for (int j = i + 1; j < size; ++j) {
                Submission s2 = this.submissions.get(j);
                if (s2.struct == null)
                    continue;

                Comparison comparison = GreedyStringTiling.compare(s1, s2, this.options.language.min_token_match());
                if (comparison.similarity() > 0)
                    comparisons.add(comparison);
            }
        }

        System.out.println("[");
        while (!comparisons.isEmpty()) {
            Comparison comparison = comparisons.poll();

            Token[] a = comparison.a.struct.tokens;
            Token[] b = comparison.b.struct.tokens;

            System.out.printf("\t{\n\t\t\"a\": \"%s\",\n\t\t\"b\": \"%s\",\n\t\t\"similarity\": %f,\n\t\t\"matches\": [\n", comparison.a.name, comparison.b.name, comparison.similarity());
            for (Match match : comparison) {
                if (match.length == 0 || match.startA < 0 || match.startB < 0)
                    continue;

                Token startA = a[match.startA];
                Token endA = a[match.startA + match.length - 1];
                Token startB = b[match.startB];
                Token endB = b[match.startB + match.length - 1];

                System.out.printf("\t\t\t{\n\t\t\t\t\"a\": [%d, %d],\n\t\t\t\t\"b\": [%d, %d],\n\t\t\t},\n", startA.getLine(), endA.getLine(), startB.getLine(), endB.getLine());
            }
            System.out.print("\t\t],\n\t},\n");
        }
        System.out.println("]");
    }

    @Override
    public void run() {
        this.submissions = new ArrayList<>();
        for (String file : options.fileList)
            this.submissions.add(new Submission(file, this.options.language, new String[]{file}));
        System.gc();

        this.submissions.removeIf(s -> !s.valid || s.struct == null || s.struct.size() < this.options.language.min_token_match());
        System.gc();

        int valid = this.submissions.size();
        if (valid < 2)
            throw new RuntimeException("Not enough valid submissions! (only " + valid + " " + (valid != 1 ? "are" : "is") + " valid):\n");
        System.gc();

        this.compare();
        System.gc();
    }

    @Override
    public void addError(String errorMsg) {
    }

    @Override
    public void print(String normalMsg, String longMsg) {
    }
}
