import java.io.Reader;
import java.io.IOException;


/** Translating Reader: a stream that is a translation of an
*  existing reader.
*  @author Marcus Cheung
*
*  NOTE: Until you fill in the right methods, the compiler will
*        reject this file, saying that you must declare TrReader
* 	     abstract.  Don't do that; define the right methods instead!
*/
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    Reader _reader;
    String _from;
    String _to;
    public TrReader(Reader str, String from, String to) {
        _reader = str;
        _from = from;
        _to = to;
    }

    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int result = _reader.read(cbuf, off, len);
        for(int i = 0; i<cbuf.length; i++){
            int index = _from.indexOf(cbuf[i]);
            if (index>=0){
                cbuf[i] = _to.charAt(index);
            }
        }
        return result;
    }

    @Override
    public void close() throws IOException{
        _reader.close();
    }
}
