package wiwiwi.io.wearwithweather.adapters;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.github.pwittchen.weathericonview.WeatherIconView;

import java.util.ArrayList;
import java.util.Date;

import wiwiwi.io.wearwithweather.pojo.AnimationUtil;
import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.pojo.WeatherResult;
import wiwiwi.io.wearwithweather.pojo.wiClothes;
import wiwiwi.io.wearwithweather.weather_utils.Weather_Wizard;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {

    final VolleyApplication volleyApplication = VolleyApplication.getInstance();
    private Context context;
    private ArrayList<WeatherResult> weatherArray;
    private LayoutInflater inflater;
    private int previousPosition = 0;
    WeatherResult currentWeather;
    Date date;
    String temperature, title, description;
    Image weatherIcon, clothesImage;
    final String CELCIUS  = "\u2103";

    public WeatherAdapter(Context context, ArrayList<WeatherResult> weatherArray )
    {
        this.context = context;
        this.weatherArray = weatherArray; //includes 4 object of weather
        //First object, current weather details
        //2. , 3. ve 4. objects give us +3,+6,+9 hours later weather details..
        inflater = LayoutInflater.from(context);

        volleyApplication.init(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.weather_item_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        Double temp = weatherArray.get(position).getTemp();
        int currentTemp = temp.intValue();
        String temperature = currentTemp + " " + CELCIUS;


        myViewHolder.date.setText(weatherArray.get(position).getDate().toString());
        myViewHolder.title.setText(weatherArray.get(position).getTitle().toString());
        myViewHolder.temperature.setText(temperature);
        myViewHolder.description.setText(weatherArray.get(position).getComment().toString());
        //myViewHolder.weatherIcon.setIconResource(Resources.getSystem().getString(R.string.clo));
       // myViewHolder.weatherIcon.setIconResource(Resources.getSystem().getString(R.string.wi_day_sunny));

        String descForIcon = weatherArray.get(position).getDescription();
        String iconCode = "";
        //SUNNY

        if(descForIcon.equals(Weather_Wizard.CLEAR_SKY))
        {
            if(weatherArray.get(position).getTemp() < 18)
            {
                iconCode = context.getString(R.string.wi_day_sunny_overcast);

            }
            else
            {
                iconCode = context.getString(R.string.wi_day_sunny);

            }
        }

        //CLOUDY
        else if(descForIcon.equals(Weather_Wizard.FEW_CLOUDS))
        {
            iconCode = context.getString(R.string.wi_day_cloudy);
        } else if(descForIcon.equals(Weather_Wizard.SCATTERED_CLOUDS))
        {
            iconCode = context.getString(R.string.wi_day_cloudy_high);
        } else if(descForIcon.equals(Weather_Wizard.BROKEN_CLOUDS) || descForIcon.equals(Weather_Wizard.OVERCAST_CLOUDS))
        {
            iconCode = context.getString(R.string.wi_day_cloudy_windy);
        }
        //RAIN
        else if(descForIcon.equals(Weather_Wizard.LIGHT_RAIN) || descForIcon.equals(Weather_Wizard.MODERATE_RAIN) || descForIcon.equals(Weather_Wizard.LIGHT_INTENSITY_SHOWER_RAIN))
        {
            iconCode = context.getString(R.string.wi_day_rain);
        } else if(descForIcon.equals(Weather_Wizard.HEAVY_INTENSITY_RAIN) || descForIcon.equals(Weather_Wizard.VERY_HEAVY_RAIN) || descForIcon.equals(Weather_Wizard.EXTREME_RAIN) || descForIcon.equals(Weather_Wizard.SHOWER_RAIN))
        {
            iconCode = context.getString(R.string.wi_day_rain_wind);
        }
        else if(descForIcon.equals(Weather_Wizard.RAGGED_SHOWER_RAIN) || descForIcon.equals(Weather_Wizard.HEAVY_INTENSITY_SHOWER_RAIN) || descForIcon.equals(Weather_Wizard.FREEZING_RAIN))
        {
            iconCode = context.getString(R.string.wi_day_rain_mix);
        }
        //SNOW
        else if(weatherArray.get(position).getTitle().equals("Snow"))
        {
            iconCode = context.getString(R.string.wi_day_snow);
        }

        myViewHolder.weatherIcon.setIconResource(iconCode);


        String weatherIconId  = weatherArray.get(position).getWeatherIconId();
        String iconUrl = "http://openweathermap.org/img/w/"+weatherIconId+".png";
        //volleyApplication.getImageLoader().get(iconUrl, ImageLoader.getImageListener(myViewHolder.ivClothesImage,R.drawable.loading,R.drawable.error));

        String Image_Path = "http://wiwiwi.somee.com/images/";
        String Image_Name = weatherArray.get(position).getWiClothesObject().getWiPath();
        final String img_url = Image_Path + Image_Name;
        volleyApplication.getImageLoader().get(img_url,ImageLoader.getImageListener(myViewHolder.ivClothesImage,R.drawable.ic_action_trending_orange,R.drawable.error));


        final wiClothes currentClothes = weatherArray.get(position).getWiClothesObject();
        final String url_of_image = currentClothes.getWiUrl();

        //icon url from internet
     //   imageCurrent.ImageUrl = string.Format("http://openweathermap.org/img/w/{0}.png", weatherCurrent.Icon);

        //myViewHolder.textview.setText(data.get(position).title);
        //myViewHolder.imageView.setImageResource(data.get(position).imageId);

        if(position > previousPosition){ // We are scrolling DOWN

            AnimationUtil.animate(myViewHolder, true);

        }else{ // We are scrolling UP

            AnimationUtil.animate(myViewHolder, false);


        }

        previousPosition = position;


        final int currentPosition = position;


        myViewHolder.ivClothesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.clothes_dialog);
                dialog.setTitle("Seçtiğiniz Ürün...");

                ImageView iv_clothes_dialog = (ImageView) dialog.findViewById(R.id.iv_clothes_dialog);
                volleyApplication.getImageLoader().get(img_url, ImageLoader.getImageListener(iv_clothes_dialog, R.drawable.ic_action_trending_orange, R.drawable.error));

               // iv_clothes_dialog.setImageResource(R.drawable.ic_launcher);

                Button btnGoMarket_clothes_dialog = (Button) dialog.findViewById(R.id.btnGoMarket_clothes_dialog);
                btnGoMarket_clothes_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(currentClothes.getWiUrl()));
                        internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
                        internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(internetIntent);
                        dialog.dismiss();
                    }
                });
                dialog.show();


              //  Toast.makeText(context, "OnClick Called at position " + position + " , url_of_image: "+ url_of_image, Toast.LENGTH_SHORT).show();
            }
        });
/*
        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(context, "OnLongClick Called at position " + position, Toast.LENGTH_SHORT).show();

                removeItem(infoData);

                return true;
            }


        });
*/

    }

    @Override
    public int getItemCount() {
        return weatherArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        WeatherIconView weatherIcon;
        TextView date,title,temperature,description;
        ImageView ivClothesImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            weatherIcon = (WeatherIconView) itemView.findViewById(R.id.weatherIcon);
            date = (TextView) itemView.findViewById(R.id.tv_wi_Date);
            title = (TextView) itemView.findViewById(R.id.tv_wi_Title);
            temperature= (TextView) itemView.findViewById(R.id.tv_wi_Temp);
            description = (TextView) itemView.findViewById(R.id.tv_wi_Descripton);
            ivClothesImage = (ImageView) itemView.findViewById(R.id.iv_ClothesImage);

        }
    }


    // This removes the data from our Dataset and Updates the Recycler View.
    private void removeItem(WeatherResult infoData) {

        int currPosition = weatherArray.indexOf(infoData);
        weatherArray.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, WeatherResult infoData) {

        weatherArray.add(position, infoData);
        notifyItemInserted(position);
    }
}
