package dad.memorywindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.*;
import java.util.Properties;

public class MemoryWindow extends Application {

    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + ".VentanaConMemoria" + File.separator + "ventana.config";

    private Stage primaryStage;
    private VBox root;  // Cambiado a variable de instancia para acceso en varios métodos

    // Valores RGB iniciales por defecto
    private int red = 206;
    private int green = 63;
    private int blue = 60;

    // Valores por defecto de la ventana
    private double windowWidth = 428;
    private double windowHeight = 278;
    private double windowPosX = 440;
    private double windowPosY = 244;

    @Override
    public void init() {
        // Leer el archivo de propiedades si existe
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                red = Integer.parseInt(properties.getProperty("background.red", "206"));
                green = Integer.parseInt(properties.getProperty("background.green", "63"));
                blue = Integer.parseInt(properties.getProperty("background.blue", "60"));
                windowWidth = Double.parseDouble(properties.getProperty("size.width", "428"));
                windowHeight = Double.parseDouble(properties.getProperty("size.height", "278"));
                windowPosX = Double.parseDouble(properties.getProperty("location.x", "440"));
                windowPosY = Double.parseDouble(properties.getProperty("location.y", "244"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Crear el layout principal
        root = new VBox(10);  // Espaciado vertical de 10 píxeles
        root.setPadding(new Insets(10));

        // Crear HBoxes para cada color
        HBox hboxRed = crearHBox("Red:", red);
        HBox hboxGreen = crearHBox("Green:", green);
        HBox hboxBlue = crearHBox("Blue:", blue);

        // Añadir los HBoxes al layout principal
        root.getChildren().addAll(hboxRed, hboxGreen, hboxBlue);

        // Configurar la escena y la ventana
        Scene scene = new Scene(root, windowWidth, windowHeight);
        primaryStage.setScene(scene);
        primaryStage.setX(windowPosX);
        primaryStage.setY(windowPosY);
        primaryStage.setTitle("Memory Window");

        // Cambiar el color de fondo después de haber configurado la escena
        actualizarColorDeFondo();

        primaryStage.show();
    }

    private HBox crearHBox(String colorName, int valorInicial) {
        Label label = new Label(colorName);
        Slider slider = crearSlider(valorInicial);

        // Añadir listener para cambiar el color al mover el slider
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (colorName) {
                case "Red:" -> red = (int) newValue.doubleValue();
                case "Green:" -> green = (int) newValue.doubleValue();
                case "Blue:" -> blue = (int) newValue.doubleValue();
            }
            actualizarColorDeFondo();
        });

        // Crear un HBox para contener el Label y el Slider
        HBox hbox = new HBox(10);  // Espacio horizontal de 10 píxeles
        hbox.getChildren().addAll(label, slider); // Label primero, luego Slider
        hbox.setAlignment(Pos.CENTER_LEFT);

        return hbox;
    }

    private Slider crearSlider(int valorInicial) {
        Slider slider = new Slider(0, 255, valorInicial);
        // Desactivar las etiquetas numéricas y las marcas de graduación
        slider.setShowTickMarks(false);
        slider.setShowTickLabels(false);
        slider.setSnapToTicks(true);
        return slider;
    }

    private void actualizarColorDeFondo() {
        Color color = Color.rgb(red, green, blue);
        // Cambiar el fondo de la ventana y el VBox
        primaryStage.getScene().setFill(color);
        root.setStyle("-fx-background-color: rgba(" + red + ", " + green + ", " + blue + ", 1);");
    }

    @Override
    public void stop() {
        // Guardar las propiedades en un archivo al cerrar la ventana
        Properties properties = new Properties();
        properties.setProperty("background.red", String.valueOf(red));
        properties.setProperty("background.green", String.valueOf(green));
        properties.setProperty("background.blue", String.valueOf(blue));
        properties.setProperty("size.width", String.valueOf(primaryStage.getWidth()));
        properties.setProperty("size.height", String.valueOf(primaryStage.getHeight()));
        properties.setProperty("location.x", String.valueOf(primaryStage.getX()));
        properties.setProperty("location.y", String.valueOf(primaryStage.getY()));

        // Crear el directorio si no existe
        File configFile = new File(CONFIG_FILE);
        configFile.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Configuración de Memory Window");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
