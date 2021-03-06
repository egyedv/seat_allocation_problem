import controller.Controller;
import database.DatabaseHandler;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Theater;
import model.utils.input.DatabaseLoader;
import model.utils.input.JsonImporter;
import model.utils.output.JsonExporter;
import model.utils.temp.InputData;
import view.View;

public class Main extends Application {

    private static final String inFilePath = "temp_files/input_1.json";  // TODO: Replace with GUI fileExplorer
    private static final String outFilePath = "temp_files/output_1.json";  // TODO: Replace with GUI fileExplorer

    @Override
    public void start(Stage stage) {
        View mainView = new View();
        new Controller(mainView);
        Theater th = new Theater("Csiky Gergely");
        JsonImporter jsonImporter = new JsonImporter(InputData.getInstance());
        jsonImporter.importFile(inFilePath);
        // DatabaseLoader.loadAll(inputData);
        JsonExporter jsonExporter = new JsonExporter(InputData.getInstance());
        jsonExporter.exportFile(outFilePath);
    }

    public static void main(String[] args) {
        launch();
    }
}
