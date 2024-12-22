package com.jamify.uaa.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class UserEntity extends AbstractEntity<Long> {

    private @NotNull String name;
    private @NotNull String email;
    private String role;

    private String country;
    private String provider;
    private String providerId;

    @Column(length = 1024) // for long imgurl (eg. spotify)
    private String imgUrl;

//    private @ElementCollection List<String> playlists;
//    private @ElementCollection List<String> events;
//    private @ElementCollection List<String> jams;
//    private @ElementCollection List<String> badges;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

//    public List<String> getPlaylists() {
//        return playlists;
//    }
//
//    public void setPlaylists(List<String> playlists) {
//        this.playlists = playlists;
//    }
//
//    public List<String> getEvents() {
//        return events;
//    }
//
//    public void setEvents(List<String> events) {
//        this.events = events;
//    }
//
//    public List<String> getJams() {
//        return jams;
//    }
//
//    public void setJams(List<String> jams) {
//        this.jams = jams;
//    }
//
//    public List<String> getBadges() {
//        return badges;
//    }
//
//    public void setBadges(List<String> badges) {
//        this.badges = badges;
//    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", country='" + country + '\'' +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
//                ", playlists=" + playlists.size() +
//                ", events=" + events.size() +
//                ", jams=" + jams.size() +
//                ", badges=" + badges.size() +
                '}';
    }
}
