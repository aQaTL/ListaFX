package list;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

public class MainViewController
{
    private DataService service;

    @FXML
    private ListView<ListEntry> entriesList;
    @FXML
    private Spinner episodeSpinner;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<ListEntry.Tag> tagsList;
    @FXML
    private Button websiteButton;

    public void init(DataService service)
    {
        this.service = service;

        episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));

        tagsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        entriesList.getItems().addAll(service.getEntries());
        entriesList.getSelectionModel().selectFirst();
        updateEntryDetails();
    }

    @FXML
    public void updateEntryDetails()
    {
        ListEntry selectedEntry = entriesList.getSelectionModel().getSelectedItem();

        if (selectedEntry != null)
        {
            descriptionArea.setText(selectedEntry.getDescription());
            tagsList.getItems().setAll(selectedEntry.getTags());
            episodeSpinner.getEditor().setText(Integer.toString(selectedEntry.getEpisodeNumber()));
        }
    }

    @FXML
    private void openWebsite()
    {
        try
        {
            Desktop.getDesktop().browse(entriesList.getSelectionModel().getSelectedItem().getWebsite().toURI());
        }
        catch (IOException | URISyntaxException e)
        {
            System.err.println("Website couldn't be opened!");

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setContentText("Website couldn't be opened! Check address.");
            error.showAndWait();
        }
    }

    @FXML
    private void save()
    {
        service.save();
    }

    @FXML
    private void showAboutDialog()
    {
        System.err.println("Not implemented yet");
        //TODO About dialog
    }

    /**
     * Removes everything that isn't a number from episodeSpinner editor
     */
    @FXML
    private void checkSpinner(KeyEvent event)
    {
        try
        {
            entriesList.getSelectionModel().getSelectedItem().setSeriesEpisodes(Integer.parseInt(episodeSpinner.getEditor().getText()));
        }
        catch (NumberFormatException e)
        {
            episodeSpinner.getEditor().setText(episodeSpinner.getEditor().getText().replace(event.getText(), ""));
        }
    }

    @FXML
    private void addEntry()
    {
        ListEntry newListEntry = service.showDialogAndGetNewEntry();

        if(newListEntry != null)
        {
            service.getEntries().add(newListEntry);
            entriesList.getItems().add(newListEntry);
        }
    }

    @FXML
    private void editEntry()
    {
        ListEntry editedListEntry = service.showDialogAndEditEntry(entriesList.getSelectionModel().getSelectedItem());

        if(editedListEntry != null)
        {
            entriesList.getSelectionModel().getSelectedItem().setSeriesTitle(editedListEntry.getSeriesTitle());
            entriesList.getSelectionModel().getSelectedItem().setSeriesEpisodes(editedListEntry.getEpisodeNumber());
            entriesList.getSelectionModel().getSelectedItem().setWebsite(editedListEntry.getWebsite());
            entriesList.getSelectionModel().getSelectedItem().setTags(editedListEntry.getTags());
            entriesList.getSelectionModel().getSelectedItem().setDescription(editedListEntry.getDescription());
            entriesList.refresh();
            updateEntryDetails();

            service.getEntries().set(entriesList.getSelectionModel().getSelectedIndex(), editedListEntry);
        }
    }

    @FXML
    private void deleteEntry()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Deleting " + entriesList.getSelectionModel().getSelectedItem().getSeriesTitle() + " from list");
        alert.setContentText("Are you sure?");
        alert.setTitle("Confirm");
        alert.showAndWait();

        if(alert.getResult().getButtonData().isDefaultButton())
        {
            service.getEntries().remove(entriesList.getSelectionModel().getSelectedItem());
            entriesList.getItems().remove(entriesList.getSelectionModel().getSelectedItem());
        }
    }
}
