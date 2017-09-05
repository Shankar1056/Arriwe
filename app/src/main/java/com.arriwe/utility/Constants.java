package com.arriwe.utility;

import com.arriwe.networkmanagers.NetworkDataModel;

import org.json.JSONObject;

/**
 * Created by Abhi1 on 11/08/15.
 */
public class Constants {

    //For Dev
 //   public static String DEV_BASE_URL            = "http://arriwe.tglobalsolutions.com/index.php?r=api/";
 //   public static String DEV_IMG_BASE_URL        = "http://arriwe.tglobalsolutions.com/";

    //For Prod
    public static String DEV_BASE_URL            = "http://35.163.45.85/index.php?r=api/";//"http://35.165.191.94/index.php?r=api/";//"http://ec2-52-41-31-236.us-west-2.compute.amazonaws.com/index.php?r=api/";
    public static String DEV_IMG_BASE_URL        = "http://35.163.45.85/";//"http://ec2-52-41-31-236.us-west-2.compute.amazonaws.com/";





    public static String Api_Register            = "register";
    public static String Api_Verify_No           = "otpconfirm";
    public static String Api_Wayndr_reg_users    = "getarriwecontacts";
    public static String Api_Tagged_Travel_Users = "createtrip";
    public static String Api_Get_User_Activities = "getuseractivity";
    public static String Api_Update_User_Loc     = "updateuserlocation";
    public static String Api_Update_Status       = "updateuseractivities";
    public static String Api_Update_GCM_TOKEN    = "updategoogleappid";
    public static String Api_Cancel_Trip          = "canceltrip";

    public static String Api_Arriwe_Trip          = "completetrip";
    public static String Api_Clear_Trip           = "cleartrip";
    public static String Api_Req_Loc              = "requestlocation";
    public static String Api_Share_Loc              = "sharelocation";
    public static String Api_Reject_Loc_Req              = "rejectrequestlocation";
    public static String Api_Reject_Rcv_Loc              = "deleterequestlocation";
    public static String Api_Get_Nearby_Contacts              = "getnearbycontacts";
    public static String Api_Get_Track                        = "gettrack";
    public static String Api_Delete_Acccount                        = "delete";
    public static String Api_send_User_Location                        = "adduserlocation";
    public static String Api_update_group_traval                        = "addgroupuser";
    public static String Api_delete_group_traval                        = "deletegroupuser";
    public static String Api_cancel_tagger_trip                       = "canceltriptagger";
    public static String Api_cancel_trivvver_trip                       = "canceltriptraveller";
    public static String Api_chnage_city_status                      = "changecitystatus";








    public  static int          YOUR_SELECT_PICTURE_REQUEST_CODE = 11;
    public  static int          CROPPED_PIC_REQUEST_CODE = 12;

    public  static String       VALIDATION_SUCCESS   = "Success";
    public  static String       INVALID_IMG          = "Please select an image";
    public  static String       INVALID_NAME         = "Invalid name";
    public  static String       INVALID_NO           = "Invalid mobile number";
    public  static String       INVALID_VER_CODE     = "Invalid verification code";
    public  static CharSequence ERROR_GETTING_PLACE  = "Unable to get place details";

    //keys
    public  static String       k_VALIDATION_RES   = "validation_res";
    public  static String       k_FAIL_REASON      = "fail_reason";
    public  static String       k_NAME             = "name";
    public  static String       k_MOB_NO           = "mob_no";
    public  static String       k_BITMAP_IMG       = "bitmap";

    public  static String         HTTPMETHOD_GET       = "GET";
    public  static String         HTTPMETHOD_POST      = "POST";
    public  static String         HTTPMETHOD_PUT       = "PUT";

    public  static String         SMS_ORIGIN           = "VK-ARRIWE";
    public  static String         SMS_ORIGIN1           = "VM-ARRIWE";
    public  static String         SMS_ORIGIN2           = "HP-ARRIWE";


    public static boolean islogin=false;
    public  static String myPreviousJSON;
    public  static String mobileNo;
}
