package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Player;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    //Repo List
    private List<Player> playerList;
    //Intent Request ID
    public static final int TRACK_REQUEST = 1;
    // Adding Listener to call it from Main Activity
    private OnItemClickListener mListener;
    //Through this we get the click and the position to our main activity
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    //for When we call the OnClick Method from main
    public void SetOnItemClickListener(OnItemClickListener listener){
        mListener = listener ;
    }
    // Provide a reference to the views for each data item, Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in our viewHolder
        public TextView playerExperience;
        public TextView playerUsername;
        public ImageView imageDelete;
        public View layout;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            layout = itemView;
            playerExperience = itemView.findViewById(R.id.secondLine);
            playerUsername = itemView.findViewById(R.id.firstLine);
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
    public MyAdapter(List<Player> myDataset)
    {
        playerList = myDataset;
    }

    // Create new views (invoked by the layout manager)

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from( parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, padding and layout parameters
        ViewHolder vh = new ViewHolder(v,mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       // final String name = values.get(position);
        holder.playerUsername.setText(playerList.get(position).getUsername());
        holder.playerExperience.setText(playerList.get(position).getExperience());
    }
    @Override
    public int getItemCount() {
        return playerList.size();
    }
}