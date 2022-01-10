# Gitlet Design Document

**Name**: Marcus Cheung

## Classes and Data Structures

## Main
*Description:* This class processes commands given in terminal and applies
necessary methods from the Commits class and Storage class.

### Storage

*Description:* This class stores all commits and branches in 1 Gitlet version control system.

*Fields:*

**File cwd:** The current working directory file.

**Commit initial:** The initial commit.

**HashMap branches:** The map for all current branches of commits (key = string of branch ID, value = branch).

**HashMap allCommits:** The map for all current commit objects (key = commit message, value = commit).

**HashMap addedCommits:** The map for all added commit objects in the staging area (key = commit message, value = commit).

**HashMap removedCommits** The map for all removed commit objects in the staging area (key = commit message, value = commit).

**LinkedList commits:** The linked list of all the commits so far (used to reference parent).

### Commit
*Description*: This class saves the current file(s) as the most recent and interacts
with the user so they can see the statuses of the commits.

*Fields:*

**Integer id:** The ID of a certain commit.

**String timestamp:** The time of when a commit was created.

**String message:** The message that the user used to mark the commit.

**Commit parent:** The parent of the current commit, which is stored in a Commit object.

**Commit secondParent:** The second parent of the current commit, which is stored in a Commit object.

**HashMap blobReference:** The map showing which files are linked to a certain commit
(key = file name, value = blob).

## Algorithms

### Main
*Methods:*

**Constructor:** Decides, based on the command in terminal, which method to execute from Storage and Commit
(i.e. add, init, remove, commit). Reflects such changes in our directory. (SPECIAL CASE: if the user executes a command that is not accounted for in Main, they will receive an error
that our system doesn't understand or allow for that command)

### Storage

*Methods:*

**Constructor:** Changes the current working directory so that it is able to use Git version control.
Creates an initial commit with a default setting and also initializes the allCommits HashMap as a result. Creates the 'master' branch which all future commits
later depend on, thus also initializing the branches HashMap. (SPECIAL CASE: if there already exists a system,
this method is aborted and user will receive an error saying so)

**add:** Adds current file to the staging area. (SPECIAL CASE: if the file does not exist, then abort this method and
user will recieve an error saying the file doesn't exist)

**commit:** Clears the staging area and creates new files in a commit "snapshot." What that means is that a commit now has
access to files prior to calling commit and files afterwards. This can only be done after calling the add method on
a file/multiple files. (SPECIAL CASES: if no files have been added, this aborts and user gets an error message; if the commit
message is blank, this aborts and user gets an error message)

**rm:** Moves a file into the remove HashMap. Must un-stage the file if it is currently in the staging area. (SPECIAL CASE:
if the file doesn't exist in the current commit history or it has not been created, this aborts and throws an error message)

**log:** Shows the current commit history starting from the current HEAD, or most recent commit object and its information (i.e.
message, timestamp, ID). This follows a very specific time trajectory so that the further down on the log, the more time
has elapsed since that commit.

**global-log:** Shows all commit history ever done, but without any order.

**find:** Shows the IDs of all the actual commit objects that were assigned to a particular commit message.
This method takes in such a message and then finds these IDs accordingly, printing them out on separate lines
if there are multiple. (SPECIAL CASE: if there are no commits associated with that message, this throws an error
message)

**status:** Shows files in different categories, as follows (in this order): Branches, Staged Files, Removed Files,
Modifications Not Staged for Commit, Untracked Files. Basically every time that we make any changes using git we can
call status and see the abstraction of the program happening in actual categories and how it's related to
our current allCommits HashMap.

**checkout:** Essentially allows for us to reroute the HEAD of the commit HashMap to be any other commit in the HashMap.
We can do older versions of commits using the ID of that particular commit, but otherwise it will revert
to the most recent one. This method also allows for us to switch branches if we call it on a branch name. Moreover, if that
branch name is not the branch we are currently on, then everything within the staging area will change to reflect that.
(SPECIAL CASES: if no files exist for us to revert to (either no matching ID or literally no prior ones), throw an error message;
if no branches of a certain name exist, throw an error message)

**branch:** Creates a new branch with the given name. Shows current head node as the head of the new branch. (SPECIAL CASE: if branch
already exists with that name, throw an error message)

**rm-branch:** Removes a branch with the given name. (SPECIAL CASE: if the branch does not exist, throw an error message)

**reset:** Changes the current branch head to be the given commit instead. Removes all other commits that were
after this one prior to this command. (SPECIAL CASE: if no commit with the ID exists, throw an error message)

**merge:** Merges files from given branch into current branch. (SPECIAL CASE: if merging 2 of the same branch together, throw
an error message; if there are commits in the staging area, throw an error message; if untracked file would be
overwritten, throw an error message)

### Commit
*Methods:*

**Constructor:** Takes in String message and Commit parent; sets message and parent accordingly.

**String getTimeStamp():** Gets the timestamp.

**String getMessage():** Gets the message.

**Commit getParent():** Gets the parent of the current commit.


## Persistence

*Strategy:* We will want to save all of our commit objects so that we can access them again in the future, particularly
whenever we reference our allCommits HashMap (i.e. referring to older commits). We can do this specifically by:
* Creating our Commit class so that it implements Serializable
* Within our Structure class, constructing our commit method so that it does the following things: 1) reads the current HEAD
  and staging area, 2) clones HEAD commit and changes message/timestamp accordingly, 3) use staging area to modify files tracked
  by new commit, and 4) write back any new objects made/modified objects