package model.utils.input;

import model.Order;
import model.Room;
import model.Seat;
import model.Theater;
import model.utils.enums.SeatStatus;
import model.utils.temp.InputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonImporter extends Importer{

    private InputData tempInputStorage;

    @Override
    public final void importFile(String filePath, InputData storage) {
        tempInputStorage = new InputData();
        JSONObject content = readJson(filePath);
        if (content == null) { return; }
        parseData(content);
        storage.addRooms(tempInputStorage.getRooms());
        storage.addTheaters(tempInputStorage.getTheaters());
        storage.addOrders(tempInputStorage.getOrders());
    }

    private JSONObject readJson(String filePath) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            return (JSONObject) obj;

        } catch (FileNotFoundException e) {
            System.out.println("File not found");               // TODO: Move it to GUI
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.out.println("Input operation interrupted");  // TODO: Move it to GUI
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            System.out.println("Invalid JSON");                 // TODO: Move it to GUI
            e.printStackTrace();
            return null;
        }
    }

    private void parseData(JSONObject content) {
        parseRooms((JSONArray) content.get("rooms"));
        parseTheaters((JSONArray) content.get("theaters"));
    }

    /**
     * Parsing all Room objects in the provided JSON.
     * @param rooms JSONObject containing the every Room's data.
     */
    private void parseRooms(JSONArray rooms) {
        for (Object roomObject : rooms) {
            JSONObject room = (JSONObject) roomObject;
            String id = parseString(room.get("id"));
            String theaterId = parseString(room.get("theater_id"));
            String name = parseString(room.get("name"));
            int columnNum = parseInt(room.get("column_num"));
            int rowNum = parseInt(room.get("row_num"));
            if (columnNum <= 0 || rowNum <= 0) {
                System.out.println("Invalid sizing parameters at Room-index: " + id);   // TODO: Move it to GUI
                continue;
            }
            Room parsedRoom = new Room(id, theaterId, name, columnNum, rowNum);
            parseSeats((JSONObject) room.get("rows"), parsedRoom);
            tempInputStorage.addRoom(parsedRoom);
        }
    }

    // TODO-Optimize: Create a new constructor for Room, where it does not generate seats.
    //  Add them now instead of replacing the (unused) old ones.

    /**
     * Parsing all the Seats and updating the rows. Should be called in Room-parsing.
     * @param rows JSONObject containing the current Room's row data.
     * @param parsedRoom The current Room's data. This will be updated.
     */
    private void parseSeats(JSONObject rows, Room parsedRoom) {
        for (int rowIndex = 0; rowIndex < parsedRoom.getRowNum(); rowIndex++) {
            JSONArray columnsInRow = (JSONArray) rows.get("" + rowIndex);
            for (int columnIndex = 0; columnIndex < parsedRoom.getColumnNum(); columnIndex ++) {
                JSONObject seat = (JSONObject) columnsInRow.get(columnIndex);
                String position = Seat.generatePositionString(rowIndex, columnIndex);
                Seat parsedSeat = parseSeat(seat, parsedRoom.getId(), position);
                parsedRoom.setSeat(rowIndex, columnIndex, parsedSeat);
            }
        }
    }

    /**
     * Parsing individual Seat objects and corresponding orders with the data provided. Should be called in Room-parsing.
     * @param seat JSONObject containing the current Seat's row data.
     * @param roomId The current Room's ID.
     * @param position Position of seat in Room. Should be generated by Seat.generatePositionString().
     */
    private Seat parseSeat(JSONObject seat, String roomId, String position) {
        String orderId = parseString(seat.get("orderId"));
        int statusNum = parseInt(seat.get("status"));
        SeatStatus status = SeatStatus.values()[statusNum];
        Seat parsedSeat = new Seat(position, roomId);
        parsedSeat.setOrderId(orderId);
        parsedSeat.setStatus(status);
        storeOrder(orderId, roomId);
        return parsedSeat;
    }

    /**
     * Searches for the provider orderId in the stored Orders. Stores it if it cannot be found.
     * @param orderId The current Order's ID.
     * @param roomId The current Room's ID.
     */
    private void storeOrder(String orderId, String roomId) {
        if (orderId == null) { return; }
        for (Order order : tempInputStorage.getOrders()) {
            if (order.getId().equals(orderId)) { return; }
        }
        tempInputStorage.addOrder(new Order(orderId, roomId));
    }

    /**
     * Parsing all Theater objects in the provided JSON.
     * @param theaters JSONObject containing the every Theater's data.
     */
    private void parseTheaters(JSONArray theaters) {
        for (Object theaterObject : theaters) {
            JSONObject theater = (JSONObject) theaterObject;
            String id = parseString(theater.get("id"));
            String name = parseString(theater.get("name"));
            tempInputStorage.addTheater(new Theater(id, name));
        }
    }

}
