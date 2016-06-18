

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 4/11/16.
 */
public class Participant {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String mobilePhone;
    private String phone;
    private String profile;
    private String countryOfNationality;
    private String[][] socialAccounts;
    private String publicFigure;
    private String organizationId;
    private String organizationName;
    private String organizationType;
    private String topLevelOrganizationId;
    private String position_title;
    private List<ForumNetwork> forumNetworks;
    private String thumbnailUrl;
    private String photoUrl;
    private List<Contribution> contributions;
    private Boolean inBooklet;
    private Long revision;



    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountryOfNationality() {
        return countryOfNationality;
    }

    public void setCountryOfNationality(String countryOfNationality) {
        this.countryOfNationality = countryOfNationality;
    }

    public String getPublicFigure() {
        return publicFigure;
    }

    public void setPublicFigure(String publicFigure) {
        this.publicFigure = publicFigure;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition_title() {
        return position_title;
    }

    public void setPosition_title(String position_title) {
        this.position_title = position_title;
    }

    public boolean isInBooklet() {
        return inBooklet;
    }

    public void setInBooklet(boolean inBooklet) {
        this.inBooklet = inBooklet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[][] getSocialAccounts() {
        return socialAccounts;
    }

    public void setSocialAccounts(String[][] socialAccounts) {
        this.socialAccounts = socialAccounts;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getTopLevelOrganizationId() {
        return topLevelOrganizationId;
    }

    public void setTopLevelOrganizationId(String topLevelOrganizationId) {
        this.topLevelOrganizationId = topLevelOrganizationId;
    }

    public List<ForumNetwork> getForumNetworks() {
        return forumNetworks;
    }

    public void setForumNetworks(List<ForumNetwork> forumNetworks) {
        this.forumNetworks = forumNetworks;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public Boolean getInBooklet() {
        return inBooklet;
    }

    public void setInBooklet(Boolean inBooklet) {
        this.inBooklet = inBooklet;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public static Participant parse(JSONObject object){
        try {
            Participant participant = new Participant();
            participant.setId(object.getString("id"));
            participant.setFirstName(object.getString("firstName"));
            participant.setLastName(object.getString("lastName"));
            participant.setFullName(object.getString("fullName"));
            participant.setProfile(object.getString("profile"));
            participant.setCountryOfNationality(object.getString("countryOfNationality"));

            //socialAccounts
//            JSONObject socialAccountsJson = object.getJSONObject("socialAccounts");
//            String[][] socialAccounts = new String[socialAccountsJson.length()][2];
//            Iterator ite = socialAccountsJson.keys();
//            int pos = 0;
//            while( ite.hasNext() ){
//                socialAccounts[pos][0] = (String) ite.next();
//                socialAccounts[pos][1] = socialAccountsJson.getString( socialAccounts[pos][0] );
//                pos++;
//            }
//            participant.setSocialAccounts(socialAccounts);
            participant.setPublicFigure(object.getString("publicFigure"));
            participant.setOrganizationId(object.getString("organizationId"));
            participant.setOrganizationName(object.getString("organizationName"));
            participant.setOrganizationType(object.getString("organizationType"));
            participant.setTopLevelOrganizationId(object.getString("topLevelOrganizationId"));
            participant.setPosition_title(object.getString("position_title"));

            //forumNetworks
            JSONArray forumNetworksJson = object.getJSONArray("forumNetworks");
            List<ForumNetwork> forumNetworks = new ArrayList<>();
            int size = forumNetworksJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject forumNetJson = forumNetworksJson.getJSONObject(i);
                ForumNetwork forumNetwork = new ForumNetwork();
                forumNetwork.setForumCommunity( forumNetJson.getString("forumCommunity") );
                forumNetwork.setNetwork(forumNetJson.getString("network"));
                forumNetworks.add( forumNetwork );
            }
            participant.setForumNetworks( forumNetworks );
            participant.setThumbnailUrl(object.getString("thumbnailUrl"));
            participant.setPhotoUrl(object.getString("photoUrl"));
            participant.setInBooklet(object.getBoolean("inBooklet"));

            //contributions
            JSONArray contributionsJson = object.getJSONArray("contributions");
            List<Contribution> contributions = new ArrayList<>();
            size = contributionsJson.length();
            for( int i = 0; i < size; i++ ){
                JSONObject contributionJson = contributionsJson.getJSONObject(i);
                Contribution contribution = new Contribution();
                contribution.setSessionId(contributionJson.getString("sessionId"));
                contribution.setType(contributionJson.getString("type"));
                contributions.add( contribution );
            }
            participant.setContributions( contributions );
            participant.setRevision(object.getLong("revision"));
            return participant;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Participant> parse(JSONArray sessionsArray){
        List<Participant> people = new ArrayList<>();
        try {
            int size = sessionsArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject peopleJson = sessionsArray.getJSONObject(i);
                people.add(parse(peopleJson));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return people;
        }
    }
}
