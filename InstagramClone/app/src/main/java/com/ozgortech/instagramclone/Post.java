package com.ozgortech.instagramclone;

public class Post {

    private String username;
    private String comment;
    private String ImageUrl;

    public Post(String username, String comment, String imageUrl) {
        this.username = username;
        this.comment = comment;
        ImageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
