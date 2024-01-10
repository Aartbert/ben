package nl.han.shared.datastructures;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.han.ISavable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represents a bounded {@link Number} value
 * which is constrained by a lower and an upper bound.
 *
 * @author Jordan Geurtsen & Vasil Verdouw & Djurre Tieman
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class BoundedValue implements ISavable<BoundedValue> {
    private UUID id;
    private Number value;
    private Number upperBound;
    private Number lowerBound;

    public BoundedValue(Number value, Number upperBound, Number lowerBound) {
        this.id = UUID.randomUUID();
        this.value = value.doubleValue();
        this.upperBound = upperBound.doubleValue();
        this.lowerBound = lowerBound.doubleValue();
    }

    /**
     * Method to set the value.
     * The value is adjusted if it's outside the bounds.
     *
     * @param value the new value
     * @author Vasil Verdouw & Djurre Tieman
     */
    public void setValue(Number value) {
        double doubleValue = value.doubleValue();
        if (doubleValue > upperBound.doubleValue()) {
            this.value = upperBound;
        } else if (doubleValue < lowerBound.doubleValue()) {
            this.value = lowerBound;
        } else {
            this.value = value;
        }
    }

    /**
     * Method to set the upper bound.
     * If the new upper bound is lower than the current lower bound,
     * the current lower bound is used as the new upper bound.
     * It also adjusts the value if it's larger than the new upper bound.
     *
     * @param upperBound the new upper bound
     * @author Vasil Verdouw
     */
    public void setUpperBound(Number upperBound) {
        if (upperBound.doubleValue() < lowerBound.doubleValue()) {
            this.upperBound = lowerBound;
        } else {
            this.upperBound = upperBound;
        }

        if (value.doubleValue() > this.upperBound.doubleValue()) {
            value = this.upperBound;
        }
    }

    /**
     * Method to set the lower bound.
     * If the new lower bound is larger than the current upper bound,
     * the current upper bound is used as the new lower bound.
     * It also adjusts the value if it's less than the new lower bound.
     *
     * @param lowerBound the new lower bound
     * @author Vasil Verdouw
     */
    public void setLowerBound(Number lowerBound) {
        if (lowerBound.doubleValue() > upperBound.doubleValue()) {
            this.lowerBound = upperBound;
        } else {
            this.lowerBound = lowerBound;
        }

        if (value.doubleValue() < this.lowerBound.doubleValue()) {
            value = this.lowerBound;
        }
    }

    /**
     * Increase the value by a given value.
     * The result is adjusted if it's outside the bounds.
     *
     * @param value the increment value
     * @author Jordan Geurtsen & Djurre Tieman
     */
    public void increaseValue(Number value) {
        setValue(this.value.doubleValue() + value.doubleValue());
    }

    /**
     * Decrease the value by a given value.
     * The result is adjusted if it's outside the bounds.
     *
     * @param value the decrement value
     * @author Jordan Geurtsen & Djurre Tieman
     */
    public void decreaseValue(Number value) {
        setValue(this.value.doubleValue() - value.doubleValue());
    }

    /**
     * Convert the value to a percentage relative to the range of the bounds.
     *
     * @return a float representing a percentage value
     * @author Vasil Verdouw & Jordan Geurtsen
     */
    public double convertToPercentage() {
        double range = upperBound.doubleValue() - lowerBound.doubleValue();
        double adjustedValue = value.doubleValue() - lowerBound.doubleValue();

        return adjustedValue / range;
    }

    /**
     * Get the double value of the object.
     *
     * @return a double representing the value of the object
     * @author Djurre Tieman
     */
    public double getDoubleValue() {
        return value.doubleValue();
    }

    /**
     * Returns the integer value of the given method.
     *
     * @return the integer value of the method
     * @author Djurre Tieman
     */
    public int getIntValue() {
        return value.intValue();
    }

    /**
     * Returns the float value of the current value.
     *
     * @return the float value of the current value
     * @author Djurre Tieman
     */
    public float getFloatValue() {
        return value.floatValue();
    }

    /**
     * Retrieves the long value of the given method.
     *
     * @return the long value of the method.
     * @author Djurre Tieman
     */
    public long getLongValue() {
        return value.longValue();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     * Creates the boundedValue table.
     * <br/>
     * This table has the following columns:
     * <ul>
     *     <li>value_id</li>
     *     <li>value</li>
     *     <li>upperbound</li>
     *     <li>lowerbound</li>
     * </ul>
     * <br/>
     * The value_id is the primary key of the table and is an INT.
     *
     * @param connection The connection.
     * @throws SQLException If the query fails.
     * @author Jordan Geurtsen, Rieke Jansen
     */
    @Override
    public void tableInit(Connection connection) throws SQLException {
        String creationString = "CREATE TABLE IF NOT EXISTS bounded_value (" +
                "value_id UUID PRIMARY KEY NOT NULL," +
                "value DOUBLE NOT NULL," +
                "upperbound DOUBLE NOT NULL," +
                "lowerbound DOUBLE NOT NULL);";
        try (PreparedStatement statement = connection.prepareStatement(creationString)) {
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * Inserts into the boundedValue table
     *
     * @author Jordan Geurtsen, Rieke Jansen
     * @see Connection
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO bounded_value(value_id,value,upperbound,lowerbound) VALUES(?,?,?,?);";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setObject(1, id);
            statement.setDouble(2, value.doubleValue());
            statement.setDouble(3, upperBound.doubleValue());
            statement.setDouble(4, lowerBound.doubleValue());
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     * @param connection
     * @return
     * @author Rieke Jansen
     */
    @Override
    public boolean exists(Connection connection) throws SQLException {
        String sql = "SELECT 1 FROM bounded_value WHERE value_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * @throws SQLException If the query fails.
     * @inheritDoc
     * @author Rieke Jansen
     */
    @Override
    public void update(Connection connection) throws SQLException {
        String query = "UPDATE bounded_value SET value=?,upperbound=?,lowerbound=?" +
                "WHERE value_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setDouble(1, value.doubleValue());
            statement.setDouble(2, upperBound.doubleValue());
            statement.setDouble(3, lowerBound.doubleValue());
            statement.setString(4, String.valueOf(id));
            statement.execute();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SQLException If the query fails.
     * @author Rieke Jansen
     * @see ISavable
     * @see Connection
     */
    @Override
    public ResultSet getLoad(Connection connection, String id) throws SQLException {
        String query = "SELECT value_id,value,upperbound,lowerbound FROM bounded_value b " +
                "WHERE value_id=?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, String.valueOf(id));
            return statement.executeQuery();
        }
    }

    @Override
    public BoundedValue map(ResultSet resultSet, Connection connection) throws SQLException {
        if (resultSet.next()) {
            UUID valueId = UUID.fromString(resultSet.getString("value_id"));
            double v = resultSet.getDouble("value");
            double upperbound = resultSet.getDouble("upperbound");
            double lowerbound = resultSet.getDouble("lowerbound");
            return new BoundedValue(valueId, v, upperbound, lowerbound);
        }
        return null;
    }

    /**
     * @throws SQLException If the query fails.
     * @inheritDoc
     * @author Rieke Jansen
     */
    @Override
    public List<BoundedValue> mapList(ResultSet resultSet, Connection connection) throws SQLException {
        ArrayList<BoundedValue> mapped = new ArrayList<>();
        while (resultSet.next()) {
            UUID valueId = UUID.fromString(resultSet.getString("value_id"));
            double v = resultSet.getDouble("value");
            double upperbound = resultSet.getDouble("upperbound");
            double lowerbound = resultSet.getDouble("lowerbound");
            BoundedValue boundedValue = new BoundedValue(valueId, v, upperbound, lowerbound);
            mapped.add(boundedValue);
        }
        return mapped;
    }

    public String toJson() {
        return "{" +
                "\"id\": \"" + id + "\"," +
                "\"value\": " + value + "," +
                "\"upperBound\": " + upperBound + "," +
                "\"lowerBound\": " + lowerBound +
                "}";
    }
}
