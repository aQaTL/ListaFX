package list;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import list.dialogs.entryDialogController;

/**
 * @author maciej
 *
 * This class provides a XML data loading mechanism
 */
public class DataService
{
    private final String programDataPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "program_data.dat";
    private final String password = getPassword();

    private ArrayList<ListEntry> entries;

    public DataService()
    {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(programDataPath)))
        {
            entries = (ArrayList<ListEntry>) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setHeaderText(null);
            info.setContentText("Couldn't find data file. Using empty one.");

            entries = new ArrayList<>();
            info.showAndWait();
        }
    }

    public ArrayList<ListEntry> getEntries()
    {
        return entries;
    }

    private String getPassword()
    {
        try (Scanner s = new Scanner(new File("pass.txt")))
        {
            return s.nextLine().trim();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(DataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void save()
    {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(programDataPath)))
        {
            objectOutputStream.writeObject(entries);
            System.out.println("Saved");
        }
        catch (IOException e)
        {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText(null);
            error.setContentText("Your work couldn't be saved.");
            error.showAndWait();
        }
    }

    public boolean checkPassword(String passwordToCheck)
    {
        return password.equals(passwordToCheck);
    }

    /**
     * Shows a dialog window, which lets user to input data for new ListEntry
     *
     * Check if null, before adding to list, because null is returned when
     * user either cancel or close the window.
     *
     * @return ListEntry with data entered in dialog
     */
    public ListEntry showDialogAndGetNewEntry()
    {
        try
        {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Add new entry");

            FXMLLoader loader = new FXMLLoader(entryDialogController.class.getResource("entryDialogView.fxml"));
            Parent parent = loader.load();

            loader.<entryDialogController>getController().init(dialogStage);

            dialogStage.setScene(new Scene(parent));
            dialogStage.showAndWait();

            return loader.<entryDialogController>getController().getListEntry();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public ListEntry showDialogAndEditEntry(ListEntry entryToEdit)
    {
        try
        {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Edit entry");

            FXMLLoader loader = new FXMLLoader(entryDialogController.class.getResource("entryDialogView.fxml"));
            Parent parent = loader.load();

            loader.<entryDialogController>getController().init(dialogStage, entryToEdit);

            dialogStage.setScene(new Scene(parent));
            dialogStage.showAndWait();

            return loader.<entryDialogController>getController().getListEntry();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return  null;
    }
}
