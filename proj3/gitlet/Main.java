package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Marcus Cheung
 */
public class Main {


    /** CWD. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** GITLET. */
    static final File GITLET = Utils.join(CWD, ".gitlet");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String type = args[0];
        Repo r = new Repo();
        if (type.equals("init")) {
            r.init();
        } else if (!GITLET.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        } else if (type.equals("add")) {
            r.add(args[1]);
        } else if (type.equals("commit")) {
            r.commit(args[1]);

        } else if (type.equals("rm")) {
            r.remove(args[1]);
        } else if (type.equals("log")) {
            r.log();
        } else if (type.equals("checkout")) {
            if (args.length == 3 && args[1].equals("--")) {
                r.checkout(args[2]);
            } else if (args.length == 4 && args[2].equals("--")) {
                r.checkout(args[1], args[3]);
            } else if (args.length == 2) {
                r.bcheckout(args[1]);
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else if (type.equals("global-log")) {
            r.globalLog();
        } else if (type.equals("find")) {
            r.find(args[1]);
        } else if (type.equals("status")) {
            r.status();
        } else if (type.equals("branch")) {
            r.branch(args[1]);
        } else if (type.equals("rm-branch")) {
            r.rmBranch(args[1]);
        } else if (type.equals("reset")) {
            r.reset(args[1]);
        } else if (type.equals("merge")) {
            r.merge(args[1]);
        } else {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }

    }

}
