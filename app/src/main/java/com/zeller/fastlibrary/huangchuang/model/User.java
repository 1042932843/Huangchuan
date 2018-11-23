/*
 * Copyright © 2015 珠海云集软件科技有限公司.
 * Website：http://www.YunJi123.com
 * Mail：dev@yunji123.com
 * Tel：+86-0756-8605060
 * QQ：340022641(dove)
 * Author：dove
 */

package com.zeller.fastlibrary.huangchuang.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 用户登录信息
 */
public class User {
    @PrimaryKey(AssignType.BY_MYSELF)
private String position;
private String birthday;
private String sex;
private String phone;
private String weight;
private String nativePlace;
private String idcard;
private String birthdayPublic;
private String weightPublic;
private String departmentName;
private String homePhone;
private String picture;
private String nativePlacePublic;
private String username;
private String homePhonePublic;
private String height;
private String idcardPublic;
private String phonePublic;
private String areaName;
private String address;
private String eMail;
private String realName;
private String uuid;
private String eMailPublic;
private String heightPublic;
private String joinPartyDate;
private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getBirthdayPublic() {
        return birthdayPublic;
    }

    public void setBirthdayPublic(String birthdayPublic) {
        this.birthdayPublic = birthdayPublic;
    }

    public String getWeightPublic() {
        return weightPublic;
    }

    public void setWeightPublic(String weightPublic) {
        this.weightPublic = weightPublic;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNativePlacePublic() {
        return nativePlacePublic;
    }

    public void setNativePlacePublic(String nativePlacePublic) {
        this.nativePlacePublic = nativePlacePublic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHomePhonePublic() {
        return homePhonePublic;
    }

    public void setHomePhonePublic(String homePhonePublic) {
        this.homePhonePublic = homePhonePublic;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getIdcardPublic() {
        return idcardPublic;
    }

    public void setIdcardPublic(String idcardPublic) {
        this.idcardPublic = idcardPublic;
    }

    public String getPhonePublic() {
        return phonePublic;
    }

    public void setPhonePublic(String phonePublic) {
        this.phonePublic = phonePublic;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String geteMailPublic() {
        return eMailPublic;
    }

    public void seteMailPublic(String eMailPublic) {
        this.eMailPublic = eMailPublic;
    }

    public String getHeightPublic() {
        return heightPublic;
    }

    public void setHeightPublic(String heightPublic) {
        this.heightPublic = heightPublic;
    }

    public String getJoinPartyDate() {
        return joinPartyDate;
    }

    public void setJoinPartyDate(String joinPartyDate) {
        this.joinPartyDate = joinPartyDate;
    }
}
