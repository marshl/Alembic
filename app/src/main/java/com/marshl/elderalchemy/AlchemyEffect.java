package com.marshl.elderalchemy;

import android.os.Parcel;
import android.os.Parcelable;

public class AlchemyEffect implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AlchemyEffect createFromParcel(Parcel in) {
            return new AlchemyEffect(in);
        }

        public AlchemyEffect[] newArray(int size) {
            return new AlchemyEffect[size];
        }
    };

    private final String code;
    private final String image;
    private final String name;

    private boolean canBeCrafted = false;

    AlchemyEffect(String code, String name, String image) {
        this.code = code;
        this.name = name;
        this.image = image;
    }

    private AlchemyEffect(Parcel in) {
        super();
        this.code = in.readString();
        this.name = in.readString();
        this.image = in.readString();
    }

    String getCode() {
        return code;
    }

    String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    void setCanBeCrafted(boolean canBeCrafted) {
        this.canBeCrafted = canBeCrafted;
    }

    boolean getCanBeCrafted() {
        return this.canBeCrafted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.code);
        parcel.writeString(this.name);
        parcel.writeString(this.image);
    }

}
