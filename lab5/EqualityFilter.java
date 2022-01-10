/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        col = input.colNameToIndex(colName);
        _match = match;

    }

    @Override
    protected boolean keep() {
        return candidateNext().getValue(col).equals(_match);
    }

    String _match;
    int col;
}
