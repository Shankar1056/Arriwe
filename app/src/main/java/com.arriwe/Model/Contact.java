package com.arriwe.Model;

/**
 * Created by Abhi1 on 17/08/15.
 */
public  class Contact {



   String contactName;
   String contactNumber;
   String contactPhoto;
   String contactLocation;

    public Contact(){
    }
    public Contact(String contactName, String contactNumber,String contactPhoto,String contactLocation){
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactPhoto = contactPhoto;
        if(contactLocation.equals("") || contactLocation.equals("null"))
            this.contactLocation = "unknown";
        else
            this.contactLocation = contactLocation;

    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactPhoto() {
        return contactPhoto;
    }

    public void setContactPhoto(String contactPhoto) {
        this.contactPhoto = contactPhoto;
    }

    public String getContactLocation() {
        return contactLocation;
    }

    public void setContactLocation(String contactLocation) {
        this.contactLocation = contactLocation;
    }
}