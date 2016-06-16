

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by oscarr on 6/7/16.
 */
public class Organization {
    private String id;
    private String name;
    private String commonName;
    private String country;
    private String sector;
    private String email;
    private String phone;
    private String address;
    private String zipCode;
    private String poBox;
    private String city;
    private String website;
    private String profile;
    private String organizationType;
    private String topLevelOrganizationId;
    private long revision;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public static Organization parse(JSONObject object){
        try {
            Organization organization = new Organization();
            organization.setId(object.getString("id"));
            organization.setName(object.getString("name"));
            organization.setCommonName(object.getString("commonName"));
            organization.setCountry(object.getString("country"));
            organization.setSector(object.getString("sector"));
            organization.setEmail(object.getString("email"));
            organization.setPhone(object.getString("phone"));
            organization.setAddress(object.getString("address"));
            organization.setZipCode(object.getString("zipCode"));
            organization.setPoBox(object.getString("poBox"));
            organization.setCity(object.getString("city"));
            organization.setWebsite(object.getString("website"));
            organization.setProfile(object.getString("profile"));
            organization.setOrganizationType(object.getString("organizationType"));
            organization.setTopLevelOrganizationId(object.getString("topLevelOrganizationId"));
            organization.setRevision(object.getLong("revision"));
            return organization;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static List<Organization> parse(JSONArray sessionsArray){
        List<Organization> organization = new ArrayList<>();
        try {
            int size = sessionsArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject orgJson = sessionsArray.getJSONObject(i);
                organization.add(parse(orgJson));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return organization;
        }
    }
}
