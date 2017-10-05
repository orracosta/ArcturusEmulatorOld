package com.habboproject.server.storage.queries.groups;

import com.habboproject.server.game.groups.items.types.*;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class GroupItemDao {
    public static int loadGroupItems(List<GroupBase> bases, List<GroupSymbol> symbols, List<GroupBaseColour> baseColours,
                                     Map<Integer, GroupSymbolColour> symbolColours, Map<Integer, GroupBackgroundColour> backgroundColours) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_items", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                count++;

                switch (resultSet.getString("type")) {
                    case "base":
                        bases.add(new GroupBase(resultSet));
                        break;

                    case "symbol":
                        symbols.add(new GroupSymbol(resultSet));
                        break;

                    case "color":
                        baseColours.add(new GroupBaseColour(resultSet));
                        break;

                    case "color2":
                        symbolColours.put(resultSet.getInt("id"), new GroupSymbolColour(resultSet));
                        break;

                    case "color3":
                        backgroundColours.put(resultSet.getInt("id"), new GroupBackgroundColour(resultSet));
                        break;
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return count;
    }
}
