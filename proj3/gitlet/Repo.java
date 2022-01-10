package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Gitlet Repo class.
 * @author Marcus Cheung
 */
public class Repo {

    /** Convenience. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** Convenience. */
    static final File GITLET = Utils.join(CWD, ".gitlet");
    /** Convenience. */
    static final File COMMITS = Utils.join(GITLET, "commits");
    /** Convenience. */
    static final File BLOBS = Utils.join(GITLET, "blobs");
    /** Convenience. */
    static final File ADD = Utils.join(GITLET, "add");
    /** Convenience. */
    static final File RM = Utils.join(GITLET, "rm");
    /** Convenience. */
    static final File BRANCH_NAME = Utils.join(GITLET, "branchName");
    /** Convenience. */
    static final File TREE = Utils.join(GITLET, "tree");
    /** Convenience. */
    static final File MSGS = Utils.join(GITLET, "msgs");
    /** Convenience. */
    static final File CHILDREN = Utils.join(GITLET, "children");
    /** Convenience. */
    static final File EQV = Utils.join(GITLET, "eqv");
    /** Convenience. */
    static final File IDS = Utils.join(GITLET, "ids");

    /** gets branches.
     * @return branches
     * */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, String> getBranches() {
        return Utils.readObject(TREE, LinkedHashMap.class);
    }

    /** gets branch.
     * @return branch
     * */
    @SuppressWarnings("unchecked")
    public String getBranch() {
        return Utils.readObject(BRANCH_NAME, String.class);
    }

    /** gets head.
     * @return head
     * */
    @SuppressWarnings("unchecked")
    public Commit getHead() {
        return Utils.readObject(Utils.join(COMMITS,
                getBranches().get(getBranch())), Commit.class);
    }

    /** gets children.
     * @return children
     * */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, ArrayList<String>> getChildren() {
        return Utils.readObject(CHILDREN, LinkedHashMap.class);
    }

    /** gets ID.
     * @param abrv abrv
     * @return ID
     * */
    @SuppressWarnings("unchecked")
    public String getID(String abrv) {
        LinkedHashMap<String, String> ids =
                Utils.readObject(IDS, LinkedHashMap.class);
        return ids.get(abrv);
    }

    /** gets eqv.
     * @return eqv
     * */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, String> getEqv() {
        return Utils.readObject(EQV, LinkedHashMap.class);
    }

    /** init. */
    @SuppressWarnings("unchecked")
    public void init() throws IOException {
        if (GITLET.exists()) {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
            System.exit(0);
        } else {
            GITLET.mkdir();
            COMMITS.mkdir();
            BLOBS.mkdir();

            ADD.createNewFile();
            Utils.writeObject(ADD, new LinkedHashMap<String, String>());
            RM.createNewFile();
            Utils.writeObject(RM, new ArrayList<String>());

            BRANCH_NAME.createNewFile();
            Utils.writeObject(BRANCH_NAME, "master");

            TREE.createNewFile();
            LinkedHashMap<String, String> tree = new LinkedHashMap<>();
            tree.put("master", null);
            Utils.writeObject(TREE, tree);

            MSGS.mkdir();

            CHILDREN.createNewFile();
            LinkedHashMap<String, ArrayList<String>> children =
                    new LinkedHashMap<>();
            Utils.writeObject(CHILDREN, children);

            EQV.createNewFile();
            LinkedHashMap<String, String> eqv = new LinkedHashMap<>();
            eqv.put("master", "master");
            Utils.writeObject(EQV, eqv);

            IDS.createNewFile();
            LinkedHashMap<String, String> ids = new LinkedHashMap<>();
            Utils.writeObject(IDS, ids);

            Commit initial = new Commit("initial commit", null);
            saveCommit(initial);



        }
    }
    /** add.
     * @param name name
     */
    @SuppressWarnings("unchecked")
    public void add(String name) throws IOException {
        File f = Utils.join(CWD, name);
        if (!f.exists()) {
            System.out.println("File doesn't exist");
            System.exit(0);
        } else {

            byte[] contents = Utils.readContents(f);
            String blobID = Utils.sha1(contents);
            File blob = Utils.join(BLOBS, blobID);

            ArrayList<String> rm = Utils.readObject(RM, ArrayList.class);
            if (rm.contains(name)) {
                rm.remove(name);
                Utils.writeObject(RM, rm);
            }

            if (!blob.exists()) {
                blob.createNewFile();
                Utils.writeContents(blob, contents);
            }
            LinkedHashMap<String, String> add =
                    Utils.readObject(ADD, LinkedHashMap.class);
            if (blobID.equals(getHead().getFiles().get(name))) {
                add.remove(name);
            } else {
                add.put(name, blobID);
            }
            Utils.writeObject(ADD, add);
        }
    }
    /** remove.
     * @param name name
     */
    @SuppressWarnings("unchecked")
    public void remove(String name) {
        LinkedHashMap<String, String> add =
                Utils.readObject(ADD, LinkedHashMap.class);
        if (add.containsKey(name)) {
            add.remove(name);
            Utils.writeObject(ADD, add);
        } else if (getHead().getFiles().containsKey(name)) {
            ArrayList<String> rm = Utils.readObject(RM, ArrayList.class);
            rm.add(name);
            Utils.writeObject(RM, rm);
            File f = Utils.join(CWD, name);
            f.delete();
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
    /** commit.
     * @param msg msg
     */
    public void commit(String msg) throws IOException {
        if (msg.isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Commit commit = new Commit(msg, getHead().getID());
        saveCommit(commit);
    }
    /** saveCommit.
     * @param commit c
     */
    @SuppressWarnings("unchecked")
    public void saveCommit(Commit commit) throws IOException {
        String id = commit.getID();

        File f = Utils.join(COMMITS, id);
        f.createNewFile();
        Utils.writeObject(f, commit);

        LinkedHashMap<String, String> branches = getBranches();
        branches.put(getBranch(), id);
        Utils.writeObject(TREE, branches);

        File msg = Utils.join(MSGS, commit.getMSG());
        ArrayList<String> commits;
        if (msg.exists()) {
            commits = Utils.readObject(msg, ArrayList.class);
        } else {
            msg.createNewFile();
            commits = new ArrayList<>();
        }
        commits.add(id);
        Utils.writeObject(msg, commits);

        LinkedHashMap<String, ArrayList<String>> children = getChildren();
        ArrayList<String> n = new ArrayList<>();
        n.add(getBranch());
        children.put(commit.getID(), n);
        if (commit.hasParent()) {
            String p1 = commit.getParentID();
            ArrayList<String> p1children = children.get(p1);
            p1children.add(getBranch());
            children.put(p1, p1children);
            if (commit.hasSecond()) {
                String p2 = commit.getSecondID();
                ArrayList<String> p2children = children.get(p2);
                p2children.add(getBranch());
                children.put(p2, p2children);
            }
        }
        Utils.writeObject(CHILDREN, children);

        LinkedHashMap<String, String> ids =
                Utils.readObject(IDS, LinkedHashMap.class);
        ids.put(commit.getID().substring(0, 6), commit.getID());
        Utils.writeObject(IDS, ids);
    }


    /** Log method. */
    public void log() {
        Formatter f = new Formatter();
        Commit commit = getHead();
        while (commit.hasParent()) {
            f.format(commit.toString() + "\n\n");
            commit = commit.getParent();
        }
        f.format(commit.toString());
        System.out.println(f);
    }

    /** Singular file from head.
     * @param filename f
     */
    @SuppressWarnings("unchecked")
    public void checkout(String filename) throws IOException {
        File path = Utils.join(CWD, filename);
        LinkedHashMap<String, String> files = getHead().getFiles();
        if (!files.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String bname = files.get(filename);
        if (!path.exists()) {
            path.createNewFile();
        }
        Utils.writeContents(path, Utils.readContents(Utils.join(BLOBS, bname)));
    }

    /** Checkout file from particular commit.
     * @param commitID id
     * @param filename f
     */
    @SuppressWarnings("unchecked")

    public void checkout(String commitID, String filename) throws IOException {
        commitID = getID(commitID.substring(0, 6));

        if (commitID == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        File c = Utils.join(COMMITS, commitID);

        Commit commit = Utils.readObject(c, Commit.class);
        if (!commit.getFiles().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String bname = commit.getFiles().get(filename);
        File path = Utils.join(CWD, filename);
        if (!path.exists()) {
            path.createNewFile();
        }
        Utils.writeContents(path, Utils.readContents(Utils.join(BLOBS, bname)));
    }

    /** branch checkout.
     * @param branchname name
     */
    public void bcheckout(String branchname) throws IOException {
        Commit current = getHead();
        LinkedHashMap<String, String> branches = getBranches();
        if (branchname.equals(getBranch())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        } else if (!branches.containsKey(branchname)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else {
            Commit other = Utils.readObject(Utils.join(COMMITS,
                    branches.get(branchname)), Commit.class);
            Set<String> currentTracked = current.getFiles().keySet();
            List<String> dir = Utils.plainFilenamesIn(CWD);
            for (String s : dir) {
                byte[] contents = Utils.readContents(Utils.join(CWD, s));
                String blobID = Utils.sha1(contents);
                if (!currentTracked.contains(s)
                        && other.getFiles().containsKey(s)
                        && !other.getFiles().get(s).equals(blobID)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first");
                    System.exit(0);
                }
            }
            for (String s : other.getFiles().keySet()) {
                String bname = other.getFiles().get(s);
                File path = Utils.join(CWD, s);
                if (!path.exists()) {
                    path.createNewFile();
                }
                Utils.writeContents(path,
                        Utils.readContents(Utils.join(BLOBS, bname)));
            }
            Set<String> otherTracked = other.getFiles().keySet();
            for (String s: current.getFiles().keySet()) {
                if (!otherTracked.contains(s)) {
                    Utils.join(CWD, s).delete();
                }
            }

            Utils.writeObject(BRANCH_NAME, branchname);

        }
    }
    /** global log. */
    @SuppressWarnings("unchecked")
    public void globalLog() {
        Formatter f = new Formatter();
        List<String> names = Utils.plainFilenamesIn(COMMITS);
        for (int i = 1; i < names.size(); i++) {
            Commit commit = Utils.readObject(
                    Utils.join(COMMITS, names.get(i)), Commit.class);
            f.format(commit.toString() + "\n\n");
        }
        Commit commit = Utils.readObject(
                Utils.join(COMMITS, names.get(0)), Commit.class);
        f.format(commit.toString());
        System.out.println(f);
    }
    /** find.
     * @param msg msg
     */
    @SuppressWarnings("unchecked")
    public void find(String msg) {
        Formatter f = new Formatter();
        if (msg.charAt(0) == '\"' && msg.charAt(msg.length() - 1) == '\"') {
            msg = msg.substring(1, msg.length() - 1);
        }
        File msgFile = Utils.join(MSGS, msg);
        if (msgFile.exists()) {
            ArrayList<String> commits =
                    Utils.readObject(msgFile, ArrayList.class);
            for (String commitID : commits) {
                f.format(commitID + "\n");
            }
            System.out.println(f);
        } else {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    /** status.*/
    @SuppressWarnings("unchecked")
    public void status() {
        Formatter f = new Formatter();
        f.format("=== Branches ===\n");
        String hbranch = getBranch();
        f.format("*" + hbranch + "\n");
        for (String s : getBranches().keySet()) {
            if (!s.equals(hbranch)) {
                f.format(s + "\n");
            }
        }
        f.format("\n=== Staged Files ===\n");
        LinkedHashMap<String, String> add =
                Utils.readObject(ADD, LinkedHashMap.class);
        for (String s : add.keySet()) {
            f.format(s + "\n");
        }
        f.format("\n=== Removed Files ===\n");
        ArrayList<String> rm = Utils.readObject(RM, ArrayList.class);
        for (String s : rm)  {
            f.format(s + "\n");
        }
        f.format("\n=== Modifications Not Staged For Commit ===\n");

        f.format("\n=== Untracked Files ===\n");
        List<String> files = Utils.plainFilenamesIn(CWD);
        Collections.sort(files);
        for (String s : files) {
            if (!add.containsKey(s) && !getHead().getFiles().containsKey(s)) {
                f.format(s + "\n");
            }
        }
        System.out.println(f);
    }


    /** branch.
     * @param name name
     */
    @SuppressWarnings("unchecked")
    public void branch(String name) {
        LinkedHashMap<String, String> branches = getBranches();
        if (branches.containsKey(name)) {
            System.out.println("A branch with that name already exists");
            System.exit(0);
        } else {
            String id = getHead().getID();
            branches.put(name, id);
            Utils.writeObject(TREE, branches);
            addEqv(name, name);

            LinkedHashMap<String, ArrayList<String>> children = getChildren();
            ArrayList<String> n = children.get(id);
            n.add(name);
            children.put(id, n);
            Utils.writeObject(CHILDREN, children);
        }
    }

    /** addEqv.
     * @param other other
     * @param name name
     */
    @SuppressWarnings("unchecked")
    public void addEqv(String name, String other) {
        LinkedHashMap<String, String> eqv = getEqv();
        eqv.put(name, other);
        Utils.writeObject(EQV, eqv);
    }

    /** rmBranch.
     * @param name name
     */
    @SuppressWarnings("unchecked")
    public void rmBranch(String name) {
        LinkedHashMap<String, String> branches = getBranches();
        if (!branches.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (name.equals(getBranch())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else {
            branches.remove(name);
            Utils.writeObject(TREE, branches);
        }
    }

    /** reset.
     * @param id id
     */
    @SuppressWarnings("unchecked")
    public void reset(String id) throws IOException {
        id = getID(id.substring(0, 6));
        if (id == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit current = getHead();
        Commit other = Utils.readObject(Utils.join(COMMITS, id), Commit.class);
        Set<String> currentTracked = current.getFiles().keySet();
        List<String> dir = Utils.plainFilenamesIn(CWD);
        for (String s : dir) {
            byte[] contents = Utils.readContents(Utils.join(CWD, s));
            String blobID = Utils.sha1(contents);
            if (!currentTracked.contains(s)
                    && other.getFiles().containsKey(s)
                    && !other.getFiles().get(s).equals(blobID)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first");
                System.exit(0);
            }
        }
        for (String s : Utils.plainFilenamesIn(CWD)) {
            if (!other.getFiles().containsKey(s)) {
                Utils.join(CWD, s).delete();
            }
        }
        for (String s : other.getFiles().keySet()) {
            String bname = other.getFiles().get(s);
            File path = Utils.join(CWD, s);
            if (!path.exists()) {
                path.createNewFile();
            }
            Utils.writeContents(path,
                    Utils.readContents(Utils.join(BLOBS, bname)));
        }
        LinkedHashMap<String, String> branches = getBranches();
        String name = getBranch();
        branches.put(name, id);
        Utils.writeObject(TREE, branches);

        Utils.writeObject(ADD, new LinkedHashMap<String, String>());
    }

    /** eqv.
     * @param name name
     * @return name
     */
    @SuppressWarnings("unchecked")
    public String eqvName(String name) {
        LinkedHashMap<String, String> eqv = getEqv();
        String next = eqv.get(name);
        while (name != next) {
            name = next;
            next = eqv.get(name);
        }
        return name;
    }

    /** merge.
     * @param other other
     */
    @SuppressWarnings("unchecked")
    public void merge(String other) throws IOException {
        LinkedHashMap<String, String> add =
                Utils.readObject(ADD, LinkedHashMap.class);
        ArrayList<String> rm = Utils.readObject(RM, ArrayList.class);
        mergeVerify(other, add, rm);
        Commit o = Utils.readObject(Utils.join(COMMITS,
                getBranches().get(other)), Commit.class);
        Commit head = getHead();
        Commit split = getSplit(other);
        verifySplit(split, head, o, other);
        Set<Map.Entry<String, String>> cMod = modified(head, split);
        Set<Map.Entry<String, String>> oMod = modified(o, split);
        Set<String> cModK = modifiedKeys(cMod);
        Set<String> oModK = modifiedKeys(oMod);
        Set<String> addFiles = new HashSet<>();
        Set<String> rmFiles = new HashSet<>();
        Set<Map.Entry<String, String>> case1 = new HashSet<>(oMod);
        case1.removeAll(cMod);
        for (Map.Entry<String, String> e : case1) {
            if (!dontAdd(o.getFiles(), head.getFiles(),
                    split.getFiles(), cModK, oModK, e.getKey())) {
                addFiles.add(e.getKey());
            }
        }
        Set<String> case2 = new HashSet<>(o.getFiles().keySet());
        case2.removeAll(split.getFiles().keySet());
        for (String f : case2) {
            if (!dontAdd(o.getFiles(), head.getFiles(),
                    split.getFiles(), cModK, oModK, f)) {
                addFiles.add(f);
            }
        }
        Set<Map.Entry<String, String>> case3 =
                new HashSet<>(split.getFiles().entrySet());
        for (Map.Entry<String, String> e : case3) {
            String f = e.getKey();

            if (!cMod.contains(e)
                    && !o.getFiles().containsKey(f)
                    && !stay(o.getFiles(), head.getFiles(),
                            split.getFiles(), cModK, oModK, f)) {
                rmFiles.add(f);
            }
        }

        Set<String> conflictFiles = getConflict(split, head, o, oMod, cMod);
        mergeUntrackedVerify(addFiles, rmFiles, conflictFiles);
        mergeCommit(addFiles, rmFiles, conflictFiles, other);
    }

    /** merge.
     * @param split split
     * @param head head
     * @param o o
     * @param oMod oMod
     * @param cMod cMod
     * @return conflicts
     */
    @SuppressWarnings("unchecked")
    public Set<String> getConflict(Commit split, Commit head, Commit o,
                                   Set<Map.Entry<String, String>> oMod,
                                   Set<Map.Entry<String, String>> cMod) {
        Set<String> conflictFiles = new HashSet<>();
        for (Map.Entry<String, String> e : split.getFiles().entrySet()) {
            String f = e.getKey();
            String b = e.getValue();
            LinkedHashMap<String, String> hmap = head.getFiles();
            LinkedHashMap<String, String> omap = o.getFiles();
            if (hmap.containsKey(f)
                    && omap.containsKey(f)
                    && !hmap.get(f).equals(omap.get(f))
                    && !hmap.get(f).equals(b)
                    && !omap.get(f).equals(b)
            ) {
                conflictFiles.add(e.getKey());
            }
        }
        for (Map.Entry<String, String> e : oMod) {
            if (!head.getFiles().containsKey(e.getKey())) {
                conflictFiles.add(e.getKey());
            }
        }
        for (Map.Entry<String, String> e : cMod) {
            if (!o.getFiles().containsKey(e.getKey())) {
                conflictFiles.add(e.getKey());
            }
        }
        Set<String> all = new HashSet<>(o.getFiles().keySet());
        all.retainAll(head.getFiles().keySet());
        for (String f : all) {
            if (!split.getFiles().containsKey(f)
                    && !o.getFiles().get(f).equals(head.getFiles().get(f))) {
                conflictFiles.add(f);
            }
        }
        return conflictFiles;
    }

    /** stay.
     * @param head head
     * @param other other
     * @param cMod cMod
     * @param oMod oMod
     * @param f f
     * @param split split
     * @return bool
     */
    public boolean stay(LinkedHashMap other, LinkedHashMap head,
                        LinkedHashMap split, Set<String> cMod,
                        Set<String> oMod, String f) {
        return (other.containsKey(f) && head.containsKey(f)
                && other.get(f).equals(head.get(f)))
                || (cMod.contains(f) && !oMod.contains(f))
                || (!split.containsKey(f) && !other.containsKey(f)
                        && head.containsKey(f))
                || (split.containsKey(f) && !oMod.contains(f)
                        && !head.containsKey(f));
    }

    /** don't Add.
     * @param split split
     * @param head head
     * @param other other
     * @param cMod cMod
     * @param oMod oMod
     * @param f f
     * @return bool
     */
    public boolean dontAdd(LinkedHashMap other, LinkedHashMap head,
                           LinkedHashMap split, Set<String> cMod,
                           Set<String> oMod, String f) {
        return (cMod.contains(f) && !oMod.contains(f))
                ||  (!other.containsKey(f)
                        && !head.containsKey(f))
                || (other.get(f).equals(head.get(f)))
                || (!split.containsKey(f)
                        && !other.containsKey(f) && head.containsKey(f))
                || (split.containsKey(f)
                        && !other.containsKey(f) && !cMod.contains(f))
                || (split.containsKey(f)
                        && !head.containsKey(f) && !oMod.contains(f));
    }


    /** verify.
     * @param other other
     * @param add add
     * @param rm rm
     */
    public void mergeVerify(String other, LinkedHashMap add, ArrayList rm) {
        if (getBranch().equals(other)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        } else if (!getBranches().containsKey(other)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (!add.isEmpty() || !rm.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }

    /** verify.
     * @param split split
     * @param head head
     * @param other other
     * @param name name
     */
    public void verifySplit(Commit split, Commit head,
                            Commit other, String name) throws IOException {
        if (split.getID().equals(other.getID())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            System.exit(0);
        }
        if (head.getID().equals(split.getID())) {
            System.out.println("Current branch fast-forwarded.");
            bcheckout(name);
            System.exit(0);
        }
    }
    /** Untracked.
     * @param add add
     * @param rm rm
     * @param conflict conflict
     */
    public void mergeUntrackedVerify(Set<String> add,
                                     Set<String> rm,
                                     Set<String> conflict) {
        Set<String> currentTracked = getHead().getFiles().keySet();
        for (String f : Utils.plainFilenamesIn(CWD)) {
            if (!currentTracked.contains(f)
                    && (add.contains(f)
                            || rm.contains(f)
                            || conflict.contains(f))) {
                System.out.println(
                        "There is an untracked file in the way; "
                                + "delete it, or add and commit it first");
                System.exit(0);
            }
        }
    }
    /** split.
     * @param other other
     * @return split
     */
    @SuppressWarnings("unchecked")
    public Commit getSplit(String other) throws IOException {
        ArrayList<Commit> queue = new ArrayList();
        HashMap<String, Integer> distances = new HashMap<>();
        int minDistance = Integer.MAX_VALUE;
        Commit head = getHead();
        Commit split = null;
        queue.add(head);
        distances.put(queue.get(0).getID(), 0);
        while (!queue.isEmpty()) {
            Commit current = queue.remove(0);
            String id = current.getID();
            int distance = distances.get(id);
            ArrayList<String> children = getChildren().get(id);

            for (int i = 0; i < children.size(); i++) {
                children.set(i, eqvName(children.get(i)));
            }
            if (children.contains(getBranch())
                    && children.contains(other)) {
                if (split != null) {
                    if (distance <= minDistance) {
                        split = current;
                    }
                    break;
                } else {
                    split = current;
                    minDistance = distance;
                }
            }
            if (current.hasParent()) {
                int pDistance = distances.get(current.getID()) + 1;
                Commit p1 = current.getParent();
                queue.add(p1);
                distances.put(p1.getID(), pDistance);
                if (current.hasSecond()) {
                    Commit p2 = current.getSecond();
                    queue.add(p2);
                    distances.put(p2.getID(), pDistance);
                }
            } else if (split == null) {
                split = current;
                minDistance = distance;
            }
        }
        return split;
    }

    /** modrm.
     * @param c c
     * @param split split
     * @return modrm
     */
    public Set<Map.Entry<String, String>> modifiedRM(Commit c, Commit split) {
        Set<Map.Entry<String, String>> splitFiles = new
                HashSet<>(split.getFiles().entrySet());
        Set<Map.Entry<String, String>> modRM = new
                HashSet<>(c.getFiles().entrySet());
        modRM.removeAll(splitFiles);
        return modRM;
    }

    /** modK.
     * @param c c
     * @param split split
     * @return mod
     */
    public Set<Map.Entry<String, String>> modified(Commit c, Commit split) {
        Set<Map.Entry<String, String>> splitFiles =
                new HashSet<>(split.getFiles().entrySet());
        Set<Map.Entry<String, String>> modRM =
                new HashSet<>(c.getFiles().entrySet());
        modRM.removeAll(splitFiles);

        Set<Map.Entry<String, String>> mod = new HashSet<>();
        for (Map.Entry<String, String> e : modRM) {
            if (split.getFiles().containsKey(e.getKey())) {
                mod.add(e);
            }
        }
        return mod;
    }

    /** modK.
     * @param mod mod
     * @return modK
     */
    public Set<String> modifiedKeys(Set<Map.Entry<String, String>> mod) {
        Set<String> mK = new HashSet<>();
        for (Map.Entry<String, String> e : mod) {
            mK.add(e.getKey());
        }
        return mK;
    }

    /** removed.
     * @param c c
     * @param split split
     * @return rm
     */
    public Set<Map.Entry<String, String>> removed(Commit c, Commit split) {
        Set<Map.Entry<String, String>> splitFiles =
                new HashSet<>(split.getFiles().entrySet());
        Set<Map.Entry<String, String>> modRM =
                new HashSet<>(c.getFiles().entrySet());
        modRM.removeAll(splitFiles);

        Set<Map.Entry<String, String>> rm = new HashSet<>();
        for (Map.Entry<String, String> e : modRM) {
            if (!split.getFiles().containsKey(e.getKey())) {
                rm.add(e);
            }
        }
        return rm;
    }

    /** mergeCommit.
     * @param add add
     * @param branch branch
     * @param conflictFiles conflictFiles
     * @param rm rm
     */
    public void mergeCommit(Set<String> add, Set<String> rm,
                            Set<String> conflictFiles,
                            String branch) throws IOException {
        Commit o = Utils.readObject(Utils.join(
                COMMITS, getBranches().get(branch)), Commit.class);
        String other = o.getID();

        for (String f : add) {
            checkout(other, f);
            add(f);
        }

        for (String f : rm) {
            remove(f);
        }
        LinkedHashMap<String, String> headFiles = getHead().getFiles();
        LinkedHashMap<String, String> otherFiles = o.getFiles();
        for (String file : conflictFiles) {
            String cCont = "";
            if (headFiles.containsKey(file)) {
                cCont = Utils.readContentsAsString(
                        Utils.join(BLOBS, headFiles.get(file)));
            }
            String oCont = "";
            if (otherFiles.containsKey(file)) {
                oCont = Utils.readContentsAsString(
                        Utils.join(BLOBS, otherFiles.get(file)));
            }
            String replace = "<<<<<<< HEAD\n"
                    + cCont
                    + "=======\n"
                    + oCont
                    + ">>>>>>>\n";
            Utils.writeContents(Utils.join(CWD, file), replace);
            add(file);
        }
        String msg = "Merged " + branch + " into " + getBranch() + ".";
        LinkedHashMap<String, String> eqv = getEqv();
        eqv.put(other, getBranch());
        Utils.writeObject(EQV, eqv);
        Commit commit = new Commit(msg, getHead().getID(), o.getID());
        saveCommit(commit);
        if (conflictFiles.size() > 0) {
            System.out.println("Encountered a merge conflict.");
        }
    }
}
