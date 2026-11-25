
package com.moviestreaming.workflow;

import java.sql.Connection;
import java.sql.SQLException;

public class WorkflowTransaction {

    private WorkflowTransaction() {
    }

    public static <T> T execute(
            Connection connection,
            SqlOperation<T> operation
    ) throws SQLException {

        if (connection == null) {
            throw new IllegalArgumentException(
                    "Connection cannot be null."
            );
        }

        if (operation == null) {
            throw new IllegalArgumentException(
                    "Operation cannot be null."
            );
        }

        boolean originalAutoCommit = connection.getAutoCommit();

        if (!originalAutoCommit) {
            return operation.execute();
        }

        try {
            connection.setAutoCommit(false);

            T result = operation.execute();

            connection.commit();

            return result;

        } catch (SQLException | RuntimeException exception) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }

            throw exception;

        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @FunctionalInterface
    public interface SqlOperation<T> {
        T execute() throws SQLException;
    }
}
