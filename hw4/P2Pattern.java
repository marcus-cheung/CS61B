/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static String P1 = "(\\d|0\\d|1[012])[/](\\d|[0-2]\\d|3[0-1])[/]([1-9]\\d{3})";

    /** Pattern to match 61b notation for literal IntLists. */
    public static String P2 = "^[\\(](\\d+[,]\\s+)*(\\d+)[\\)]";

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static String P3 = "(\\w*)(\\w+)(-)*(\\w+)[.](.\\w+)"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static String P4 = ""; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static String P5 = "(((25[0-5])|([01]?[0-9]{2})|(2[0-4]\\d|\\d))[.]){3}((25[0-5])|([01]?[0-9]{2})|(2[0-4]\\d|\\d))";
}