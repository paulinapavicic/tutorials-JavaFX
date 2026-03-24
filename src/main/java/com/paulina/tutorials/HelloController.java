package com.paulina.tutorials;

import com.paulina.tutorials.models.Book;
import com.paulina.tutorials.models.Tutorial;
import com.paulina.tutorials.services.BookService;
import com.paulina.tutorials.services.TutorialService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private CheckBox publishedCheck;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;


    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Long> booksIdColumn;
    @FXML private TableColumn<Book, String> booksTitleColumn;
    @FXML private TableColumn<Book, String> booksAuthorColumn;
    @FXML private TableColumn<Book, String> booksIsbnColumn;
    @FXML private TableColumn<Book, Boolean> booksAvailableColumn;
    @FXML private TextField booksTitleField, booksAuthorField, booksIsbnField;
    @FXML private CheckBox booksAvailableCheck;
    @FXML private Button booksRefreshButton, booksSaveButton, booksUpdateButton, booksDeleteButton, booksClearButton;
    @FXML private Label booksStatusLabel;


    private Tutorial selectedTutorial = null;

    private final TutorialService tutorialService = new TutorialService();
    private final BookService bookService = new BookService();
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

        tutorialsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedTutorial = newSelection;
                        populateEditForm(newSelection);
                        statusLabel.setText("✏️ Edit: " + newSelection.getTitle());
                    } else {
                        onClearForm();
                        statusLabel.setText("👆 Select row to edit");
                    }
                }
        );
        booksIdColumn.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            return book == null || book.getId() == null ?
                    new javafx.beans.property.SimpleLongProperty(0L).asObject() :
                    new javafx.beans.property.SimpleLongProperty(book.getId()).asObject();
        });

        booksTitleColumn.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            return book == null ? null :
                    new javafx.beans.property.SimpleStringProperty(book.getTitle());
        });

        booksAuthorColumn.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            return book == null ? null :
                    new javafx.beans.property.SimpleStringProperty(book.getAuthor());
        });

        booksIsbnColumn.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            return book == null ? null :
                    new javafx.beans.property.SimpleStringProperty(book.getIsbn());
        });

        booksAvailableColumn.setCellValueFactory(cell -> {
            Book book = cell.getValue();
            return book == null ? null :
                    new javafx.beans.property.SimpleBooleanProperty(book.isAvailable());
        });
        booksTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        booksTitleField.setText(newSelection.getTitle());
                        booksAuthorField.setText(newSelection.getAuthor() != null ? newSelection.getAuthor() : "");
                        booksIsbnField.setText(newSelection.getIsbn() != null ? newSelection.getIsbn() : "");
                        booksAvailableCheck.setSelected(newSelection.isAvailable());
                        booksStatusLabel.setText("✏️ Edit: " + newSelection.getTitle());
                    } else {
                        clearBooksForm();
                        booksStatusLabel.setText("👆 Select row to edit");
                    }
                }
        );

        refreshTutorials();

    }

    private void populateEditForm(Tutorial tutorial) {
        titleField.setText(tutorial.getTitle());
        descriptionField.setText(tutorial.getDescription());
        publishedCheck.setSelected(tutorial.isPublished());
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

    @FXML
    private void onSaveClick() {

        if (titleField.getText().trim().isEmpty()) {
            statusLabel.setText("❌ Title is required!");
            return;
        }


        Tutorial newTutorial = new Tutorial(null,
                titleField.getText().trim(),
                descriptionField.getText().trim(),
                publishedCheck.isSelected());

        try {
            saveButton.setDisable(true);
            statusLabel.setText("⏳ Saving...");

            tutorialService.saveTutorial(newTutorial);
            refreshTutorials();
            statusLabel.setText("✅ Record saved successfully!");
            onClearForm();

        } catch (Exception e) {
            statusLabel.setText("❌ Save failed: " + e.getMessage());
        } finally {
            saveButton.setDisable(false);
        }
    }

    @FXML
    private void onClearForm() {
        titleField.clear();
        descriptionField.clear();
        publishedCheck.setSelected(false);
        statusLabel.setText("👆 Fill form and click Save!");
    }

    @FXML
    private void onUpdateClick() {
        if (selectedTutorial == null) {
            statusLabel.setText("❌ Select a row first!");
            return;
        }

        if (titleField.getText().trim().isEmpty()) {
            statusLabel.setText("❌ Title is required!");
            return;
        }


        selectedTutorial.setTitle(titleField.getText().trim());
        selectedTutorial.setDescription(descriptionField.getText().trim());
        selectedTutorial.setPublished(publishedCheck.isSelected());

        try {
            updateButton.setDisable(true);
            statusLabel.setText("⏳ Updating...");

            tutorialService.updateTutorial(selectedTutorial);
            refreshTutorials();
            statusLabel.setText("✅ Record updated successfully!");
            onClearForm();

        } catch (Exception e) {
            statusLabel.setText("❌ Update failed: " + e.getMessage());
        } finally {
            updateButton.setDisable(false);
        }
    }



    @FXML
    private void onDeleteClick() {
        if (selectedTutorial == null) {
            statusLabel.setText("❌ Select a row first!");
            return;
        }


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Tutorial");
        alert.setContentText("Are you sure you want to delete:\n\"" +
                selectedTutorial.getTitle() + "\"?");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            try {
                deleteButton.setDisable(true);
                statusLabel.setText("⏳ Deleting...");

                tutorialService.deleteTutorial(selectedTutorial.getId());


                tutorialsTable.getItems().remove(selectedTutorial);
                selectedTutorial = null;
                onClearForm();

                statusLabel.setText("✅ Record deleted successfully!");
                deleteButton.setDisable(true);

            } catch (Exception e) {
                statusLabel.setText("❌ Delete failed: " + e.getMessage());
            }
        }
    }


    @FXML
    void onBooksRefreshClick() {
        List<Book> books = bookService.getAllBooks();
        booksTable.setItems(FXCollections.observableArrayList(books));
        booksStatusLabel.setText("✅ Loaded " + books.size() + " books");
    }

    @FXML
    void onBooksSaveClick() {
        try {
            Book newBook = new Book(
                    booksTitleField.getText(),
                    booksAuthorField.getText(),
                    booksIsbnField.getText(),
                    booksAvailableCheck.isSelected()
            );

            bookService.saveBook(newBook);
            onBooksRefreshClick();
            clearBooksForm();
            booksStatusLabel.setText("✅ New book saved!");
        } catch (Exception e) {
            booksStatusLabel.setText("❌ Save failed: " + e.getMessage());
        }
    }

    @FXML
    void onBooksUpdateClick() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            booksStatusLabel.setText("👆 Select a book first");
            return;
        }

        try {
            selected.setTitle(booksTitleField.getText());
            selected.setAuthor(booksAuthorField.getText());
            selected.setIsbn(booksIsbnField.getText());
            selected.setAvailable(booksAvailableCheck.isSelected());

            bookService.updateBook(selected);
            onBooksRefreshClick();
            booksStatusLabel.setText("✅ Book updated!");
        } catch (Exception e) {
            booksStatusLabel.setText("❌ Update failed: " + e.getMessage());
        }
    }

    @FXML
    void onBooksDeleteClick() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            booksStatusLabel.setText("👆 Select a book first");
            return;
        }

        try {
            bookService.deleteBook(selected.getId());
            onBooksRefreshClick();
            clearBooksForm();
            booksStatusLabel.setText("✅ Book deleted!");
        } catch (Exception e) {
            booksStatusLabel.setText("❌ Delete failed: " + e.getMessage());
        }
    }

    @FXML
    void onBooksClearForm() {
        clearBooksForm();
        booksTable.getSelectionModel().clearSelection();
        booksStatusLabel.setText("📝 Form cleared");
    }


    private void clearBooksForm() {
        booksTitleField.clear();
        booksAuthorField.clear();
        booksIsbnField.clear();
        booksAvailableCheck.setSelected(false);
    }


}