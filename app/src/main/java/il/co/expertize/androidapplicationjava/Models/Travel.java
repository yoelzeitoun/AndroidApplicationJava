package il.co.expertize.androidapplicationjava.Models;

public class Travel {

    private String travelId = "id";
    private String clientName;
    private String clientPhone;
    private String clientEmail;

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) { this.travelId = travelId;}

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}

