package tbject.com.smstocalendar.pojo;

import java.util.Date;

public class SmsEvent  {
    private String title;
    private String description;
    private String place;
    private Date date;
    private Date dateEnd;
    private String phoneNumber;
    private Date accepted;
    private boolean timeFound;


    public void setTitle(String title) {
        if (title==null ||title.isEmpty())
            title=" ";
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(String place) {
        if (place==null||place.isEmpty())
            place= " ";
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



    public Date getAccepted() {
        return accepted;
    }

    public void setAccepted(Date accepted) {
        this.accepted = accepted;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "SmsEvent{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", date=" + date + '\'' +
                ", dateEnd=" + dateEnd + '\'' +
                ", phoneNumber="+phoneNumber + '\'' +
                ", accepted="+accepted +
                '}';
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setTimeFound(boolean timeFound) {
        this.timeFound = timeFound;
    }

    public boolean isTimeFound() {
        return timeFound;
    }
}
