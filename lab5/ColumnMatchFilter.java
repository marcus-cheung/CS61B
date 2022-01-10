/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        col1 = input.colNameToIndex(colName1);
        col2 = input.colNameToIndex(colName2);
    }

    @Override
    protected boolean keep() {
        Table.TableRow r = candidateNext();
        return r.getValue(col1).equals(r.getValue(col2));
    }

    int col1;
    int col2;
}
