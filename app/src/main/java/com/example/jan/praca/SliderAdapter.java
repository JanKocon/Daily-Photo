package com.example.jan.praca;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Jan on 17.04.2018.
 */

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }
    public int[] slide_images = {
            R.drawable.logo,
            R.drawable.logo2,
            R.drawable.logo3
    };
    public String[] slide_headings = {
            "Witaj!","Ustaw powiadomienia","Połącz się z Dyskiem Google"
    };
    public String[] slide_descriptions = {
      "Aplikacja DailyPhoto pomoże Ci utworzyć dziennik codziennych wydarzeń które następnie przekształci w film poklatkowy",
            "Aby uchwycić codzienne chwilę warto skorzystać z sytemu powiadomień",
            "Nie chcesz utracić wspomnień? Utwórz kopie zapasową!"
    };

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView  headings = (TextView) view.findViewById(R.id.slide_heading);
        TextView descripton = (TextView) view.findViewById(R.id.slide_description);
        slideImageView.setImageResource(slide_images[position]);
        headings.setText(slide_headings[position]);
        descripton.setText(slide_descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
