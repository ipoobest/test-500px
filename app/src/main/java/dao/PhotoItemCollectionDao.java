
package dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.example.jpoobest.poobestcom.view.PhotoListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoItemCollectionDao implements Parcelable {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<PhotoItemDao> data = null;

    public PhotoItemCollectionDao(){

    }

    protected PhotoItemCollectionDao(Parcel in) {
        data = in.createTypedArrayList(PhotoItemDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoItemCollectionDao> CREATOR = new Creator<PhotoItemCollectionDao>() {
        @Override
        public PhotoItemCollectionDao createFromParcel(Parcel in) {
            return new PhotoItemCollectionDao(in);
        }

        @Override
        public PhotoItemCollectionDao[] newArray(int size) {
            return new PhotoItemCollectionDao[size];
        }
    };

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List< PhotoItemDao> getData() {
        return data;
    }

    public void setData(List< PhotoItemDao> data) {
        this.data = data;
    }

}
