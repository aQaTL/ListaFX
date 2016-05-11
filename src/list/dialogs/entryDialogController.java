package list.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import list.ListEntry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Maciej on 2016-05-09.
 */
public class entryDialogController
{

    @FXML
    private TextField nameField;
    @FXML
    private Spinner<Integer> episodeSpinner;
    @FXML
    private TextField websiteField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<ListEntry.Tag> tagsList;
    @FXML
    private Button doneButton;
    @FXML
    private Button cancelButton;

    private ListEntry listEntry;
    private Stage stage;

    public void init(Stage stage)
    {
        this.stage = stage;

        episodeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
    }

    public void init(Stage stage, ListEntry listEntry)
    {
        this.stage = stage;

        nameField.setText(listEntry.getName());
        episodeSpinner.getEditor().setText(Integer.toString(listEntry.getEpisodeNumber()));
        websiteField.setText(listEntry.getWebsite().toString());
        tagsList.getItems().setAll(listEntry.getTags());
        descriptionArea.setText(listEntry.getDescription());
    }

    /**
     * Invoked as action for Done button
     */
    @FXML
    private void doneButtonAction()
    {
        listEntry = new ListEntry();
        listEntry.setName(nameField.getText());
        listEntry.setEpisodeCounter(Integer.parseInt(episodeSpinner.getEditor().getText()));
        listEntry.setDescription(descriptionArea.getText());

        ArrayList<ListEntry.Tag> tags = new ArrayList<>(tagsList.getSelectionModel().getSelectedItems().size());
        tagsList.getSelectionModel().getSelectedItems().forEach((tag) -> tags.add(tag));
        listEntry.setTags(tags);

        try
        {
            listEntry.setWebsite(new URL(websiteField.getText()));
        }
        catch (MalformedURLException e)
        {
            //TODO show error dialog (bad URL)
        }
        stage.close();
    }

    @FXML
    private void cancelButtonAction()
    {
        stage.close();
    }

    /**
     * Removes everything that isn't a number from episodeSpinner editor
     */
    @FXML
    private void checkSpinner(KeyEvent event)
    {
        try
        {
            Integer.parseInt(episodeSpinner.getEditor().getText());
        }
        catch (NumberFormatException e)
        {
            episodeSpinner.getEditor().setText(episodeSpinner.getEditor().getText().replace(event.getText(), ""));
        }
    }

    /**
     * Gets listEntry created from values from this dialog fields
     *
     * @return ListEntry; can be null if user close dialog or clicks cancel
     */
    public ListEntry getListEntry()
    {
        return listEntry;
    }

    public TextField getNameField()
    {
        return nameField;
    }

    public Spinner<Integer> getEpisodeSpinner()
    {
        return episodeSpinner;
    }

    public TextField getWebsiteField()
    {
        return websiteField;
    }

    public TextArea getDescriptionArea()
    {
        return descriptionArea;
    }

    public ListView<ListEntry.Tag> getTagsList()
    {
        return tagsList;
    }

}