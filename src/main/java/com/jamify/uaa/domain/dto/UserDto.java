package com.jamify.uaa.domain.dto;

import java.util.List;

public class UserDto {
    private String name;
    private String email;
    private String role;

    private List<String> playlists;
    private List<String> events;
    private List<String> jams;
    private List<String> badges;

    public UserDto() {
    }

    public UserDto(String name, String email, String role, List<String> playlists, List<String> events, List<String> jams, List<String> badges) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.playlists = playlists;
        this.events = events;
        this.jams = jams;
        this.badges = badges;
    }


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

    public List<String> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<String> playlists) {
        this.playlists = playlists;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getJams() {
        return jams;
    }

    public void setJams(List<String> jams) {
        this.jams = jams;
    }

    public List<String> getBadges() {
        return badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
    }
}
