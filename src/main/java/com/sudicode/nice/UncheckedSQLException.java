package com.sudicode.nice;

import java.sql.SQLException;

/**
 * Wraps a {@link SQLException} with an unchecked exception.
 */
public class UncheckedSQLException extends RuntimeException {

    /**
     * Construct a new {@link UncheckedSQLException}.
     *
     * @param sqlException the cause
     */
    public UncheckedSQLException(SQLException sqlException) {
        super(sqlException);
    }

}
