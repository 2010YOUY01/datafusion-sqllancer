package sqlancer.common.schema;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;

public class AbstractSchema<A extends AbstractTable<?, ?>> {

    private final List<A> databaseTables;

    public AbstractSchema(List<A> databaseTables) {
        this.databaseTables = Collections.unmodifiableList(databaseTables);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (A t : getDatabaseTables()) {
            sb.append(t);
            sb.append("\n");
        }
        return sb.toString();
    }

    public A getRandomTable() {
        return Randomly.fromList(getDatabaseTables());
    }

    public A getRandomTable(Predicate<A> predicate) {
        return Randomly.fromList(getDatabaseTables().stream().filter(predicate).collect(Collectors.toList()));
    }

    public A getRandomTableOrBailout(Function<A, Boolean> f) {
        List<A> relevantTables = databaseTables.stream().filter(t -> f.apply(t)).collect(Collectors.toList());
        if (relevantTables.isEmpty()) {
            throw new IgnoreMeException();
        }
        return Randomly.fromList(relevantTables);
    }

    public List<A> getDatabaseTables() {
        return databaseTables;
    }

    public List<A> getDatabaseTablesRandomSubsetNotEmpty() {
        return Randomly.nonEmptySubset(databaseTables);
    }

    public A getDatabaseTable(String name) {
        return databaseTables.stream().filter(t -> t.getName().equals(name)).findAny().orElse(null);
    }

    public String getFreeIndexName() {
        int i = 0;
        if (Randomly.getBooleanWithRatherLowProbability()) {
            i = (int) Randomly.getNotCachedInteger(0, 100);
        }
        do {
            String indexName = String.format("i%d", i++);
            boolean indexNameFound = false;
            for (A table : databaseTables) {
                if (table.getIndexes().stream().anyMatch(ind -> ind.getIndexName().contentEquals(indexName))) {
                    indexNameFound = true;
                    break;
                }
            }
            if (!indexNameFound) {
                return indexName;
            }
        } while (true);
    }

    public String getFreeTableName() {
        int i = 0;
        if (Randomly.getBooleanWithRatherLowProbability()) {
            i = (int) Randomly.getNotCachedInteger(0, 100);
        }
        do {
            String tableName = String.format("t%d", i++);
            if (databaseTables.stream().noneMatch(t -> t.getName().contentEquals(tableName))) {
                return tableName;
            }
        } while (true);

    }

    public String getFreeViewName() {
        int i = 0;
        if (Randomly.getBooleanWithRatherLowProbability()) {
            i = (int) Randomly.getNotCachedInteger(0, 100);
        }
        do {
            String tableName = String.format("v%d", i++);
            if (databaseTables.stream().noneMatch(t -> t.getName().contentEquals(tableName))) {
                return tableName;
            }
        } while (true);

    }

}
