package edu.upc.eetac.dsa.lastsurvivorfrontend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import edu.upc.eetac.dsa.lastsurvivorfrontend.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    //Repo List
    private List<Message> messageList;
    // Adding Listener to call it from Main Activity
    private MessageAdapter.OnItemClickListener mListener;
    //Through this we get the click and the position to our main activity
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    //for When we call the OnClick Method from main
    public void SetOnItemClickListener(MessageAdapter.OnItemClickListener listener){
        mListener = listener ;
    }
    // Provide a reference to the views for each data item, Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in our viewHolder
        public TextView username;
        public TextView comment;
        public ImageView imageIcon;
        public View layout;

        public ViewHolder(View itemView, MessageAdapter.OnItemClickListener listener) {
            super(itemView);
            layout = itemView;
            comment = itemView.findViewById(R.id.secondLine);
            username = itemView.findViewById(R.id.firstLine);
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
    public MessageAdapter(List<Message> myDataset)
    {
        messageList = myDataset;
    }

    // Create new views (invoked by the layout manager)

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from( parent.getContext());
        View v = inflater.inflate(R.layout.forum_layout, parent, false);
        // set the view's size, margins, padding and layout parameters
        MessageAdapter.ViewHolder vh = new MessageAdapter.ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // final String name = values.get(position);
        Message message = messageList.get(position);
        holder.username.setText(message.getUsername());
        holder.comment.setText(String.valueOf(message.getMessage()));
        if(!message.getAvatar().equals("basicAvatar")){
            Bitmap bitmapImg  = StringToBitmap(message.getAvatar());
            holder.imageIcon.setImageBitmap(bitmapImg);
        }else{
            //Drawable myDrawable = getResources().getDrawable(R.drawable.sword_png_icon_20);
            Bitmap icon = BitmapFactory.decodeResource(holder.imageIcon.getResources(),R.drawable.userlogo);
            icon = getResizedBitmap(icon,200,200);
            holder.imageIcon.setImageBitmap(icon);
        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    private Bitmap StringToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
