package layout.face.sidface.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by wildan on 12/17/16.
 */
public class FaceUId
{
    private String albumId;
    private String albumUser;
    private String numbytes;
    private String timeStamp;
    private String users;

    public FaceUId()
    {
    }

    public FaceUId(String paramString1, String paramString2, byte[] paramArrayOfByte, HashMap<String, String> paramHashMap)
    {
        this.albumId = paramString1;
        this.albumUser = paramString2;
        this.timeStamp = setTimeStamp();
        this.numbytes = setNumbytes(paramArrayOfByte);
        this.users = setUsers(paramHashMap);
        Log.d("HASH", paramHashMap.toString());
    }

    public String getAlbumId()
    {
        return this.albumId;
    }

    public String getAlbumUser()
    {
        return this.albumUser;
    }

    public String getNumbytes()
    {
        return this.numbytes;
    }

    public String getTimeStamp()
    {
        return this.timeStamp;
    }

    public String getUsers()
    {
        return this.users;
    }

    public void setAlbumId(String paramString)
    {
        this.albumId = paramString;
    }

    public void setAlbumUser(String paramString)
    {
        this.albumUser = paramString;
    }

    private String setNumbytes(byte[] paramArrayOfByte)
    {
        return Arrays.toString(paramArrayOfByte);
    }

    private String setTimeStamp()
    {
        this.timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return this.timeStamp;
    }

    private String setUsers(HashMap<String, String> paramHashMap)
    {
        return paramHashMap.toString();
    }
}