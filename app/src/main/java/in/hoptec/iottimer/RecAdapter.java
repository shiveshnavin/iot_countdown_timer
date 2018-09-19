package in.hoptec.iottimer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView ;

import java.util.List;

import static in.hoptec.iottimer.MainActivity.hmsTimeFormatter;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ItemRowHolder> {

    private List<Dummy> postsModelList;
    private Context mContext;
    private String username;


    public RecAdapter(Context mContext, List<Dummy> postsModelList) {
        this.postsModelList = postsModelList;
        this.mContext = mContext;
    }
    public RecAdapter(){

    }
    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_list, viewGroup, false);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }
    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder,  int i) {



        final Dummy item=postsModelList.get(itemRowHolder.getAdapterPosition());
        itemRowHolder.id.setText(item.id);
        itemRowHolder.time.setText(item.text);

        itemRowHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(item);
            }
        });


    }

public void click(Dummy x){}

public static class Dummy
{
    String id;
    String text;

    public Dummy(String id, String text) {
        this.id = id;
        this.text = hmsTimeFormatter(Long.valueOf(text));
    }
}

    @Override
    public int getItemCount() {
        return (null != postsModelList ? postsModelList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        public TextView time;
        public Button id;

        public ItemRowHolder(View view) {
            super(view);

            this.time = (TextView) view.findViewById(R.id.time);
            this.id =(Button)  view.findViewById(R.id.id);
        }
    }
}