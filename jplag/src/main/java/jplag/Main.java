package jplag;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Options.usage();
            return;
        }

        new JPlag(args).run();
    }
}
