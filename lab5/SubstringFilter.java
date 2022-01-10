/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        col = input.colNameToIndex(colName);
        _subStr = subStr;
    }

    @Override
    protected boolean keep() {
        return candidateNext().getValue(col).contains(_subStr);
    }

    int col;
    String _subStr;
}
