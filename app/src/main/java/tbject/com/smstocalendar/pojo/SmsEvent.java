package tbject.com.smstocalendar.pojo;

import java.util.Date;

public class SmsEvent  {
    private String title;
    private String description;
    private String address;
    private Date date;
    private Date dateEnd;
    private String phoneNumber;
    private Date accepted;
    private String addressPattern;


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

    public void setAddress(String address) {
        if (address==null||address.isEmpty())
            address= "";
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
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
                ", address='" + address + '\'' +
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


    public String getAddressPattern() {
        return addressPattern;
    }

    public void setAddressPattern(String addressPattern) {
        this.addressPattern = addressPattern;
    }

}
