package parimal.examples.flickrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotosList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context mContext,List<Photo> mPhotosList) {
        this.mPhotosList = mPhotosList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //called by layout when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        //called by the layout manager when it wants new data in an existing row

        if(mPhotosList==null || mPhotosList.size()==0){
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText("No photo match your search.\n\nUse the search icon to search for photos.");
        }
        else{
            Photo photoItem = mPhotosList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " " + position);

            Picasso.with(mContext).load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);
            holder.title.setText(photoItem.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return ((mPhotosList!=null) && (mPhotosList.size()!=0)?mPhotosList.size():1);
    }

    void loadNewData(List<Photo> newPhotos){
        mPhotosList=newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return ((mPhotosList!=null) && (mPhotosList.size()!=0)?mPhotosList.get(position):null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail=null;
        TextView title=null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);
            this.title=(TextView)itemView.findViewById(R.id.title);
                    
        }
    }

}
