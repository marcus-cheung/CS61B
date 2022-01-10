package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/** Commit class for Gitlet.
 *  @author Marcus Cheung
 */
public class Commit implements Serializable {
    /** msg. */
    private String _msg;
    /** date. */
    private Date _time;
    /** files. */
    private LinkedHashMap<String, String> _files;
    /** id. */
    private String _id;
    /** p1. */
    private String _parent1;
    /** p2. */
    private String _parent2 = null;

    /** CWD. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** gitlet. */
    static final File GITLET = Utils.join(CWD, ".gitlet");
    /** commits. */
    static final File COMMITS = Utils.join(GITLET, "commits");
    /** add. */
    static final File ADD = Utils.join(GITLET, "add");
    /** rm. */
    static final File RM = Utils.join(GITLET, "rm");
    /** formatter. */
    static final SimpleDateFormat F =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");


    /** Commit constructor.
     * @param msg msg
     * @param p1 p1
     * */
    @SuppressWarnings("unchecked")
    public Commit(String msg, String p1) {
        Commit parent = null;
        if (p1 != null) {
            parent = Utils.readObject(Utils.join(COMMITS, p1), Commit.class);
        }
        _msg  = msg;
        _parent1 = p1;
        if (parent == null) {
            _time = new Date(0);
            _files = new LinkedHashMap<>();
        } else {
            _time = new Date();
            _files = (LinkedHashMap<String, String>) parent._files.clone();
            LinkedHashMap<String, String> add =
                    Utils.readObject(ADD, LinkedHashMap.class);
            ArrayList<String> rm = Utils.readObject(RM, ArrayList.class);
            if (add.isEmpty() && rm.isEmpty()) {
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
            _files.putAll(add);
            Utils.writeObject(ADD, new LinkedHashMap<String, String>());

            for (String name : rm)  {
                _files.remove(name);
            }
            Utils.writeObject(RM, new ArrayList<String>());

        }
        _id = Utils.sha1(_files.toString(),
                Utils.serialize(_parent1), _msg, _time.toString());
    }
    /** Commit constructor.
     * @param msg msg
     * @param p1 p1
     * @param p2 p2
     * */
    public Commit(String msg, String p1, String p2) {
        this(msg, p1);
        _parent2 = p2;
    }

    /** gets files.
     * @return files
     * */
    public LinkedHashMap<String, String> getFiles() {
        return _files;
    }

    /** gets id.
     * @return id
     * */
    public String getID() {
        return _id;
    }

    /** checks for parent.
     * @return bool
     * */
    public boolean hasParent() {
        return _parent1 != null;
    }

    /** checks for second parent.
     * @return bool
     * */
    public boolean hasSecond() {
        return _parent2 != null;
    }

    /** gets p1 id.
     * @return id
     * */
    public String getParentID() {
        return _parent1;
    }
    /** gets p2 id.
     * @return id
     * */
    public String getSecondID() {
        return _parent2;
    }
    /** gets p1.
     * @return commit
     * */
    public Commit getParent() {
        return Utils.readObject(Utils.join(COMMITS, _parent1), Commit.class);
    }
    /** gets p2.
     * @return commit
     * */
    public Commit getSecond() {
        return Utils.readObject(Utils.join(COMMITS, _parent2), Commit.class);
    }

    @Override
    /** Log.
     * @return String
     * */
    public String toString() {
        return "===\ncommit " + _id
               + "\nDate: " + F.format(_time)
               + "\n" + _msg;
    }

    /** gets msg.
     * @return msg
     * */
    public String getMSG() {
        return _msg;
    }

}
