package tbject.com.smstocalendar.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class SmsEventAdapter extends RecyclerView.Adapter<SmsEventAdapter.SmsEventViewHolder> {

    private List<SmsEvent> contactList;

    public SmsEventAdapter(List<SmsEvent> contactList) {

        this.contactList = contactList;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(SmsEventViewHolder contactViewHolder, int i) {
        SmsEvent smsEvent = contactList.get(i);
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm");
        contactViewHolder.dateValue.setText(dateFormat.format(smsEvent.getDate()));
        contactViewHolder.placeValue.setText(smsEvent.getAddress());
        contactViewHolder.title.setText(smsEvent.getTitle());
    }

    @Override
    public SmsEventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new SmsEventViewHolder(itemView);
    }

    public static class SmsEventViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected  TextView place;
        protected TextView placeValue;
        protected  TextView date;
        protected TextView dateValue;
        public SmsEventViewHolder(View v) {
            super(v);
            place=(TextView)v.findViewById(R.id.placeName);
            placeValue=(TextView) v.findViewById(R.id.placeValue);
            date =  (TextView)  v.findViewById(R.id.dateName);
            dateValue =(TextView)  v.findViewById(R.id.dateValue);
            title = (TextView) v.findViewById(R.id.title);
        }
    }
}