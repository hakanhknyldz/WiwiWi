package wiwiwi.io.wearwithweather.pojo;

/**
 * Created by dilkom-hak on 25.05.2016.
 */
public class UserDetails {
    String name;
    String surname;
    String username;
    String passwd;
    String genderType;

    public UserDetails()
    {

    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
