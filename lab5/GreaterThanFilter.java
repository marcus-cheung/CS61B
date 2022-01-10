/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        col = input.colNameToIndex(colName);
        _ref = ref;

    }

    @Override
    protected boolean keep() {
        return candidateNext().getValue(col).compareTo(_ref)>0;
    }

    int col;
    String _ref;
}
