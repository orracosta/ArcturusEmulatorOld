package com.habboproject.server.storage.queries.pets;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.game.pets.data.PetMessageType;
import com.habboproject.server.game.pets.data.PetSpeech;
import com.habboproject.server.game.pets.data.StaticPetProperties;
import com.habboproject.server.game.pets.races.PetRace;
import com.habboproject.server.storage.SqlHelper;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class PetDao {
    public static List<PetRace> getRaces() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<PetRace> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM pet_races", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(new PetRace(resultSet));
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

    public static Map<Integer, PetSpeech> getMessages(AtomicInteger petSpeechCount) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, PetSpeech> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM pet_messages", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int petType = resultSet.getInt("pet_type");
                PetMessageType messageType = PetMessageType.valueOf(resultSet.getString("message_type"));

                if(!data.containsKey(petType)) {
                    data.put(petType, new PetSpeech());
                }

                PetSpeech petSpeech = data.get(petType);

                if(!petSpeech.getMessages().containsKey(messageType)) {
                    petSpeech.getMessages().put(messageType, Lists.newArrayList());
                }

                petSpeechCount.incrementAndGet();
                petSpeech.getMessages().get(messageType).add(resultSet.getString("message_string"));
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

    public static Map<String, String> getTransformablePets() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, String> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM pet_transformable", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getString("name"), resultSet.getString("data"));
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

    public static Map<Integer, PetData> getPetsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, PetData> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM pet_data WHERE owner_id = ? AND room_id = 0", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getInt("id"), new PetData(resultSet));
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

    public static int createPet(int ownerId, String petName, int type, int race, String colour) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO `pet_data` (`owner_id`, `pet_name`, `type`, `race_id`, `colour`, `scratches`, `level`, `happiness`, `experience`, `energy`, `birthday`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, ownerId);
            preparedStatement.setString(2, petName);
            preparedStatement.setInt(3, type);
            preparedStatement.setInt(4, race);
            preparedStatement.setString(5, colour);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, StaticPetProperties.DEFAULT_LEVEL);
            preparedStatement.setInt(8, StaticPetProperties.DEFAULT_HAPPINESS);
            preparedStatement.setInt(9, StaticPetProperties.DEFAULT_EXPERIENCE);
            preparedStatement.setInt(10, StaticPetProperties.DEFAULT_ENERGY);
            preparedStatement.setInt(11, (int) Comet.getTime());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return 0;
    }

    public static void savePosition(int x, int y, int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE pet_data SET x = ?, y = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);

            preparedStatement.setInt(3, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveStats(int scratches, int level, int happiness, int experience, int energy, int petId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE pet_data SET scratches = ?, level = ?, happiness = ?, experience = ?, energy = ? WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, scratches);
            preparedStatement.setInt(2, level);
            preparedStatement.setInt(3, happiness);
            preparedStatement.setInt(4, experience);
            preparedStatement.setInt(5, energy);
            preparedStatement.setInt(6, petId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deletePets(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM pet_data WHERE owner_id = ? AND room_id = 0;", sqlConnection);
            preparedStatement.setInt(1, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveHorseData(int id, boolean saddled, int hair, int hairDye, boolean anyRider, int raceId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE pet_data SET saddled = ?, hair_style = ?, hair_colour = ?, any_rider = ?, race_id = ? WHERE id = ?", sqlConnection);

            preparedStatement.setString(1, saddled ? "true" : "false");
            preparedStatement.setInt(2, hair);
            preparedStatement.setInt(3, hairDye);
            preparedStatement.setString(4, anyRider ? "true" : "false");
            preparedStatement.setInt(5, raceId);

            preparedStatement.setInt(6, id);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
