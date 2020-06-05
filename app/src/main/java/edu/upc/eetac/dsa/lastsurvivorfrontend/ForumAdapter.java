package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Forum;
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder>  {
    //Repo List
    private List<Forum> forumList;
    private Context mContext;
    // Adding Listener to call it from Main Activity
    private ForumAdapter.OnItemClickListener mListener;
    //Through this we get the click and the position to our main activity
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    //for When we call the OnClick Method from main
    public void SetOnItemClickListener(ForumAdapter.OnItemClickListener listener){
        mListener = listener ;
    }
    // Provide a reference to the views for each data item, Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in our viewHolder
        public TextView title;
        public TextView username;
        public ImageView imageIcon;
        public View layout;

        public ViewHolder(View itemView, ForumAdapter.OnItemClickListener listener) {
            super(itemView);
            layout = itemView;
            username = itemView.findViewById(R.id.secondLine);
            title = itemView.findViewById(R.id.firstLine);
            imageIcon = itemView.findViewById(R.id.avatarImg);
            //Click Handler for the whole Item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if( listener !=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ForumAdapter(List<Forum> myDataset)
    {
        forumList = myDataset;
    }

    // Create new views (invoked by the layout manager)

    @NonNull
    @Override
    public ForumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from( parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, padding and layout parameters
        ForumAdapter.ViewHolder vh = new ForumAdapter.ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // final String name = values.get(position);
        Forum forum = forumList.get(position);
        holder.title.setText(forum.getName());
        holder.username.setText(String.valueOf(forum.getAdmin()));
        if(!forum.getAvatar().equals("basicAvatar")){
            Bitmap bitmapImg  = StringToBitmap(forum.getAvatar());
            holder.imageIcon.setImageBitmap(bitmapImg);
        }else{

        }

    }
    private Bitmap StringToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
    @Override
    public int getItemCount() {
        return forumList.size();
    }
}
