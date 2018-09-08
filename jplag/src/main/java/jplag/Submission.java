package jplag;

public class Submission implements Comparable<Submission> {
    public final String name;

    public final Language language;

    public final String[] files; // = new String[0];

    public final Structure struct;

    public final boolean valid;

    public Submission(String name, Language language, String[] files) {
        this.language = language;
        this.name = name;
        this.files = files;
        this.struct = this.language.parse(null, files);
        this.valid = !this.language.errors() && this.struct.size() >= 3;
    }

    @Override
    public int compareTo(Submission other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
