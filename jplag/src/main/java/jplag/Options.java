package jplag;

import jplagUtils.PropertiesLoader;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Options {
    private static String[] languages = {
            "c/c++", "jplag.cpp.Language",
            "java17", "jplag.java17.Language",
            "python3", "jplag.python3.Language",
    };

    public jplag.Language language;

    public List<String> fileList = new ArrayList<>();

    public Options(String[] args, ProgramI program) throws Exception {
        if (args.length == 0) {
            Options.usage();
            return;
        }

        String languageName = null;
        for (int i = 0; i < args.length; i++)
            if (args[i].startsWith("-")) {
                String arg = args[i];
                if (arg.equals("-l") && i + 1 < args.length) {
                    languageName = args[i + 1].toLowerCase();
                    ++i;
                } else if (arg.equals("-c") && i + 2 < args.length)
                    while (i + 1 < args.length) {
                        this.fileList.add(args[i + 1]);
                        ++i;
                    }
                else
                    throw new Exception("Unknown option: " + arg);
            }
        if (languageName == null)
            throw new Exception("No language found...");
        System.gc();

        for (int j = 0; j < Options.languages.length - 1; j += 2)
            if (Options.languages[j].equals(languageName)) {
                Constructor<?> constructor = Class.forName(Options.languages[j + 1]).getDeclaredConstructors()[0];
                this.language = (Language) constructor.newInstance(program);
            }
        if (this.language == null)
            throw new Exception("Illegal value: Language instantiation failed: Unknown language \"" + languageName + "\"");
        System.gc();
    }

    static void usage() {
        System.err.print("Main (Version " + PropertiesLoader.loadProps("jplag/version.properties").getProperty("version", "devel") + ")"
                + ", Copyright (c) 2004-2017 KIT - IPD Tichy, Guido Malpohl, and others.\n"
                + "Usage: Main [ options ] [-c file1 file2 ...]\n"
                + " <root-dir>        The root-directory that contains all submissions.\n\n"
                + "options are:\n"
                + " -l <language>   (Language) Supported Languages:\n"
                + " -c [files]      Compare a list of files. Should be the last one.\n");
        for (int i = 0; i < languages.length - 2; i += 2)
            System.err.print(languages[i] + (i == 0 ? " (default), " : ", "));
        System.err.println(languages[languages.length - 2]);
    }
}
