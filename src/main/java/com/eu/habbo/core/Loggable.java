package com.eu.habbo.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Loggable
{
    void log(PreparedStatement statement) throws SQLException;
}