package com.example.gettingstarted;

import android.widget.TextView;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.content.Context;

public class GetPropertyRecyclerViewAdapater extends RecyclerView.Adapter<GetPropertyRecyclerViewAdapater.ViewHolder>
{
    private Property[] propertyDataSet;
    private Context toastActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final ImageView image;
        public final TextView bedrooms;
        public final TextView bathrooms;
        public final TextView garages;
        public final TextView landsize;
        public final TextView price;
        //public final TextView description;
        public final TextView location;

        public ViewHolder(View v)
        {
            super(v);

            image = (ImageView)v.findViewById(R.id.image_property_home);
            bedrooms = (TextView)v.findViewById(R.id.bedrooms_property_home);
            bathrooms = (TextView)v.findViewById(R.id.bathrooms_property_home);
            garages = (TextView)v.findViewById(R.id.garages_property_home);
            landsize = (TextView)v.findViewById(R.id.area_property_home);
            price = (TextView)v.findViewById(R.id.price_property_home);
            //description = (TextView)v.findViewById(R.id.description_property_home);
            location = (TextView)v.findViewById(R.id.location_property_home);
        }
    }

    public GetPropertyRecyclerViewAdapater(Property[] _propertyDataSet, Context _toastActivity)
    {
        toastActivity = _toastActivity;
        propertyDataSet = _propertyDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        holder.image.setImageBitmap(propertyDataSet[position].imageBitmap);
        holder.bedrooms.setText(propertyDataSet[position].bedrooms);
        holder.bathrooms.setText(propertyDataSet[position].bathrooms);
        holder.garages.setText(propertyDataSet[position].garages);
        holder.landsize.setText(propertyDataSet[position].landSize);
        holder.price.setText(propertyDataSet[position].price);
        holder.location.setText(propertyDataSet[position].location);
    }

    @Override
    public int getItemCount()
    {
        return propertyDataSet.length;
    }
}
