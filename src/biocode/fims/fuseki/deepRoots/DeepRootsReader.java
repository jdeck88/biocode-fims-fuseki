package biocode.fims.fuseki.deepRoots;

import biocode.fims.bcid.ExpeditionMinter;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A sample for reading a deepLinks data file in JSON.  The following libraries are required:
 * commons-beanutils-1.8.3.jar
 * commons-collections-3.2.1.jar
 * commons-lang-2.6.jar
 * commons-logging-1.1.jar
 * ezmorph-1.0.6.jar
 * json-lib-2.4-jdk15.jar
 */
public class DeepRootsReader {

    public DeepRoots createRootData(Integer projectId, String expeditionCode) throws Exception {
        // Create the deepLinks.rootData Class
        DeepRoots rootData = new DeepRoots(projectId, expeditionCode);
        // Get deepLinks json object
        ExpeditionMinter expeditionMinter = new ExpeditionMinter();
        JSONObject metadata;
        try {
             metadata = expeditionMinter.getMetadata(projectId, expeditionCode);

            // Get todays's date
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            rootData.setDescription((String) metadata.get("expeditionTitle"));
            rootData.setDate(dateFormat.format(date));
            rootData.setShortName((String) metadata.get("expeditionCode"));
            //System.out.println("projectId = " + projectId + "; expeditionCode = " + expeditionCode);
            //System.out.println("metadata JSON=" + metadata.toString());
        } catch (Exception e) {
            //System.out.println("ERROR getting metadata ... " + e.getMessage() );
            throw new Exception(e);
        }

        // TODO pass in the deepRoots data from the frontend class
        ArrayList<JSONObject> datasets = null;
        try {
            datasets = expeditionMinter.getDatasets(Integer.valueOf(metadata.get("expeditionId").toString()));
        } catch (Exception e) {
            //System.out.println("ERROR getting datasets ... " + e.getMessage() + "; " + metadata.get("expeditionId") + "; "+ metadata.get("expeditionCode"));
            throw new Exception(e);
        }

        try {
            expeditionMinter.close();
            // Create the Hashmap to store in the deepLinks.rootData class
            HashMap<String, String> data = new HashMap<String, String>();
            // Loop the data elements
            for (Object d : datasets) {
                JSONObject dataObject = (JSONObject) d;
                String alias = (String) dataObject.get("title");
                String identifier = (String) dataObject.get("identifier");
                data.put(alias, identifier);
            }
            rootData.setData(data);
            // Assign the actual data to the deepLinks.rootData element
        } catch (Exception e){
            //System.out.println("ERROR assigning root data ...  "+ e.getMessage());
            throw new Exception(e);
        }

        return rootData;
    }

    /**
     * Main method used for local testing
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DeepRootsReader reader = new DeepRootsReader();
        // Some path name to the file
        String filePath = "file:////Users/jdeck/IdeaProjects/bcid/src/deepRoots/test.json";
        // Creating the object
       DeepRoots rootData = reader.createRootData(5,"TEST214");
        // Output for testing
        System.out.println(rootData.toString());
    }

}
