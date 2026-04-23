package com.example.connect_four;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AvatarAdapter extends BaseAdapter {

    private Context context;
    // List of 10 avatar images from the drawable folder
    private int[] avatarImages = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
            R.drawable.avatar9,
            R.drawable.avatar10,
            R.drawable.avatar11,
            R.drawable.avatar12
    };

    public AvatarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return avatarImages.length;
    }

    @Override
    public Object getItem(int position) {
        return avatarImages[position];
    }

    @Override
    public long getItemId(int position) {
        return avatarImages[position]; // Return the resource ID of the avatar
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Set width and height for each image
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(avatarImages[position]);
        return imageView;
    }
}
