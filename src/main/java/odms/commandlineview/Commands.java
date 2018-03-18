package odms.commandlineview;

import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

public enum Commands {
    INVALID,

    // General Commands
    HELP,
    PRINTALL,
    PRINTDONORS,

    // IO Commands
    EXPORT,
    IMPORT,

    // Profile Commands
    PROFILECREATE,
    PROFILEDELETE,
    PROFILEVIEW,

    // Donor Commands
    DONORDATECREATED,
    DONORDONATIONS,
    DONORUPDATE,

    // Orgon Commands
    ORGANADD,
    ORGANREMOVE,
    ORGANDONATE;

    public static ArgumentCompleter commandAutoCompletion() {
        return new ArgumentCompleter(
            new StringsCompleter("help"),
            new StringsCompleter("print-all"),
            new StringsCompleter("print-donors"),

            new StringsCompleter("export"),
            new StringsCompleter("import"),

            new StringsCompleter("create-profile"),
            new StringsCompleter("delete-profile")
        );
    }
}
