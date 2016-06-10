package list;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import list.dialogs.entryDialogController;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author maciej
 *         <p>
 *         This class provides a XML data loading mechanism
 */
public class DataService
{
    private final String programDataPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "program_data.dat";
    private final String password = getPassword();
    private String userEncodedLogin, username;
    public String malAddress = "http://myanimelist.net/";
    private long userID;

    private ArrayList<ListEntry> entries;

    public DataService(String encodedLogin) throws IOException
    {
        /*
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(programDataPath)))
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
        */
        userEncodedLogin = encodedLogin;

        String userListAddress = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime";
        Document userListDocument = Jsoup.connect(userListAddress).get();

        Elements entries = userListDocument.getElementsByTag("anime");
        for (Element entry : entries)
        {
            ListEntry listEntry = new ListEntry();


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
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(programDataPath)))
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
     * <p>
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
        return null;
    }

    /**
     * Searches for entries that match to given title
     *
     * @param seriesTitle title of the sought anime
     * @return array of ListEntry that have been found
     */
    public ListEntry[] searchForEntries(String seriesTitle)
    {
        return null;
    }


    //Adds new entry to user's list
    public void addEntryToMAL(String encodedLogin, long id, int initEpisode)
    {
        try
        {
            String address = "http://myanimelist.net/api/animelist/add/" + id + ".xml";
            String entryToAdd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<entry>" +
                    "<episode>" + initEpisode + "</episode>" +
                    "<status>1</status>" +
                    "<score>0</score>" +
                    "</entry>";

            Document addAnimeDocument = Jsoup.connect(address).data("data", entryToAdd).header("Authorization", encodedLogin).post();

            if (addAnimeDocument.title().contains("Created"))
            {
                System.out.println("Added successfully");
            }
        }
        catch (HttpStatusException e)
        {
            System.err.println("Couldn't add. STATUS=" + e.getStatusCode());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteEntryFromMAL(long id)
    {
        try
        {
            String address = malAddress + "api/animelist/delete/" + id + ".xml";
            Jsoup.connect(address).header("Authorization", userEncodedLogin).get();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void updateEntryToMAL()
    {

    }
}
