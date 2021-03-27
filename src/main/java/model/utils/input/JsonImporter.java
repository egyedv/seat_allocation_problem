package model.utils.input;

import lombok.Getter;
import model.Room;
import model.Theater;
import model.utils.temp.InputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonImporter extends Importer{

    @Override
    public final void importFile(String filePath, InputData storage) {
        JSONObject content = readJson(filePath);
        if (content == null) { return; }
        parseData(content);
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
    }

    private void parseRooms(JSONArray rooms) {
        JSONObject room = (JSONObject) rooms.get(1);
        String id = (String) room.get("id");
        String theaterId = (String) room.get("theater_id");
        int columnNum = parseInt(room.get("column_num"));
        int rowNum = parseInt(room.get("row_num"));
        Room parsedRoom = new Room(id, theaterId, columnNum, rowNum);
    }

    private int parseInt(Object object) {
        String value = object.toString();
        return Integer.parseInt(value);
    }

}
