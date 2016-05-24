package wiwiwi.io.wearwithweather.pojo;

import java.util.ArrayList;
import java.util.Date;

public class WeatherResult
{
    public Date Date; // time of result
    public String WeatherIconId; // icon of that weather result;
    public Double Temp; // Current temprature : 25 C , 15 C
    public String Title; // Status of weather ; Güneşli, yağmurlu
    public String Description; // Details of weather; Hava yağmurlu şemsiyeni kap ;) ..
    public String Clothes_Image_Name; // image of clothes..
    public String Comment;

    public Double Humidity;
    public Double Pressure;
    public String CityName;
    public ArrayList<String> ClothesNames;
    public wiClothes wiClothesObject = new wiClothes();


    public  WeatherResult()
    {

    }

    public wiClothes getWiClothesObject() {
        return wiClothesObject;
    }

    public void setWiClothesObject(wiClothes wiClothesObject) {
        this.wiClothesObject = wiClothesObject;
    }

    public void setClothesNames(ArrayList<String> clothesNames) {
        ClothesNames = clothesNames;
    }

    public ArrayList<String> getClothesNames() {
        return ClothesNames;
    }

    public java.util.Date getDate() {
        return Date;
    }
    public void setDate(java.util.Date date) {
        Date = date;
    }

    public String getWeatherIconId() {
        return WeatherIconId;
    }

    public void setWeatherIconId(String weatherIconId) {
        WeatherIconId = weatherIconId;
    }

    public Double getTemp() {
        return Temp;
    }
    public void setTemp(Double temp) {
        Temp = temp;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getClothes_Image_Name() {
        return Clothes_Image_Name;
    }

    public void setClothes_Image_Name(String clothes_Image_Name) {
        Clothes_Image_Name = clothes_Image_Name;
    }

    public Double getHumidity() {
        return Humidity;
    }

    public void setHumidity(Double humidity) {
        Humidity = humidity;
    }

    public Double getPressure() {
        return Pressure;
    }

    public void setPressure(Double pressure) {
        Pressure = pressure;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }
}
