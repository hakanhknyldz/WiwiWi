package wiwiwi.io.wearwithweather.adapters;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Date;

import wiwiwi.io.wearwithweather.R;
import wiwiwi.io.wearwithweather.network.VolleyApplication;
import wiwiwi.io.wearwithweather.pojo.AnimationUtil;
import wiwiwi.io.wearwithweather.pojo.WeatherResult;
import wiwiwi.io.wearwithweather.pojo.wiClothes;


public class WearAdapter extends RecyclerView.Adapter<WearAdapter.MyViewHolder> {

    final VolleyApplication volleyApplication = VolleyApplication.getInstance();
    private Context context;
    private ImageLoader imageLoader;
    private ArrayList<wiClothes> wiClothesList;
    private LayoutInflater inflater;
    private int previousPosition = 0;
    Image clothesImage;
    public WearAdapter(Context context, ArrayList<wiClothes> wiClothesList )
    {
        this.context = context;
        this.wiClothesList = wiClothesList;

        inflater = LayoutInflater.from(context);
        volleyApplication.init(context);
       // imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View view = inflater.inflate(R.layout.wear_item_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        wiClothes current = wiClothesList.get(position);

        String wiPath = current.getWiPath();
        final String wiUrl  = current.getWiUrl();
        String genderId = current.getGenderId();
        String catId = current.getCatId();

        final String img_url = "http://wiwiwi.somee.com/images/" + wiPath;
        volleyApplication.getImageLoader().get(img_url, ImageLoader.getImageListener(myViewHolder.ivClothesImage,R.drawable.ic_action_trending_orange,R.drawable.error));
/*
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ic_loading_deneme)
                .showImageOnFail(R.drawable.ic_loading_deneme)
                .showImageOnLoading(R.drawable.ic_image_loading).build();

        imageLoader.displayImage(img_url,myViewHolder.ivClothesImage,options);
*/
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
                //imageLoader.displayImage(img_url,iv_clothes_dialog,options);


               volleyApplication.getImageLoader().get(img_url, ImageLoader.getImageListener(iv_clothes_dialog, R.drawable.ic_action_trending_orange, R.drawable.error));

                Button btnGoMarket_clothes_dialog = (Button) dialog.findViewById(R.id.btnGoMarket_clothes_dialog);
                btnGoMarket_clothes_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent internetIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(wiUrl));
                        internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
                        internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(internetIntent);
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return wiClothesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView ivClothesImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivClothesImage = (ImageView) itemView.findViewById(R.id.iv_ClothesImage);

        }
    }


    // This removes the data from our Dataset and Updates the Recycler View.
    private void removeItem(wiClothes infoData) {

        int currPosition = wiClothesList.indexOf(infoData);
        wiClothesList.remove(currPosition);
        notifyItemRemoved(currPosition);
    }

    // This method adds(duplicates) a Object (item ) to our Data set as well as Recycler View.
    private void addItem(int position, wiClothes infoData) {

        wiClothesList.add(position, infoData);
        notifyItemInserted(position);
    }
}
