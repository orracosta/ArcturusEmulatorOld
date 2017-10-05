package com.habboproject.server.storage.queries.pets;

import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RoomPetDao {
    public static List<PetData> getPetsByRoomId(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<PetData> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM pet_data WHERE room_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                PetData petData = new PetData(resultSet);
                petData.setRoomPosition(new Position(resultSet.getInt("x"), resultSet.getInt("y")));

                data.add(petData);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static void updatePet(int roomId, int x, int y, int petId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE pet_data SET room_id = ?, x = ?, y = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, petId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
