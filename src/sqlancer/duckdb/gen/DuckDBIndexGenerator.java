package sqlancer.duckdb.gen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sqlancer.IgnoreMeException;
import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.ast.newast.Node;
import sqlancer.duckdb.DuckDBProvider.DuckDBGlobalState;
import sqlancer.duckdb.DuckDBSchema.DuckDBColumn;
import sqlancer.duckdb.DuckDBSchema.DuckDBTable;
import sqlancer.duckdb.DuckDBToStringVisitor;
import sqlancer.duckdb.ast.DuckDBExpression;

public class DuckDBIndexGenerator {
	
	public static Query getQuery(DuckDBGlobalState globalState) {
		if (true) {
			// https://github.com/cwida/duckdb/issues/495
			throw new IgnoreMeException();
		}
		Set<String> errors = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE ");
		if (Randomly.getBoolean()) {
			errors.add("Cant create unique index, table contains duplicate data on indexed column(s)");
			sb.append("UNIQUE ");
		}
		sb.append("INDEX ");
		sb.append(Randomly.fromOptions("i0", "i1", "i2", "i3", "i4")); // cannot query this information
		sb.append(" ON ");
		DuckDBTable table = globalState.getSchema().getRandomTable();
		sb.append(table.getName());
		sb.append("(");
		List<DuckDBColumn> columns = table.getRandomNonEmptyColumnSubset();
		for (int i = 0; i < columns.size(); i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(columns.get(i).getName());
			sb.append(" ");
			if (Randomly.getBooleanWithRatherLowProbability()) {
				sb.append(Randomly.fromOptions("ASC", "DESC"));
			}
		}
		sb.append(")");
		if (Randomly.getBoolean()) {
			sb.append(" WHERE ");
			Node<DuckDBExpression> expr = new DuckDBExpressionGenerator(globalState).setColumns(table.getColumns()).generateExpression();
			sb.append(DuckDBToStringVisitor.asString(expr));
		}
		errors.add("already exists!");
		return new QueryAdapter(sb.toString(), errors, true);
	}

}
