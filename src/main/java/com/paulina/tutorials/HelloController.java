package com.paulina.tutorials;

import com.paulina.tutorials.models.Tutorial;
import com.paulina.tutorials.services.TutorialService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class HelloController {
    @FXML
    private TableView<Tutorial> tutorialsTable;
    @FXML
    private TableColumn<Tutorial, Long> idColumn;
    @FXML
    private TableColumn<Tutorial, String> titleColumn;
    @FXML
    private TableColumn<Tutorial, String> descriptionColumn;
    @FXML
    private TableColumn<Tutorial, Boolean> publishedColumn;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;

    private final TutorialService tutorialService = new TutorialService();

    @FXML
    public void initialize() {


        idColumn.setCellValueFactory(cell -> {
            Tutorial tutorial = cell.getValue();
            if (tutorial == null || tutorial.getId() == null) {
                return new javafx.beans.property.SimpleLongProperty(0L).asObject();
            }
            return new javafx.beans.property.SimpleLongProperty(tutorial.getId()).asObject();
        });

        titleColumn.setCellValueFactory(cell -> {
            Tutorial tutorial = cell.getValue();
            return tutorial == null ? null :
                    new javafx.beans.property.SimpleStringProperty(tutorial.getTitle());
        });

        descriptionColumn.setCellValueFactory(cell -> {
            Tutorial tutorial = cell.getValue();
            return tutorial == null ? null :
                    new javafx.beans.property.SimpleStringProperty(tutorial.getDescription());
        });

        publishedColumn.setCellValueFactory(cell -> {
            Tutorial tutorial = cell.getValue();
            if (tutorial == null) return null;
            return new javafx.beans.property.SimpleBooleanProperty(tutorial.isPublished());
        });


        refreshTutorials();

    }


    @FXML
    protected void onRefreshClick() {
        refreshTutorials();
    }

    @FXML
    private void refreshTutorials() {
        try {
            refreshButton.setDisable(true);
            statusLabel.setText("⏳ Loading from API...");

            List<Tutorial> tutorials = tutorialService.getAllTutorials();
            ObservableList<Tutorial> observableTutorials = FXCollections.observableArrayList(tutorials);
            tutorialsTable.setItems(observableTutorials);

            statusLabel.setText("✅ Loaded " + tutorials.size() + " tutorials");
        } catch (Exception e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
        } finally {
            refreshButton.setDisable(false);
        }
    }

}